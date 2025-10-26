package com.bookingservice.booking.kafka;

import com.bookingservice.booking.event.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingEventConsumer {

    @KafkaListener(topics = "booking-events", groupId = "booking-service")
    public void consumeBookingEvent(BookingEvent event) {
        log.info("Получено событие из Kafka: {}", event);

        switch (event.getStatus()) {
            case "BOOKING_CREATED" -> log.info("Бронь создана: {}", event.getBookingId());
            case "BOOKING_CANCELLED" -> log.info("Бронь отменена: {}", event.getBookingId());
            case "BOOKING_EXPIRED" -> log.info("Бронь истекла: {}", event.getBookingId());
            default -> log.warn("Неизвестный тип события: {}", event.getStatus());
        }
    }
}