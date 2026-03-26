package com.stockflow.inventory.dto.response;

import com.stockflow.inventory.entity.Purchase;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseResponse {
    private Long id;
    private ProductInfo product;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalCost;
    private LocalDate purchaseDate;
    private LocalDateTime createdAt;

    @Data @Builder
    public static class ProductInfo {
        private Long id;
        private String name;
        private String category;
    }

    public static PurchaseResponse from(Purchase p) {
        return PurchaseResponse.builder()
                .id(p.getId())
                .product(ProductInfo.builder()
                        .id(p.getProduct().getId())
                        .name(p.getProduct().getName())
                        .category(p.getProduct().getCategory())
                        .build())
                .quantity(p.getQuantity())
                .pricePerUnit(p.getPricePerUnit())
                .totalCost(p.getTotalCost())
                .purchaseDate(p.getPurchaseDate())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
