package com.major.project.airBnbApp.repository;

import com.major.project.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface guestsRepository extends JpaRepository<Guest, Long> {
}
