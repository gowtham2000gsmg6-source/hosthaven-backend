package com.example.demo.service;

import com.example.demo.entity.BookingReservation;
import com.example.demo.entity.StayReview;
import com.example.demo.repository.PropertyListingRepository;
import com.example.demo.repository.StayReviewRepository;
import com.example.demo.repository.BookingReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReviewService {

    private final StayReviewRepository reviewRepository;
    private final BookingReservationRepository bookingRepository;
    private final PropertyListingRepository listingRepository;

    public ReviewService(StayReviewRepository reviewRepository,
                         BookingReservationRepository bookingRepository,
                         PropertyListingRepository listingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
    }

    @Transactional
    public StayReview submitReview(BookingReservation booking, Integer rating, String comment) {
        if (booking.getStatus() != BookingReservation.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Can only review completed stays");
        }
        if (Boolean.TRUE.equals(booking.getIsRated())) {
            throw new IllegalStateException("This stay has already been rated");
        }

        StayReview review = new StayReview();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);

        booking.setIsRated(true);
        bookingRepository.save(booking);

        StayReview saved = reviewRepository.save(review);
        updateListingRating(booking.getListing().getId());
        return saved;
    }

    private void updateListingRating(Long listingId) {
        List<StayReview> reviews = reviewRepository.findByBookingListingId(listingId);
        double avg = reviews.stream()
                .mapToInt(StayReview::getRating)
                .average()
                .orElse(0.0);
        listingRepository.findById(listingId).ifPresent(listing -> {
            listing.setAverageRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            listingRepository.save(listing);
        });
    }
}
