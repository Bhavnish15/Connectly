package com.major.project.airBnbApp.controller;

import com.major.project.airBnbApp.dto.RoomDto;
import com.major.project.airBnbApp.exception.UnauthorizedException;
import com.major.project.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto) throws UnauthorizedException {
        log.info("Attempting to create a new room with tye: "+ roomDto.getType());
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) throws UnauthorizedException {
        log.info("Getting All the Room in a Hotel");
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId, @PathVariable Long roomId){
        log.info("Getting Room by Id in an hotel : "+ roomId);
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> DeleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId) throws UnauthorizedException {
        log.info("Deleting the Room by Id: "+ roomId);
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }


}




























