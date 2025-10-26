package com.bookingservice.booking.repository;

import com.bookingservice.booking.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID>{
    List<Seat> findByEventId(UUID eventId);
}
