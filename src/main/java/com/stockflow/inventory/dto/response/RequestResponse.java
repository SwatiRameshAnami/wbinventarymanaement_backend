package com.stockflow.inventory.dto.response;

import com.stockflow.inventory.entity.Request;
import com.stockflow.inventory.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestResponse {
    private Long id;
    private StaffInfo staff;
    private ProductInfo product;
    private Integer quantityRequested;
    private RequestStatus status;
    private String note;
    private LocalDateTime requestDate;
    private LocalDateTime updatedAt;

    @Data @Builder
    public static class StaffInfo {
        private Long id;
        private String name;
        private String username;
    }

    @Data @Builder
    public static class ProductInfo {
        private Long id;
        private String name;
        private String category;
    }

    public static RequestResponse from(Request r) {
        return RequestResponse.builder()
                .id(r.getId())
                .staff(StaffInfo.builder()
                        .id(r.getStaff().getId())
                        .name(r.getStaff().getName())
                        .username(r.getStaff().getUsername())
                        .build())
                .product(r.getProduct() == null ? null : ProductInfo.builder()
                        .id(r.getProduct().getId())
                        .name(r.getProduct().getName())
                        .category(r.getProduct().getCategory())
                        .build())
                .quantityRequested(r.getQuantityRequested())
                .status(r.getStatus())
                .note(r.getNote())
                .requestDate(r.getRequestDate())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
