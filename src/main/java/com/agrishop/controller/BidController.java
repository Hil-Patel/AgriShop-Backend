package com.agrishop.controller;

import com.agrishop.model.Bid;
import com.agrishop.model.Crop;
import com.agrishop.model.User;
import com.agrishop.payload.request.BidRequest;
import com.agrishop.payload.response.BidResponse;
import com.agrishop.payload.response.MessageResponse;
import com.agrishop.repository.BidRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/bids")
public class BidController {
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_BUYER')")
    public ResponseEntity<?> placeBid(@Valid @RequestBody BidRequest bidRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bidder = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Crop crop = cropRepository.findById(bidRequest.getCropId())
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        // Check if crop is active
        if (crop.getStatus() != Crop.CropStatus.ACTIVE) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("This crop listing is no longer active"));
        }

        // Check if bidding end date has passed
        if (crop.getEndDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Bidding period has ended for this crop"));
        }

        // Check if bid amount is greater than minimum bid
        BigDecimal bidAmount = new BigDecimal(bidRequest.getAmount());
        if (bidAmount.compareTo(crop.getMinBid()) < 0) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Bid amount must be greater than the minimum bid"));
        }

        // Check if bid amount is greater than current highest bid
        Optional<BigDecimal> highestBid = bidRepository.findHighestBidAmountByCrop(crop);
        if (highestBid.isPresent() && bidAmount.compareTo(highestBid.get()) <= 0) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Bid amount must be greater than the current highest bid"));
        }

        // Create and save the bid
        Bid bid = new Bid();
        bid.setAmount(bidAmount);
        bid.setCrop(crop);
        bid.setBidder(bidder);
        bid.setStatus(Bid.BidStatus.PENDING);

        bidRepository.save(bid);

        return ResponseEntity.ok(new MessageResponse("Bid placed successfully!"));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<List<BidResponse>> getBidsByCrop(@PathVariable Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        List<Bid> bids = bidRepository.findByCropOrderByAmountDesc(crop);
        List<BidResponse> bidResponses = bids.stream()
                .map(this::mapBidToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bidResponses);
    }

    @GetMapping("/my-bids")
    @PreAuthorize("hasRole('ROLE_BUYER')")
    public ResponseEntity<List<BidResponse>> getMyBids() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bidder = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Bid> bids = bidRepository.findByBidder(bidder);
        List<BidResponse> bidResponses = bids.stream()
                .map(this::mapBidToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bidResponses);
    }

    @PutMapping("/{bidId}/accept")
    @PreAuthorize("hasRole('ROLE_FARMER')")
    public ResponseEntity<?> acceptBid(@PathVariable Long bidId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return bidRepository.findById(bidId)
                . map(bid -> {
                    if (!bid.getCrop().getSeller().getId().equals(userDetails.getId())) {
                        return ResponseEntity.badRequest()
                                .body(new MessageResponse("You can only accept bids for your own crops!"));
                    }
                    
                    bid.setStatus(Bid.BidStatus.ACCEPTED);
                    bidRepository.save(bid);
                    
                    // Mark the crop as completed
                    Crop crop = bid.getCrop();
                    crop.setStatus(Crop.CropStatus.COMPLETED);
                    cropRepository.save(crop);
                    
                    return ResponseEntity.ok(new MessageResponse("Bid accepted successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private BidResponse mapBidToResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAmount(bid.getAmount());
        response.setCropId(bid.getCrop().getId());
        response.setCropName(bid.getCrop().getName());
        response.setBidderId(bid.getBidder().getId());
        response.setBidderName(bid.getBidder().getName());
        response.setStatus(bid.getStatus().name());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }
}