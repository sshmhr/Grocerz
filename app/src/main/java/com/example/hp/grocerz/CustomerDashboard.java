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

public class CustomerDashboard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference myUserDatabaseRef;
    private TextView balance;
    private TextView name;
    private IntentIntegrator qrScan;
    private SQLiteDatabase db;
    private DatabaseReference myProdRef;
    private DatabaseReference child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myUserDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initElements();
        fillDashboard();
        initDatabase();
    }

    private void initDatabase() {
        db = openOrCreateDatabase("testproducts",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cart"+mUser.getUid()+"(prodId VARCHAR PRIMARY KEY,prodName VARCHAR,count int,availableCount int,price varchar);");
    }

    private void initElements() {
        myProdRef = FirebaseDatabase.getInstance().getReference("product");
        name = findViewById(R.id.customer_name_view);
        balance = findViewById(R.id.customer_balance_view);
    }

    public void scanProduct(View view){
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Product Not Found Contact the Manager", Toast.LENGTH_LONG).show();
            } else {
                String productId = result.getContents();
                addProductToCart(productId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addProductToCart(String productId) {
        Cursor c = db.rawQuery("Select * from cart"+mUser.getUid()+" where prodId = '" + productId + "';",null) ;
        if(c.getCount() == 0 ){
            addNewProductToDB(productId);
        }else{
            c.moveToFirst();
            int count = c.getInt(2);
            int availableCount = c.getInt(3);
            if(count+1>availableCount) Toast.makeText(this, "Item count greater than inventory contact nmanager", Toast.LENGTH_SHORT).show();
            else db.execSQL("update cart"+mUser.getUid()+" set count = count + 1 where prodId='" + productId + "';");
        }
    }

    private void addNewProductToDB(final String productId) {
        child = myProdRef.child(productId);
        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product x = dataSnapshot.getValue(Product.class);
                String prodName = x.name;
                double price =  x.price;
                int quantity =  + x.quantity ;
                db.execSQL("insert into cart" + mUser.getUid() + " values('" + productId + "','" + prodName + "',1," + quantity + ",'" + price + "');" );
                Toast.makeText(CustomerDashboard.this, "Added  " + prodName + " to Cart", Toast.LENGTH_SHORT).show();;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    public void moveToCart(View view){
        Intent i = new Intent(this,CustomerCart.class);
        startActivity(i);

    }

    private void fillDashboard() {
        Query query = myUserDatabaseRef.child("customer").orderByChild("email").equalTo(mUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot buisness : dataSnapshot.getChildren()) {
                        Customer x  = buisness.getValue(Customer.class);
                        name.setText(x.name);
                        balance.setText("" + x.balance);}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerDashboard.this, "Error " + databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.customer_logout:
                mAuth.signOut();
                Intent i = new Intent(CustomerDashboard.this,MainActivity.class);
                startActivity(i);
                this.finish();
                return true;
            case R.id.customer_QR:
                Intent i2 = new Intent(this,CustomerQR.class);
                startActivity(i2);
                return true;
            case R.id.customer_settings:
                Intent i1 = new Intent(CustomerDashboard.this,CustomerProfile.class);
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
                CustomerDashboard.this.finishAndRemoveTask();
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
        }
    }

}
