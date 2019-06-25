package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BuisnessAddAmount extends AppCompatActivity {

    private TextView textCustName;
    private TextView textCustAddress;
    private TextView textCustBalance;
    private TextView textCustContact;
    private String key;
    private ProgressBar loading;
    private EditText amount;
    private DatabaseReference child;
    private Customer x;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_add_amount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getStringExtra("custId");
        initElements();
        populateData();
    }

    private void populateData() {
        loading.setVisibility(View.VISIBLE);
        button.setClickable(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("customer");
        child = myRef.child(key);

        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                x = dataSnapshot.getValue(Customer.class);
                textCustName.setText(x.name);
                textCustAddress.setText(x.address);
                textCustBalance.setText("" + x.balance);
                textCustContact.setText(x.phone);
                loading.setVisibility(View.GONE);
                button.setClickable(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
                loading.setVisibility(View.GONE);
                button.setClickable(true);
            }
        });
    }

    private void initElements() {
        textCustName = findViewById(R.id.text_cust_name);
        textCustAddress = findViewById(R.id.text_cust_address);
        textCustBalance = findViewById(R.id.text_cust_balance);
        textCustContact = findViewById(R.id.text_cust_contact);
        loading = findViewById(R.id.customer_detail_progress);
        amount = findViewById(R.id.amt);
        button = findViewById(R.id.button3);
    }

    public void addAmount(View view){
        String strAmt = amount.getText().toString().trim();
        if(!strAmt.equals("")){
            int amt = Integer.parseInt(strAmt);
            x.balance+=amt;
            child.setValue(x).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(BuisnessAddAmount.this, "Balance updated successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(BuisnessAddAmount.this,buisnessDashboard.class);
                    startActivity(i);
                    BuisnessAddAmount.this.finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BuisnessAddAmount.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
