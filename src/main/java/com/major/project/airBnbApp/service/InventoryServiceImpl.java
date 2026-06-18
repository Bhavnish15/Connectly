package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.HotelDto;
import com.major.project.airBnbApp.dto.HotelPriceDto;
import com.major.project.airBnbApp.dto.HotelSearchRequest;
import com.major.project.airBnbApp.entity.Hotel;
import com.major.project.airBnbApp.entity.Inventory;
import com.major.project.airBnbApp.entity.Room;
import com.major.project.airBnbApp.repository.HotelMinPriceRepository;
import com.major.project.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; !today.isAfter(endDate); today = today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .city(room.getHotel().getCity())
                    .hotel(room.getHotel())
                    .price(room.getBasePrice())
                    .date(today)
                    .room(room)
                    .closed(false)
                    .bookedCount(0)
                    .reservedCount(0)
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .build();

            inventoryRepository.save(inventory);
        }

    }

    @Override
    public void deleteAllInventories(Room room) {
        log.info("Deleting the inventories of room with id: {}", room.getId());
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels for {} city, from {}, to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<HotelPriceDto> hotelPage = hotelMinPriceRepository.findHotelWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(), dateCount, pageable);

        return hotelPage;
    }
}




































