package com.example.demo.service;

import com.example.demo.entity.BookingReservation;
import com.example.demo.entity.PropertyListing;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.BookingReservationRepository;
import com.example.demo.repository.PropertyListingRepository;
import com.example.demo.repository.StayReviewRepository;
import com.example.demo.repository.SystemUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ListingManagementService implements IListingManagementService {

    private final PropertyListingRepository listingRepository;
    private final BookingReservationRepository bookingRepository;
    private final SystemUserRepository userRepository;
    private final StayReviewRepository reviewRepository;

    public ListingManagementService(PropertyListingRepository listingRepository,
                                    BookingReservationRepository bookingRepository,
                                    SystemUserRepository userRepository,
                                    StayReviewRepository reviewRepository) {
        this.listingRepository = listingRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public PropertyListing createListing(String title, String description, String address,
                                          BigDecimal basePrice, SystemUser owner, String benefits, String imageUrl) {
        if (owner == null) {
            throw new IllegalArgumentException("Property owner is mandatory");
        }
        PropertyListing listing = new PropertyListing(owner, title, description, address, basePrice);
        listing.setBenefits(benefits);
        listing.setImageUrl(imageUrl);
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void publishListing(Long listingId) {
        PropertyListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Listing not found"));
        if (listing.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Base price must be greater than zero");
        }
        listing.setStatus(PropertyListing.ListingStatus.ACTIVE);
        listingRepository.save(listing);
    }

    @Override
    public List<PropertyListing> getAllListings() {
        return listingRepository.findAll();
    }

    @Override
    public Optional<PropertyListing> getListingById(Long id) {
        return listingRepository.findById(id);
    }

    @Override
    @Transactional
    public PropertyListing updateListing(Long id, String title, String description, String address,
                                          BigDecimal basePrice, Long ownerId, String benefits, String imageUrl) {
        PropertyListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Listing not found"));
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setAddress(address);
        listing.setBasePrice(basePrice);
        listing.setBenefits(benefits);
        if (imageUrl != null) {
            listing.setImageUrl(imageUrl);
        }
        if (ownerId != null) {
            userRepository.findById(ownerId).ifPresent(listing::setOwner);
        }
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public PropertyListing updateListingStatus(Long id, PropertyListing.ListingStatus status) {
        PropertyListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Listing not found"));
        if (status == PropertyListing.ListingStatus.MAINTENANCE) {
            List<BookingReservation.BookingStatus> activeStatuses = List.of(
                    BookingReservation.BookingStatus.PENDING,
                    BookingReservation.BookingStatus.CONFIRMED,
                    BookingReservation.BookingStatus.CHECKED_IN
            );
            if (bookingRepository.existsByListingIdAndStatusIn(id, activeStatuses)) {
                throw new IllegalStateException(
                        "Cannot put property in maintenance while there are active or pending bookings.");
            }
        }
        listing.setStatus(status);
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void deleteListing(Long id) {
        if (!listingRepository.existsById(id)) {
            throw new com.example.demo.exception.ResourceNotFoundException("Listing not found");
        }
        // Reviews reference bookings, and bookings reference the listing.
        // Delete children first (reviews -> bookings) to avoid FK constraint violations.
        reviewRepository.deleteAll(reviewRepository.findByBookingListingId(id));
        bookingRepository.deleteAll(bookingRepository.findByListingId(id));
        listingRepository.deleteById(id);
    }
}
