package com.example.beautyandcosmetics;

import java.util.UUID;

public class FavoriteItem {

    private String userId;
    private String name;
    private String price;
    private String imageURL;

    private static String currentUserId;

    private String productId;
    private boolean isLiked;

    public FavoriteItem() {
        // Constructeur par défaut requis pour les appels à DataSnapshot.getValue(FavoriteItem.class)
    }

    public FavoriteItem(String userId, String name, String price, String imageURL, boolean isLiked) {
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
        this.productId = generateProductId();
        this.isLiked = false;
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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
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
    public void setProductId(String productId) {
        this.productId = productId;
    }




    private String generateProductId() {
        return userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
    }
}
