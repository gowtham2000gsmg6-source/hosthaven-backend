package com.example.demo.service;

import com.example.demo.entity.BookingReservation;
import com.example.demo.entity.PropertyListing;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.BookingReservationRepository;
import com.example.demo.repository.PropertyListingRepository;
import com.example.demo.repository.SystemUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BookingOrchestrator implements IBookingOrchestrator {

    private final BookingReservationRepository bookingRepository;
    private final PropertyListingRepository listingRepository;
    private final SystemUserRepository userRepository;

    public BookingOrchestrator(BookingReservationRepository bookingRepository,
                               PropertyListingRepository listingRepository,
                               SystemUserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingReservation reserveStay(Long listingId, Long guestId, LocalDate start, LocalDate end) {
        PropertyListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Listing not found with ID: " + listingId));

        if (listing.getStatus() != PropertyListing.ListingStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Listing is not active for bookings. Current status: " + listing.getStatus());
        }

        long overlapping = bookingRepository.countOverlappingBookings(
                listingId, start, end, BookingReservation.BookingStatus.CANCELLED);
        if (overlapping > 0) {
            throw new IllegalStateException("Selected dates are already booked");
        }

        SystemUser guest = userRepository.findById(guestId)
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
        if (guest == null) {
            throw new com.example.demo.exception.ResourceNotFoundException("No guest user found for booking");
        }

        long nights = ChronoUnit.DAYS.between(start, end);
        if (nights <= 0) {
            throw new IllegalStateException("Check-out date must be after check-in date");
        }

        BigDecimal totalPrice = listing.getBasePrice().multiply(BigDecimal.valueOf(nights));

        BookingReservation booking = new BookingReservation();
        booking.setListing(listing);
        booking.setGuest(guest);
        booking.setCheckInDate(start);
        booking.setCheckOutDate(end);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingReservation.BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingReservation confirmBooking(Long bookingId) {
        BookingReservation booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingReservation.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        booking.setStatus(BookingReservation.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingReservation cancelBooking(Long bookingId) {
        BookingReservation booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() == BookingReservation.BookingStatus.CANCELLED
                || booking.getStatus() == BookingReservation.BookingStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Booking cannot be cancelled in current status: " + booking.getStatus());
        }
        booking.setStatus(BookingReservation.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingReservation checkIn(Long bookingId) {
        BookingReservation booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingReservation.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be checked in");
        }
        booking.setStatus(BookingReservation.BookingStatus.CHECKED_IN);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingReservation checkOut(Long bookingId) {
        BookingReservation booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingReservation.BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Only checked-in guests can be checked out");
        }
        booking.setStatus(BookingReservation.BookingStatus.COMPLETED);
        return bookingRepository.save(booking);
    }
}
