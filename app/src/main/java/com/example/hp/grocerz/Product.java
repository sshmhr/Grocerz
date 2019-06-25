package com.example.hp.grocerz;

public class Product {
    public  String name;
    public int quantity;
    public double price;
    public String store;
    Product(){}

    Product(String name , int quantity , double price , String store){
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.store = store;
    }
}
