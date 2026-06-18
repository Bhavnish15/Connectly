package com.major.project.airBnbApp.dto;

import com.major.project.airBnbApp.entity.Hotel;
import com.major.project.airBnbApp.entity.Room;
import com.major.project.airBnbApp.entity.User;
import com.major.project.airBnbApp.entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate checkInDate;
    private LocalDate checkoutDate;
    private Integer roomsCount;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;
}
