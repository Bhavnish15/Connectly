package com.major.project.airBnbApp.dto;

import com.major.project.airBnbApp.entity.Hotel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomDto {

    private Long id;
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private Integer capacity;
    private String[] photos;
    private String[] amenities;
}
