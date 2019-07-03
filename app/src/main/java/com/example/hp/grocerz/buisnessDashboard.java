package com.example.hp.grocerz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.view.View.INVISIBLE;

public class buisnessDashboard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference myRef;
    private TextView buisness_name;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        initEements();
    }


    private void initEements() {
        buisness_name = findViewById(R.id.text_buisnessName);
        Query query = myRef.child("buisness").orderByChild("email").equalTo(mUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot buisness : dataSnapshot.getChildren()) {
                        Buisness x  = buisness.getValue(Buisness.class);
                        buisness_name.setText(x.name); }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buisnessDashboard.this, "Error " + databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addNewProduct(View view){
        Intent i = new Intent(this , BuisnessGenerateNewProduct.class);
        startActivity(i);
    }

    public void genQrForOldProducts(View view){
        Intent i = new Intent(this,EditProductDetails.class);
        startActivity(i);
    }

    public void readCustomerQrDetails(View view){
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                String obj = result.getContents();
                Intent i = new Intent(this , BuisnessAddAmount.class);
                i.putExtra("custId",obj);
                startActivity(i);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buisness_dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buisness_logout:
                mAuth.signOut();
                Intent i = new Intent(buisnessDashboard.this,MainActivity.class);
                startActivity(i);
                this.finish();
                return true;
            case R.id.buisness_Inventory:
                Intent i2 = new Intent(this,Inventory.class);
                startActivity(i2);
                return true;
            case R.id.action_settings:
                Intent i1 = new Intent(this,BuisnessProfile.class);
                startActivity(i1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EXIT THE APP");
        builder.setIcon(R.drawable.ic_close_black_24dp);
        builder.setMessage("Are You Sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                buisnessDashboard.this.finishAndRemoveTask();
            }
        });
        builder.setNegativeButton("NO",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Intent i  = new Intent(this,MainActivity.class);
            startActivity(i);
        }else{
            Log.d("main","user not logged");
        }
    }
}
