package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.HotelDto;
import com.major.project.airBnbApp.dto.HotelInfoDto;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    void activateHotel(Long hotelId);
    void DeleteHotelById(Long id);
    List<HotelDto> getAllHotels();

    HotelInfoDto getHoteInfoById(Long hotelId);
}
