package com.example.demo.service;

import com.example.demo.entity.BookingReservation;
import java.time.LocalDate;

public interface IBookingOrchestrator {

    BookingReservation reserveStay(Long listingId, Long guestId, LocalDate start, LocalDate end);

    BookingReservation confirmBooking(Long bookingId);

    BookingReservation cancelBooking(Long bookingId);

    BookingReservation checkIn(Long bookingId);

    BookingReservation checkOut(Long bookingId);
}
