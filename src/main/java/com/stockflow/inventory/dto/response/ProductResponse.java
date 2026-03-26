package com.stockflow.inventory.dto.response;

import com.stockflow.inventory.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private String description;
    private String stockStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product p) {
        String status;
        if (p.getStockQuantity() == 0) {
            status = "OUT_OF_STOCK";
        } else if (p.getStockQuantity() <= p.getLowStockThreshold()) {
            status = "LOW_STOCK";
        } else {
            status = "AVAILABLE";
        }

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .stockQuantity(p.getStockQuantity())
                .lowStockThreshold(p.getLowStockThreshold())
                .description(p.getDescription())
                .stockStatus(status)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
