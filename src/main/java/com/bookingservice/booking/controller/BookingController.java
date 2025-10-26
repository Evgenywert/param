package com.bookingservice.booking.controller;

import com.bookingservice.booking.dto.*;
import com.bookingservice.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookSeat(
            @RequestBody @Valid BookSeatRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        BookingResponse response = bookingService.book(request, idempotencyKey);

        return switch (response.getStatus()) {
            case "SUCCESS" -> ResponseEntity.ok(response);
            case "SEAT_ALREADY_TAKEN" -> ResponseEntity.status(409).body(response);
            default -> ResponseEntity.internalServerError().body(response);
        };
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResponse> cancelBooking(
            @RequestBody @Valid CancelBookingRequest request) {

        CancelResponse response = bookingService.cancel(request);

        return switch (response.getStatus()) {
            case "SUCCESS" -> ResponseEntity.ok(response);
            case "NOT_FOUND" -> ResponseEntity.status(404).body(response);
            default -> ResponseEntity.internalServerError().body(response);
        };
    }
}