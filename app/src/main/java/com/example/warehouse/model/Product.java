package com.example.warehouse.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Model sản phẩm trong kho hàng. Chỉ chứa dữ liệu; mọi truy vấn nằm trong DatabaseHelper.
 */
public class Product {

    private long id;
    @NonNull
    private String name;
    private long price;
    private int quantity;
    @NonNull
    private String category;
    private boolean active;
    @Nullable
    private String description;

    public Product(long id, @NonNull String name, long price, int quantity,
                    @NonNull String category, boolean active, @Nullable String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public long getStockValue() {
        return price * (long) quantity;
    }
}
