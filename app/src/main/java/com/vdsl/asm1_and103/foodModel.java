package com.vdsl.asm1_and103;

public class foodModel {

    private  String _id;
    private String name;
    private double price;
    private  String category;
    private  String description;
    private  String servingSize;
    private  String image;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public foodModel() {
    }

    public foodModel(String _id, String name, int price, String category, String description, String servingSize, String image) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.servingSize = servingSize;
        this.image = image;
    }

    public foodModel(String name, double price,String servingSize,String image) {
        this.name = name;
        this.price = price;
        this.servingSize = servingSize;
        this.image = image;   
    }
    public foodModel(String _id,String name, double price,String servingSize,String image) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.servingSize = servingSize;
        this.image = image;
    }
}
