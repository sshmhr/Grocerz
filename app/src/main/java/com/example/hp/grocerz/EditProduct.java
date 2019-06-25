package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProduct extends AppCompatActivity {
    private DatabaseReference myRef;
    private EditText text_name;
    private EditText text_price;
    private EditText text_qty;
    private EditText text_store;
    private String store;
    private String qty;
    private String price;
    private String name;
    private ProgressBar progress;
    private String key;
    private Button button;
    private DatabaseReference child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initElements();
        key = getIntent().getStringExtra("productKey");
        myRef = FirebaseDatabase.getInstance().getReference("product");
        populateData();
    }
    private void initElements() {
        text_name = findViewById(R.id.prod_edit_name);
        text_price = findViewById(R.id.prod_edit_price);
        text_qty = findViewById(R.id.prod_edit_qty);
        text_store = findViewById(R.id.prod_edit_store);
        progress = findViewById(R.id.product_edit_progress);
        progress.setVisibility(View.GONE);
        button = findViewById(R.id.editProduct);
    }

    private void populateData() {
        progress.setVisibility(View.VISIBLE);
        button.setClickable(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("product");
        child = myRef.child(key);

        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product x = dataSnapshot.getValue(Product.class);
                text_name.setText(x.name);
                text_price.setText("" + x.price);
                text_store.setText("" + x.store);
                text_qty.setText("" + x.quantity);
                progress.setVisibility(View.GONE);
                button.setClickable(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
                progress.setVisibility(View.GONE);
                button.setClickable(true);
            }
        });
    }


    public void editProduct(View v){
        if(isValid()){
            progress.setVisibility(View.VISIBLE);
            Product newProduct = new Product(name,Integer.parseInt(qty),Double.parseDouble(price),store);
            myRef.child(key).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditProduct.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    Intent i = new Intent(EditProduct.this,buisnessDashboard.class);
                    startActivity(i);
                    EditProduct.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(EditProduct.this, "Product Not Updated " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No field can be left blank", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        name = text_name.getText().toString();
        price = text_price.getText().toString();
        qty = text_qty.getText().toString();
        store = text_store.getText().toString();
        if(name.isEmpty() ||price.isEmpty() ||qty.isEmpty() ||store.isEmpty() )
            return false;
        else
            return true;
    }
}
