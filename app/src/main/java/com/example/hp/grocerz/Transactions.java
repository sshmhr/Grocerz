package com.example.hp.grocerz;

public class Transactions {

    public  String userId;
    public String details;
    public String date;
    Transactions(){}

    Transactions(String userId , String details, String date ){
        this.userId = userId;
        this.details = details;
        this.date = date;
    }

}
