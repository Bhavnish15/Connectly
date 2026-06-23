package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.RoomDto;
import com.major.project.airBnbApp.entity.Hotel;
import com.major.project.airBnbApp.entity.Room;
import com.major.project.airBnbApp.entity.User;
import com.major.project.airBnbApp.exception.ResourceNotFoundException;
import com.major.project.airBnbApp.exception.UnauthorizedException;
import com.major.project.airBnbApp.repository.HotelRepository;
import com.major.project.airBnbApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;


    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) throws UnauthorizedException {
        log.info("Creating a new room in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: "+ hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+ hotelId);
        }
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Transactional
    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) throws UnauthorizedException {
        log.info("Getting All Rooms from "+ hotelId + " Hotel");
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Hotel not found with id: "+ hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+ hotelId);
        }
        return hotel.getRooms()
                .stream()
                .map((e) -> modelMapper.map(e, RoomDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting rooms by Id: "+ roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found with id: "+ roomId));
        return modelMapper.map(room, RoomDto.class);

    }

    @Transactional
    @Override
    public void deleteRoomById(Long roomId) throws UnauthorizedException {
        log.info("Deleting the room with Id: " + roomId);
        boolean exists = roomRepository.existsById(roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found with id: "+ roomId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+ roomId);
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }
}


















