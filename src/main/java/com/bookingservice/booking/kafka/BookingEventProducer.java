package com.bookingservice.booking.kafka;

import com.bookingservice.booking.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    private static final String TOPIC = "booking-events";

    public void sendBookingEvent(BookingEvent event) {
        kafkaTemplate.send(TOPIC, event.getBookingId().toString(), event);
        log.info("Отправлено событие {} в Kafka: {}", event.getStatus(), event);
    }
}