package com.example.demo.controller;

import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.entity.BookingReservation;
import com.example.demo.entity.StayReview;
import com.example.demo.repository.BookingReservationRepository;
import com.example.demo.repository.StayReviewRepository;
import com.example.demo.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final BookingReservationRepository bookingRepository;
    private final StayReviewRepository stayReviewRepository;

    public ReviewController(ReviewService reviewService,
                            BookingReservationRepository bookingRepository,
                            StayReviewRepository stayReviewRepository) {
        this.reviewService = reviewService;
        this.bookingRepository = bookingRepository;
        this.stayReviewRepository = stayReviewRepository;
    }

    @PostMapping
    public ResponseEntity<StayReview> submitReview(@RequestBody ReviewRequestDto dto) {
        BookingReservation booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking not found"));
        StayReview review = reviewService.submitReview(booking, dto.getRating(), dto.getComment());
        return ResponseEntity.ok(review);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<StayReview>> getReviewsByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(stayReviewRepository.findByBookingListingId(propertyId));
    }
}
