package com.agrishop.controller;

import com.agrishop.model.Crop;
import com.agrishop.model.User;
import com.agrishop.payload.request.CropRequest;
import com.agrishop.payload.response.CropResponse;
import com.agrishop.payload.response.MessageResponse;
import com.agrishop.repository.CropRepository;
import com.agrishop.repository.UserRepository;
import com.agrishop.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/crops")
public class CropController {
    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/public/all")
    public ResponseEntity<List<CropResponse>> getAllActiveCrops() {
        List<Crop> crops = cropRepository.findActiveCrops(LocalDateTime.now());
        List<CropResponse> cropResponses = crops.stream()
                .map(this::mapCropToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cropResponses);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getCropById(@PathVariable Long id) {
        return cropRepository.findById(id)
                .map(this::mapCropToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_FARMER')")
    public ResponseEntity<?> createCrop(@Valid @RequestBody CropRequest cropRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User seller = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Crop crop = new Crop();
        crop.setName(cropRequest.getName());
        crop.setDescription(cropRequest.getDescription());
        crop.setQuantity(cropRequest.getQuantity());
        crop.setUnit(cropRequest.getUnit());
        crop.setMinBid(new BigDecimal(cropRequest.getMinBid()));
        crop.setEndDate(cropRequest.getEndDate());
        crop.setSeller(seller);
        crop.setStatus(Crop.CropStatus.ACTIVE);

        // Handle image
        if (cropRequest.getImageBase64() != null && !cropRequest.getImageBase64().isEmpty()) {
            // Remove data:image/jpeg;base64, prefix if present
            String base64Image = cropRequest.getImageBase64();
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            crop.setImage(Base64.getDecoder().decode(base64Image));
        }

        cropRepository.save(crop);

        return ResponseEntity.ok(new MessageResponse("Crop created successfully!"));
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('ROLE_FARMER')")
    public ResponseEntity<List<CropResponse>> getMyListings() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User seller = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Crop> crops = cropRepository.findBySeller(seller);
        List<CropResponse> cropResponses = crops.stream()
                .map(this::mapCropToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cropResponses);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ROLE_FARMER')")
    public ResponseEntity<?> cancelCrop(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return cropRepository.findById(id)
                .map(crop -> {
                    if (!crop.getSeller().getId().equals(userDetails.getId())) {
                        return ResponseEntity.badRequest()
                                .body(new MessageResponse("You can only cancel your own listings!"));
                    }
                    
                    crop.setStatus(Crop.CropStatus.CANCELLED);
                    cropRepository.save(crop);
                    return ResponseEntity.ok(new MessageResponse("Crop listing cancelled successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private CropResponse mapCropToResponse(Crop crop) {
        CropResponse response = new CropResponse();
        response.setId(crop.getId());
        response.setName(crop.getName());
        response.setDescription(crop.getDescription());
        response.setQuantity(crop.getQuantity());
        response.setUnit(crop.getUnit());
        response.setMinBid(crop.getMinBid());
        response.setEndDate(crop.getEndDate());
        response.setStatus(crop.getStatus().name());
        response.setCreatedAt(crop.getCreatedAt());
        
        // Set seller info
        response.setSellerId(crop.getSeller().getId());
        response.setSellerName(crop.getSeller().getName());
        
        // Convert image to Base64
        if (crop.getImage() != null) {
            response.setImageBase64("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(crop.getImage()));
        }
        
        return response;
    }
}