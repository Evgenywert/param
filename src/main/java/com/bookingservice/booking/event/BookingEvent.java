package com.bookingservice.booking.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private UUID bookingId;
    private UUID seatId;
    private UUID userId;
    private String status;
    private LocalDateTime timestamp;
}