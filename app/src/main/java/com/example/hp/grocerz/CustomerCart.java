package com.example.hp.grocerz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerCart extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SQLiteDatabase db;
    private DatabaseReference myUserDatabaseRef;
    private DatabaseReference myTransactionDatabaseRef;
    private DatabaseReference myProductDatabaseRef;
    private Customer newCust;
    private String custkey;
    private int balance;
    private double shoppingamt;
    private double totcost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myUserDatabaseRef = FirebaseDatabase.getInstance().getReference();
        myProductDatabaseRef = FirebaseDatabase.getInstance().getReference("product");
        myTransactionDatabaseRef = FirebaseDatabase.getInstance().getReference("transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initDatabase();
    }

    private void initDatabase() {
        db = openOrCreateDatabase("testProducts2",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cart"+mUser.getUid()+"(prodId VARCHAR PRIMARY KEY,prodName VARCHAR,count int,availableCount int,price varchar);");
        Cursor c = db.rawQuery("Select rowid _id,* from cart"+mUser.getUid()+";",null) ;
        if(c.getCount()>0){
            ListView list = findViewById(R.id.Customer_cart_list);
            CartItemAdapter adapter = new CartItemAdapter(
                    this, R.layout.cart_item, c, 0 );
            list.setAdapter(adapter);
        }else{
            Toast.makeText(this, "No item present in the cart", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearCart(View view){
        db.execSQL("delete from cart"+mUser.getUid()+";");
        Toast.makeText(this, "Cart is cleared", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this,CustomerDashboard.class);
        startActivity(i);
        this.finish();
    }

    public void pay(View v){
        Query query = myUserDatabaseRef.child("customer").orderByChild("email").equalTo(mUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot buisness : dataSnapshot.getChildren()) {

                        Customer x  = buisness.getValue(Customer.class);
                        int bal = x.balance ;
                        shoppingamt = computeamt();
                        if(shoppingamt > (double) bal){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCart.this);
                            builder.setTitle("INSUFFICIENT BALANCE");
                            builder.setIcon(R.drawable.ic_close_black_24dp);
                            builder.setMessage("Your Balance is rs " + bal + "/- and you have shopped for rs " + shoppingamt +  " /-  ,\n please remove some items from the cart or recharge the wallet");
                            builder.setNegativeButton("OK",null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCart.this);
                            builder.setTitle("ARE YOU SURE ? ");
                            builder.setIcon(R.drawable.ic_close_black_24dp);
                            builder.setMessage("Do you want to proceed to pay rs " + shoppingamt + " /- your current balance is rs " + bal + " /- and your remaining balance after transaction would be rs "+ ((double)bal - (double) shoppingamt) + " /-");
                            balance = bal - (int) shoppingamt;
                            final  String name = x.name;
                            final  String email = x.email;
                            final String phone = x.phone;
                            final String address = x.address;
                            custkey = buisness.getKey();
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    newCust = new Customer(name,email,phone,address, balance);

                                    myUserDatabaseRef.child("customer").child(custkey).setValue(newCust).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CustomerCart.this, "Balance Deducted", Toast.LENGTH_SHORT).show();
                                            manageInventory();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CustomerCart.this, "Balance not Deducted " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("NO",null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerCart.this, "Error " + databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    private void manageInventory() {
        Cursor c = db.rawQuery("Select * from cart"+mUser.getUid()+";",null) ;
        String mail = "";
        double total = 0 ;
        if(c.moveToFirst()) {
            do {

                int count = c.getInt(2);
                String key = c.getString(0);
                String name = c.getString(1);
                int totCount = c.getInt(3);
                double price = Double.parseDouble(c.getString(4));
                String location = c.getString(5);
                totcost = 0;
                mail+="Item Name : " + name  + "\nitem id : " + key + "\nnoOfUnits " + count + "\ncost : " + (count*price) + "\n\n";
                totcost +=(count*price);
                total+=totcost;
                Product p = new Product(name,totCount - count ,price,location );
                myProductDatabaseRef.child(key).setValue(p).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomerCart.this, "issue while sending mail amount is refunded" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        newCust.balance = newCust.balance + (int)shoppingamt ;
                        myUserDatabaseRef.child("customer").child(custkey).setValue(newCust);
                    }
                });
            } while (c.moveToNext());
            mail+="TOTAL : " + total  + " \n" ;
            addTransaction(mail);
            db.execSQL("delete from cart"+mUser.getUid()+";");
            Toast.makeText(this, "Cart is cleared", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,CustomerDashboard.class);
            startActivity(i);
            this.finish();
        }

    }

    private void addTransaction(String details) {
        String dt = getDateTime() ;
        Transactions t = new Transactions(mUser.getUid(),details,dt);
        String key =myTransactionDatabaseRef.push().getKey();
        myTransactionDatabaseRef.child(key).setValue(t);
        db.execSQL("insert into Transactions" + mUser.getUid() + " values('" +key + "','" + mUser.getUid() + "','"+ details + "','" + dt +"');" );
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private double computeamt() {
        Cursor c = db.rawQuery("Select * from cart"+mUser.getUid()+";",null) ;
        double total = 0 ;
        if(c.moveToFirst()) {
            do {
                total = total + Double.parseDouble(c.getString(4))*c.getInt(2);
            } while (c.moveToNext());
        }
        return total;
    }

}
