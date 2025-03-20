package com.agrishop.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CropRequest {
    private String name;
    private String description;
    private Double quantity;
    private String unit;
    private BigDecimal minBid;
    private LocalDateTime endDate;
    private MultipartFile[] images; // Accept multiple images

    // Getters and Setters
}
