package com.stockflow.inventory.dto.request;

import com.stockflow.inventory.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestStatusUpdate {

    @NotNull(message = "Status is required")
    private RequestStatus status;

    private String note;
}
