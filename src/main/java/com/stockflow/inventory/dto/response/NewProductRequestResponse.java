package com.stockflow.inventory.dto.response;

import com.stockflow.inventory.entity.NewProductRequest;
import com.stockflow.inventory.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewProductRequestResponse {
    private Long id;
    private StaffInfo staff;
    private String productName;
    private String category;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;

    @Data @Builder
    public static class StaffInfo {
        private Long id;
        private String name;
    }

    public static NewProductRequestResponse from(NewProductRequest r) {
        return NewProductRequestResponse.builder()
                .id(r.getId())
                .staff(StaffInfo.builder()
                        .id(r.getStaff().getId())
                        .name(r.getStaff().getName())
                        .build())
                .productName(r.getProductName())
                .category(r.getCategory())
                .description(r.getDescription())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
