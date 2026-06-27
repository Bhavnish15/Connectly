package com.major.project.airBnbApp.repository;

import com.major.project.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface guestsRepository extends JpaRepository<Guest, Long> {
}
