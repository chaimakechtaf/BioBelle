package com.example.beautyandcosmetics;

import java.util.UUID;

public class CartItem {
    private String userId;
    private String name;
    private String brand;
    private String price;
    private String imageURL;
    private int quantity;

    private static String currentUserId;

    private String productId;


    public CartItem() {
        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    }

    public CartItem(String userId, String name, String brand, String price, String imageURL, int quantity) {
        this.userId = userId;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.imageURL = imageURL;
        this.quantity = quantity;
        this.productId = generateProductId(userId, name);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    public String getProductId() {
        return productId;
    }

    static String generateProductId(String userId, String name) {
        return userId + "_" + name;
    }
}
