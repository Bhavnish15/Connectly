package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.HotelDto;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    void activateHotel(Long hotelId);
    void DeleteHotelById(Long id);

}
