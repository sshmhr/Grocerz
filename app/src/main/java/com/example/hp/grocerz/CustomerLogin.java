package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CustomerLogin extends Activity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        initComponents();
        mAuth = FirebaseAuth.getInstance();
        spinner.setVisibility(GONE);
    }

    public void customerPasswordReset(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();
        if(Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(emailAddress).matches()) {
            spinner.setVisibility(VISIBLE);
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                spinner.setVisibility(GONE);
                                Log.d("password", "Email sent.");
                                Toast.makeText(CustomerLogin.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                                email.setText("");
                                password.setText("");
                            }else{
                                spinner.setVisibility(GONE);
                                Toast.makeText(CustomerLogin.this, "Error  " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                email.setText("");
                                password.setText("");
                            }
                        }

                    });
        }else{
            email.setText("");
            Toast.makeText(this, "Please enter a proper email", Toast.LENGTH_SHORT).show();
        }
    }


    public void customerSignup(View v){
        Intent i = new Intent(this,CustomerSignup.class);
        startActivity(i);
    }

    private void initComponents() {
        email = findViewById(R.id.cust_edit_phone);
        password = findViewById(R.id.prod_edit_price);
        spinner = findViewById(R.id.progressBar3);
    }

    public void customerLogin(View v) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child("customer").orderByChild("email").equalTo(email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    spinner.setVisibility(VISIBLE);
                    Task<AuthResult> x = mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                    OnCompleteListener<AuthResult> z = new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                spinner.setVisibility(GONE);
                                Log.d("customerLogin", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(CustomerLogin.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(CustomerLogin.this, CustomerDashboard.class);
                                startActivity(i);
                                CustomerLogin.this.finish();
                            } else {
                                spinner.setVisibility(GONE);
                                Log.w("customer login", "signInWithEmail:failure", task.getException());
                                Toast.makeText(CustomerLogin.this, "Authentication failed." + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    x.addOnCompleteListener(CustomerLogin.this,z);
                }else{
                    Toast.makeText(CustomerLogin.this, "This email does not correspond to a customer",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerLogin.this, "Error : " + databaseError.getMessage() + " Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Toast.makeText(this, "User alrady logged in , please logout first If you want to login with a different Id", Toast.LENGTH_SHORT).show();
            Intent i  = new Intent(this,CustomerDashboard.class);
            startActivity(i);
            this.finish();
        }
    }

}

