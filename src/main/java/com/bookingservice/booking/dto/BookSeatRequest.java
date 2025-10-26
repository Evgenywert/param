package com.bookingservice.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSeatRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String seatId;

    @NotBlank
    private String userId;

    private String idempotencyKey;
}