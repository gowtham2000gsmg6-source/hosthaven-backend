package com.example.demo.repository;

import com.example.demo.entity.PropertyListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface PropertyListingRepository extends JpaRepository<PropertyListing, Long> {

    List<PropertyListing> findByStatus(PropertyListing.ListingStatus status);
}
