package com.example.demo.repository;

import com.example.demo.entity.StayReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StayReviewRepository extends JpaRepository<StayReview, Long> {

    List<StayReview> findByBookingListingId(Long listingId);
}
