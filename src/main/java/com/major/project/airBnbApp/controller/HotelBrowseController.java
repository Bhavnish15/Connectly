package com.major.project.airBnbApp.controller;

import com.major.project.airBnbApp.dto.HotelInfoDto;
import com.major.project.airBnbApp.dto.HotelPriceDto;
import com.major.project.airBnbApp.dto.HotelSearchRequest;
import com.major.project.airBnbApp.service.HotelService;
import com.major.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest){
        var page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto>getHotelInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHoteInfoById(hotelId));
    }

}














