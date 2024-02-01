package com.example.beautyandcosmetics;

public class ShopItem {
    private String name;
    private String price;
    private String imageUrl;
    private String id;

    // Default constructor required for DataSnapshot.getValue(ShopItem.class)
    public ShopItem() {
    }

    public ShopItem(String name, String price, String imageUrl, String id) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
