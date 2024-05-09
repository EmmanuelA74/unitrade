package com.example.unitrade;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Listing {


    private String itemName;
    private String itemDescription;
    private String itemCondition;
    private double price;
    private String deliveryType;
    private String imageUrl;
    private long id;

    private boolean addedToCart;

    private String review;




    // Constructor, getters, and setters
    public Listing() {}

    public Listing(String itemName, String itemDescription, String itemCondition, double price, String deliveryType, String imageUrl , boolean addedToCart, String review) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCondition = itemCondition;
        this.price = price;
        this.deliveryType = deliveryType;
        this.imageUrl = imageUrl;
        this.addedToCart = false;
        this.review = review;
    }

    // Getters and setters
    // Add getters and setters for all fields

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addToCart(){
        this.addedToCart = true;
    }

    public void removeFromCart(){
        this.addedToCart = false;
    }

    public String getReview(){
        return review;
    }

    public void setReview(String review){
        this.review = review;
    }

}
