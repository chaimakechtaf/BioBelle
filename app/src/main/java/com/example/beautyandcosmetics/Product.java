package com.example.beautyandcosmetics;

public class Product {

    private String name;
    private String price;
    private String imageUrl; // Change from imageFilePath to imageUrl

    // Required default constructor for Firebase
    public Product() {
        // Default constructor required for Firebase
    }

    // Constructor without imageUrl
    public Product(String name, String price) {
        this.name = name;
        this.price = price;
        // imageUrl will be null by default
    }

    // Constructor with all parameters
    public Product(String name,  String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public String getName() {
        return name;
    }


    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setter methods (you may not need them, but it's good practice to have them)
    public void setName(String name) {
        this.name = name;
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
