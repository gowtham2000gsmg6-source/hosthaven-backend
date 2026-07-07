package com.example.demo.repository;

import com.example.demo.entity.BookingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;


public interface BookingReservationRepository extends JpaRepository<BookingReservation, Long> {

    @Query("SELECT COUNT(b) FROM BookingReservation b WHERE b.listing.id = :listingId " +
           "AND b.status <> :cancelledStatus " +
           "AND b.checkInDate < :end AND b.checkOutDate > :start")
    long countOverlappingBookings(@Param("listingId") Long listingId,
                                  @Param("start") LocalDate start,
                                  @Param("end") LocalDate end,
                                  @Param("cancelledStatus") BookingReservation.BookingStatus cancelledStatus);

    List<BookingReservation> findByListingId(Long listingId);

    List<BookingReservation> findByGuestId(Long guestId);

    List<BookingReservation> findByListingOwnerId(Long ownerId);

    boolean existsByListingIdAndStatusIn(Long listingId, List<BookingReservation.BookingStatus> statuses);
}
