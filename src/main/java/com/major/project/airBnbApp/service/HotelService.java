package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.HotelDto;
import com.major.project.airBnbApp.dto.HotelInfoDto;
import com.major.project.airBnbApp.exception.UnauthorizedException;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id) throws UnauthorizedException;
    HotelDto updateHotelById(Long id, HotelDto hotelDto) throws UnauthorizedException;
    void activateHotel(Long hotelId) throws UnauthorizedException;
    void DeleteHotelById(Long id) throws UnauthorizedException;
    List<HotelDto> getAllHotels();

    HotelInfoDto getHoteInfoById(Long hotelId);
}
