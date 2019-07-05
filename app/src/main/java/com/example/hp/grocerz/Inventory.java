package com.example.hp.grocerz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Inventory extends AppCompatActivity {
    private InventoryAdapter mPlaceAdapter;
    private ArrayList<Product> mInventoryItem;
    private ArrayList<String> mIdList;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initProducts();
    }

    private void initProducts() {
        mInventoryItem = new ArrayList<>();
        mIdList = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child("product");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot prod : dataSnapshot.getChildren()) {
                        Product x  = prod.getValue(Product.class);
                        mInventoryItem.add(x);
                        mIdList.add(prod.getKey());
                    }
                    displayProducts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayProducts() {
        RecyclerView mrecyclerPlace = (RecyclerView) findViewById(R.id.inventory_recycler);
        LinearLayoutManager mlinearPlaceManager = new LinearLayoutManager(this);
        mPlaceAdapter = new InventoryAdapter(this,mInventoryItem,mIdList);
        mrecyclerPlace.setAdapter(mPlaceAdapter);
        mrecyclerPlace.setLayoutManager(mlinearPlaceManager);
    }

}
