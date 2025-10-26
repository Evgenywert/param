package com.bookingservice.booking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private String status;
    private String bookingId;
    private String expiresAt;
}