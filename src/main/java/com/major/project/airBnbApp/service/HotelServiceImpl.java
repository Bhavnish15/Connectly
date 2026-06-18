package com.major.project.airBnbApp.service;


import com.major.project.airBnbApp.dto.HotelDto;
import com.major.project.airBnbApp.dto.HotelInfoDto;
import com.major.project.airBnbApp.dto.RoomDto;
import com.major.project.airBnbApp.entity.Hotel;
import com.major.project.airBnbApp.entity.Room;
import com.major.project.airBnbApp.exception.ResourceNotFoundException;
import com.major.project.airBnbApp.repository.HotelRepository;
import com.major.project.airBnbApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the Hotel with Id: {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id: "+ id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Getting the hotel for update.");
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with this id :"+id));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }


    @Transactional
    @Override
    public void DeleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found with id: "+ id));

        for(Room room: hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel...with Id: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found with id: "+ hotelId));
        hotel.setActive(true);
        // Assuming only do it once
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
//        modelMapper.map(hotel, HotelDto)
    }


    @Override
    public List<HotelDto> getAllHotels() {
        log.info("Getting All Hotels");
        List<Hotel> hotels = hotelRepository.findAll();
            return hotels.stream()
                    .map((e) -> modelMapper.map(e, HotelDto.class))
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelInfoDto getHoteInfoById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found with id: "+ hotelId));

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((e) -> modelMapper.map(e, RoomDto.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);
    }


}



















