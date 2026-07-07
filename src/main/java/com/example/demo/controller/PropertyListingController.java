package com.example.demo.controller;

import com.example.demo.dto.ListingRequestDto;
import com.example.demo.entity.PropertyListing;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.service.IListingManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
public class PropertyListingController {

    private final IListingManagementService listingService;
    private final SystemUserRepository userRepository;

    public PropertyListingController(IListingManagementService listingService,
                                     SystemUserRepository userRepository) {
        this.listingService = listingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/owners")
    public ResponseEntity<List<SystemUser>> getOwners() {
        return ResponseEntity.ok(userRepository.findByRole(SystemUser.UserRole.PROPERTY_OWNER));
    }

    @GetMapping
    public ResponseEntity<List<PropertyListing>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyListing> getListingById(@PathVariable Long id) {
        return listingService.getListingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PropertyListing> createListing(@Valid @RequestBody ListingRequestDto dto) {
        SystemUser owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != SystemUser.UserRole.PROPERTY_OWNER
                && owner.getRole() != SystemUser.UserRole.HOSPITALITY_ADMIN) {
            throw new com.example.demo.exception.BusinessValidationException("Only property owners or admins can create listings");
        }

        PropertyListing listing = listingService.createListing(
                dto.getTitle(), dto.getDescription(), dto.getAddress(),
                dto.getBasePrice(), owner, dto.getBenefits(), dto.getImageUrl()
        );
        listingService.publishListing(listing.getId());
        PropertyListing published = listingService.getListingById(listing.getId()).orElse(listing);
        return ResponseEntity.status(HttpStatus.CREATED).body(published);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyListing> updateListing(@PathVariable Long id,
                                                          @Valid @RequestBody ListingRequestDto dto) {
        PropertyListing updated = listingService.updateListing(
                id, dto.getTitle(), dto.getDescription(), dto.getAddress(),
                dto.getBasePrice(), dto.getOwnerId(), dto.getBenefits(), dto.getImageUrl()
        );
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PropertyListing> updateStatus(@PathVariable Long id,
                                                         @RequestParam PropertyListing.ListingStatus status) {
        PropertyListing updated = listingService.updateListingStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseEntity.ok("PropertyListing deleted successfully.");
    }
}
