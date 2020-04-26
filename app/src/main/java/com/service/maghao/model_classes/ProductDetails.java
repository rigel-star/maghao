package com.service.maghao.model_classes;

public class ProductDetails {

    private String productName, productQuantity;
    private String productImage, marketPrice;
    private String prodcutKey, productId;



    public ProductDetails(){}

    public ProductDetails(String productName, String productImage, String productPrice, String productQuantity,
                          String productKey, String productId) {
        this.productName = productName;
        this.productImage = productImage;
        this.marketPrice = productPrice;
        this.productQuantity = productQuantity;
        this.prodcutKey = productKey;
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getProdcutKey() {
        return prodcutKey;
    }

    public void setProdcutKey(String prodcutKey) {
        this.prodcutKey = prodcutKey;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
