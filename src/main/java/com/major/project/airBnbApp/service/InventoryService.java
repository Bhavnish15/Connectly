package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.HotelDto;
import com.major.project.airBnbApp.dto.HotelPriceDto;
import com.major.project.airBnbApp.dto.HotelSearchRequest;
import com.major.project.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
