package com.example.warehouse.model;

import androidx.annotation.NonNull;

/**
 * Một dòng lịch sử nhập/bán của một sản phẩm.
 */
public class StockLog {

    private final long id;
    private final long productId;
    @NonNull
    private final String type;
    private final int amount;
    private final int quantityAfter;
    private final long createdAt;

    public StockLog(long id, long productId, @NonNull String type, int amount,
                     int quantityAfter, long createdAt) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.amount = amount;
        this.quantityAfter = quantityAfter;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getQuantityAfter() {
        return quantityAfter;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
