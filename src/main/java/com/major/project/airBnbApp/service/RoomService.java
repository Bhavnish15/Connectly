package com.major.project.airBnbApp.service;


import com.major.project.airBnbApp.dto.RoomDto;
import com.major.project.airBnbApp.exception.UnauthorizedException;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId, RoomDto roomDto) throws UnauthorizedException;
    List<RoomDto> getAllRoomsInHotel(Long hotelId) throws UnauthorizedException;
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId) throws UnauthorizedException;
}