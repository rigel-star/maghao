package com.service.maghao.model_classes;

public class DraftsItems {

    private String name, image, qty, price, productKey, id, state;


    public DraftsItems(){}

    public DraftsItems(String name, String image, String qty) {
        this.name = name;
        this.image = image;
        this.qty = qty;
    }

    public DraftsItems(String name, String img, String qty, String price){
        this.name = name;
        this.image = img;
        this.qty = qty;
        this.price = price;
    }

    public DraftsItems(java.lang.String name, java.lang.String image, java.lang.String qty,
                       String price, String key, String id) {
        this.name = name;
        this.image = image;
        this.qty = qty;
        this.price = price;
        this.productKey = key;
        this.id = id;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
