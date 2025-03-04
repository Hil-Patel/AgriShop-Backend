package com.agrishop.payload.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidResponse {
    private Long id;
    private BigDecimal amount;
    private Long cropId;
    private String cropName;
    private Long bidderId;
    private String bidderName;
    private String status;
    private LocalDateTime createdAt;
}