package com.agrishop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CropRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Double quantity;

    @NotBlank
    private String unit;

    @NotNull
    @Positive
    private String minBid;

    @NotNull
    private LocalDateTime endDate;
    
    private String imageBase64;
}