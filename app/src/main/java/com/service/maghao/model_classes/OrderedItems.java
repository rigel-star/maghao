package com.service.maghao.model_classes;

public class OrderedItems {

    private String name, image, key, qty, cost;

    OrderedItems(){}

    public OrderedItems(String name, String image, String key) {
        this.name = name;
        this.image = image;
        this.key = key;
    }

    public OrderedItems(String name, String image, String key, String qty, String cost) {
        this.name = name;
        this.image = image;
        this.key = key;
        this.qty = qty;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
