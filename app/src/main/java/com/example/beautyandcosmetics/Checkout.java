package com.example.beautyandcosmetics;

public class Checkout {
    private String Full_Name;
    private String Email;
    private String Phone;
    private String Address;
    private String city;
    private String totalProducts;  // New field
    private double totalPrice;    // New field

    public Checkout() {
        // Default constructor required for calls to DataSnapshot.getValue(Checkout.class)
    }

    public Checkout(String Full_Name, String Email, String Phone, String Address, String city, String totalProducts, double totalPrice) {
        this.Full_Name = Full_Name;
        this.Email = Email;
        this.Phone = Phone;
        this.Address = Address;
        this.city = city;
        this.totalProducts = totalProducts;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(String totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
