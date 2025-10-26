package com.bookingservice.booking.repository;

import com.bookingservice.booking.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface BookingRepository extends JpaRepository<Booking, UUID>{
    Optional<Booking> findBySeatId(UUID seatId);
}
