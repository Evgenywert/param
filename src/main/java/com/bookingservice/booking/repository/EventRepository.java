package com.bookingservice.booking.repository;

import com.bookingservice.booking.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID>{

}
