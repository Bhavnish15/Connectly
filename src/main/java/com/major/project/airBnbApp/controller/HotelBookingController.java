package com.major.project.airBnbApp.controller;

import com.major.project.airBnbApp.dto.BookingDto;
import com.major.project.airBnbApp.dto.BookingRequest;
import com.major.project.airBnbApp.dto.GuestDto;
import com.major.project.airBnbApp.exception.UnauthorizedException;
import com.major.project.airBnbApp.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest bookingRequest){
            return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestDtoList) throws UnauthorizedException {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

}
