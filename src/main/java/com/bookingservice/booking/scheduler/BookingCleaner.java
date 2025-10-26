package com.bookingservice.booking.scheduler;

import com.bookingservice.booking.enums.BookingStatus;
import com.bookingservice.booking.enums.SeatStatus;
import com.bookingservice.booking.model.entity.Booking;
import com.bookingservice.booking.model.entity.Seat;
import com.bookingservice.booking.repository.BookingRepository;
import com.bookingservice.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleaner {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredBookings() {
        List<Booking> expired = bookingRepository.findAll()
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .filter(b -> b.getExpiresAt().isBefore(LocalDateTime.now()))
                .toList();

        if (expired.isEmpty()) return;
        log.info("Найдено {} просроченных броней", expired.size());

        for (Booking booking : expired) {
            Seat seat = booking.getSeat();
            seat.setStatus(SeatStatus.FREE);
            booking.setStatus(BookingStatus.CANCELLED);

            seatRepository.save(seat);
            bookingRepository.save(booking);

            log.info("Бронь {} истекла — место {} освобождено", booking.getId(), seat.getId());
        }
    }
}