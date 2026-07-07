package com.example.demo.controller;

import com.example.demo.dto.BookingRequestDto;
import com.example.demo.entity.BookingReservation;
import com.example.demo.repository.BookingReservationRepository;
import com.example.demo.service.IBookingOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final IBookingOrchestrator bookingOrchestrator;
    private final BookingReservationRepository bookingRepository;

    public BookingController(IBookingOrchestrator bookingOrchestrator,
                             BookingReservationRepository bookingRepository) {
        this.bookingOrchestrator = bookingOrchestrator;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public ResponseEntity<List<BookingReservation>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingReservation>> getOwnerBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<BookingReservation> createBooking(@RequestBody BookingRequestDto dto) {
        BookingReservation booking = bookingOrchestrator.reserveStay(
                dto.getListingId(), dto.getGuestId(), dto.getCheckInDate(), dto.getCheckOutDate());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingReservation> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingOrchestrator.confirmBooking(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingReservation> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingOrchestrator.cancelBooking(id));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<BookingReservation> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(bookingOrchestrator.checkIn(id));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<BookingReservation> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingOrchestrator.checkOut(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingRepository.deleteById(id);
        return ResponseEntity.ok("BookingReservation deleted successfully.");
    }
}
