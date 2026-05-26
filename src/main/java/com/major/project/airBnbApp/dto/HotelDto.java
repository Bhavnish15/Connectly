package com.major.project.airBnbApp.dto;

import com.major.project.airBnbApp.entity.HotelContactInfo;
import com.major.project.airBnbApp.entity.Room;
import lombok.Data;
import java.util.List;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
