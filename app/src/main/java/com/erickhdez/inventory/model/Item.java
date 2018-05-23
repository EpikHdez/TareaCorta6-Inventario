package com.erickhdez.inventory.model;

public class Item {
    private String uid;
    private String name;
    private String description;
    private String picture;
    private double price;

    public Item() {

    }

    public Item(String name, String description, String picture, double price) {
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
