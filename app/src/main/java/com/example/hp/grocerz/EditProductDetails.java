package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EditProductDetails extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private EditText key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = findViewById(R.id.prod_key);
    }

    public void readFromQR(View view){
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }

    public void manualEntry(View view){
        if(!key.getText().toString().isEmpty()){
            Intent i = new Intent(this ,EditProduct.class);
            i.putExtra("productKey",key.getText().toString().trim());
            startActivity(i);
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                String obj = result.getContents();
                Intent i = new Intent(this ,EditProduct.class);
                i.putExtra("productKey",obj);
                startActivity(i);
                this.finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
