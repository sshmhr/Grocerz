package com.example.hp.grocerz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerCart extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initDatabase();
    }

    private void initDatabase() {
        db = openOrCreateDatabase("testproducts",MODE_PRIVATE,null);
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

}
