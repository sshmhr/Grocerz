package com.example.hp.grocerz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }
    public void customerLogin(View view){
        Intent i = new Intent(this,CustomerLogin.class);
        startActivity(i);
    }
    public void buisnessLogin(View view){
        Intent i = new Intent(this,BuisnessLogin.class);
        startActivity(i);
    }
        @Override
    public void onStart() {
        super.onStart();
        progressbar = findViewById(R.id.progressBar4);
        progressbar.setVisibility(GONE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
            myRef = FirebaseDatabase.getInstance().getReference();
        if(currentUser!=null){
            progressbar.setVisibility(VISIBLE);
            findViewById(R.id.button).setClickable(false);
            findViewById(R.id.button2).setClickable(false);
            Query query = myRef.child("buisness").orderByChild("email").equalTo(currentUser.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Intent i = new Intent(MainActivity.this,buisnessDashboard.class);
                        startActivity(i);
                        MainActivity.this.finish();
                    }else{
                        Intent i = new Intent(MainActivity.this,CustomerDashboard.class);
                        startActivity(i);
                        MainActivity.this.finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    findViewById(R.id.button).setClickable(true);
                    findViewById(R.id.button2).setClickable(true);
                    progressbar.setVisibility(INVISIBLE);
                    Toast.makeText(MainActivity.this, "Error " + databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Log.d("main","user not logged");
        }
    }

}
