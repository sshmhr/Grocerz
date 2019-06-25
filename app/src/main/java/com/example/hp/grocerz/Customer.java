package com.example.hp.grocerz;

public class Customer {
    public  String name = "";
    public  String email = "" ;
    public String phone = "";
    public String address = "";
    public int balance = 0 ;

    Customer(){}
    Customer(String name , String email , String phone , String address , int balance){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.balance = balance ;
    }
}
