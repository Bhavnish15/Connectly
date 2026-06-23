package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.dto.BookingDto;
import com.major.project.airBnbApp.dto.BookingRequest;
import com.major.project.airBnbApp.dto.GuestDto;
import com.major.project.airBnbApp.exception.UnauthorizedException;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) throws UnauthorizedException;
}
