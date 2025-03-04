package com.agrishop.payload.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CropResponse {
    private Long id;
    private String name;
    private String description;
    private Double quantity;
    private String unit;
    private BigDecimal minBid;
    private LocalDateTime endDate;
    private String status;
    private Long sellerId;
    private String sellerName;
    private String imageBase64;
    private LocalDateTime createdAt;
}