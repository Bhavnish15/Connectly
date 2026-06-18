package com.major.project.airBnbApp.repository;

import com.major.project.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
