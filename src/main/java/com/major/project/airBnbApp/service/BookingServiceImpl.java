package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.BookingDto;
import com.major.project.airBnbApp.dto.BookingRequest;
import com.major.project.airBnbApp.dto.GuestDto;
import com.major.project.airBnbApp.entity.*;
import com.major.project.airBnbApp.entity.enums.BookingStatus;
import com.major.project.airBnbApp.exception.ResourceNotFoundException;
import com.major.project.airBnbApp.exception.UnauthorizedException;
import com.major.project.airBnbApp.repository.*;
import com.major.project.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final guestsRepository guestsRepository;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel : {}, room: {}, date {} - {}", bookingRequest.getHotelId(), bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        log.info("BBhavnish Bhardwajjjjjjj");
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id "+ bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore.");
        }

        // Reserve the room/update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        for(Inventory inventory: inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);



        // Calculate dynamic amount

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        // Create the Booking
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkoutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) throws UnauthorizedException {

        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking is not belongs to current user" + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestsRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) throws UnauthorizedException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+ bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id: "+ user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        String sessionUrl = checkoutService.getCheckoutSession(booking, frontendUrl+"/payments/success", frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session != null){
                String sessionId = session.getId();
                Booking booking = (Booking) bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("Payment is not completed yet! Try again..."));
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckoutDate(), booking.getRoomsCount());
                inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckoutDate(), booking.getRoomsCount());
                log.info("Booking Confirmed for session Id: {}", sessionId);
            }else {
                log.warn("Unhandled event type: {}", event.getType());
            }

        }
    }

    @Override
    public String cancelBooking(Long bookingId) throws UnauthorizedException, StripeException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+ bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnauthorizedException("Booking does not belong to this user with id: "+ user.getId());
        }
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only Confirmed bookings can cancel.");
        }

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckoutDate(), booking.getRoomsCount());
        inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckoutDate(), booking.getRoomsCount());

        // handle Refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundCreateParams);
        }catch (StripeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}































