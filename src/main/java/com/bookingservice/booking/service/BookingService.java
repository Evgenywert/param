package com.bookingservice.booking.service;

import com.bookingservice.booking.dto.BookSeatRequest;
import com.bookingservice.booking.dto.BookingResponse;
import com.bookingservice.booking.dto.CancelBookingRequest;
import com.bookingservice.booking.dto.CancelResponse;

public interface BookingService {

    BookingResponse book(BookSeatRequest request, String idempotencyKey);

    CancelResponse cancel(CancelBookingRequest request);
}