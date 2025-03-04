package com.agrishop.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BidRequest {
    @NotNull
    private Long cropId;

    @NotNull
    @Positive
    private String amount;
}