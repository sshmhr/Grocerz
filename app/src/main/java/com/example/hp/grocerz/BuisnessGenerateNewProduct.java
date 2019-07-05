package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuisnessGenerateNewProduct extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_generate_new_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initElements();
        myRef = FirebaseDatabase.getInstance().getReference("product");
    }

    private void initElements() {
        text_name = findViewById(R.id.prod_edit_name);
        text_price = findViewById(R.id.prod_edit_price);
        text_qty = findViewById(R.id.prod_edit_qty);
        text_store = findViewById(R.id.cust_edit_phone);
        progress = findViewById(R.id.product_gen_progress);
        progress.setVisibility(View.GONE);
    }

    public void addNewProduct(View v){
        if(isValid()){
            progress.setVisibility(View.VISIBLE);
            Product newProduct = new Product(name,Integer.parseInt(qty),Double.parseDouble(price),store);
            final String key = myRef.push().getKey();
            myRef.child(key).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(BuisnessGenerateNewProduct.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    Intent i = new Intent(BuisnessGenerateNewProduct.this,GenerateProductQR.class);
                    i.putExtra("productId",key);
                    startActivity(i);
                    BuisnessGenerateNewProduct.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(BuisnessGenerateNewProduct.this, "Product Not Added" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
