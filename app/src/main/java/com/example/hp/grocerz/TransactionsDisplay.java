package com.example.hp.grocerz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionsDisplay extends AppCompatActivity {
    private InventoryAdapter mPlaceAdapter;
    private ArrayList<Transactions> mTransactionItem;
    private ArrayList<String> mIdList;
    private DatabaseReference myRef;
    private FirebaseUser mUser;
    private SQLiteDatabase db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = openOrCreateDatabase("testProducts2",MODE_PRIVATE,null);
//        refresh(null);
        initTransactions();
    }

    private void initTransactions() {
        mTransactionItem = new ArrayList<>();
        mIdList = new ArrayList<>();
        Cursor c = db.rawQuery("Select * from Transactions"+mUser.getUid()+";",null) ;
        if(c.moveToFirst()) {
            do {
                Transactions t = new Transactions(c.getString(1),c.getString(2),c.getString(3));
                mTransactionItem.add(t);
                mIdList.add(c.getString(0));
            } while (c.moveToNext());
        }
        Collections.reverse(mTransactionItem);
        Collections.reverse(mIdList);
        displayProducts();
    }

    public void refresh(View v) {
        mTransactionItem = new ArrayList<>();
        mIdList = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child("transaction").orderByChild("userId").equalTo(mUser.getUid());
        db.execSQL("delete from Transactions" + mUser.getUid() + " ;");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot tnxn : dataSnapshot.getChildren()) {
                        Transactions x  = tnxn.getValue(Transactions.class);
                        mTransactionItem.add(x);
                        mIdList.add(tnxn.getKey());
                        db.execSQL("insert into Transactions" + mUser.getUid() + " values('" +tnxn.getKey() + "','" + x.userId + "','" + x.details+ "','"+ x.date+"');" );
                    }
                    Collections.reverse(mTransactionItem);
                    Collections.reverse(mIdList);
                    displayProducts();
                    Toast.makeText(TransactionsDisplay.this, "Refreshed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayProducts() {
        RecyclerView mrecyclerPlace = (RecyclerView) findViewById(R.id.tnxn_recycler);
        LinearLayoutManager mlinearPlaceManager = new LinearLayoutManager(this);
        mPlaceAdapter = new InventoryAdapter(this,mTransactionItem,mIdList,1);
        mrecyclerPlace.setAdapter(mPlaceAdapter);
        mrecyclerPlace.setLayoutManager(mlinearPlaceManager);
    }

}
