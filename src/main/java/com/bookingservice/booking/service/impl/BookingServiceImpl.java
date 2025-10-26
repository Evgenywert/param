package com.bookingservice.booking.service.impl;

import com.bookingservice.booking.dto.*;
import com.bookingservice.booking.event.BookingEvent;
import com.bookingservice.booking.kafka.BookingEventProducer;
import com.bookingservice.booking.model.entity.*;
import com.bookingservice.booking.enums.*;
import com.bookingservice.booking.repository.*;
import com.bookingservice.booking.service.BookingService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;
    private final BookingEventProducer eventProducer;

    @Override
    @Transactional
    public BookingResponse book(BookSeatRequest request, String idempotencyKey) {
        log.info("Получен запрос на бронирование: {}", request);

        String lockKey = "seat-lock:" + request.getSeatId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(5, 5, TimeUnit.MINUTES)) {
                log.warn("Не удалось получить лок для места {}", request.getSeatId());
                return BookingResponse.builder()
                        .status("SEAT_ALREADY_TAKEN")
                        .build();
            }

            log.info("Лок получен для места {}", request.getSeatId());

            UUID seatId = UUID.fromString(request.getSeatId());
            Optional<Seat> seatOpt = seatRepository.findById(seatId);
            if (seatOpt.isEmpty()) {
                log.warn("Место не найдено: {}", request.getSeatId());
                return BookingResponse.builder()
                        .status("ERROR")
                        .build();
            }

            Seat seat = seatOpt.get();

            if (seat.getStatus() != SeatStatus.FREE) {
                log.info("Место {} уже занято", seat.getId());
                return BookingResponse.builder()
                        .status("SEAT_ALREADY_TAKEN")
                        .build();
            }

            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);

            Booking booking = Booking.builder()
                    .seat(seat)
                    .userId(UUID.fromString(request.getUserId()))
                    .status(BookingStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .build();

            bookingRepository.save(booking);

            eventProducer.sendBookingEvent(BookingEvent.builder()
                    .bookingId(booking.getId())
                    .seatId(seat.getId())
                    .userId(booking.getUserId())
                    .status("BOOKING_CREATED")
                    .timestamp(LocalDateTime.now())
                    .build());

            log.info("Бронь {} успешно создана для места {}", booking.getId(), seat.getId());

            return BookingResponse.builder()
                    .status("SUCCESS")
                    .bookingId(booking.getId().toString())
                    .expiresAt(booking.getExpiresAt().toString())
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ошибка при попытке установить лок для места {}", request.getSeatId(), e);
            return BookingResponse.builder().status("ERROR").build();
        }
    }

    @Override
    @Transactional
    public CancelResponse cancel(CancelBookingRequest request) {
        UUID bookingId = UUID.fromString(request.getBookingId());
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            return CancelResponse.builder().status("NOT_FOUND").build();
        }

        Booking booking = bookingOpt.get();
        Seat seat = booking.getSeat();

        seat.setStatus(SeatStatus.FREE);
        seatRepository.save(seat);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        eventProducer.sendBookingEvent(BookingEvent.builder()
                .bookingId(booking.getId())
                .seatId(seat.getId())
                .userId(booking.getUserId())
                .status("BOOKING_CANCELLED")
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Бронь {} отменена", bookingId);

        return CancelResponse.builder().status("SUCCESS").build();
    }
}