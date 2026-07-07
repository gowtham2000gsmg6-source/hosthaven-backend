package com.example.demo.service;

import com.example.demo.entity.PropertyListing;
import com.example.demo.entity.SystemUser;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IListingManagementService {

    PropertyListing createListing(String title, String description, String address,
                                   BigDecimal basePrice, SystemUser owner, String benefits, String imageUrl);

    void publishListing(Long listingId);

    List<PropertyListing> getAllListings();

    Optional<PropertyListing> getListingById(Long id);

    PropertyListing updateListing(Long id, String title, String description, String address,
                                   BigDecimal basePrice, Long ownerId, String benefits, String imageUrl);

    PropertyListing updateListingStatus(Long id, PropertyListing.ListingStatus status);

    void deleteListing(Long id);
}
