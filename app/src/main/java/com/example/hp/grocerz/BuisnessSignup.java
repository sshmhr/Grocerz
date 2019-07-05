package com.example.hp.grocerz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class BuisnessSignup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private TextView error;
    private EditText address;
    private EditText name;
    private EditText phone;
    private EditText password;
    private EditText email;
    String errorMsg = "" ;
    private String inEmail;
    private String inPassword;
    private String inPhone;
    private String inName;
    private String inAddress;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_signup);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        initElements();
        progress.setVisibility(View.GONE);
    }

    private void initElements() {
        email = findViewById(R.id.cust_edit_phone);
        password = findViewById(R.id.prod_edit_price);
        phone = findViewById(R.id.cust_edit_pno);
        name = findViewById(R.id.prod_edit_name);
        address = findViewById(R.id.text_buisness_address);
        error = findViewById(R.id.text_buisness_error);
        progress = findViewById(R.id.progressBar);
    }

    public void buisnessSignup(View v){
        error.setText("");
        if(validateDetails()){
            Log.d("val","details validated");
            saveBuisnessToFirebaseAuth();
        }else{
            error.setVisibility(View.VISIBLE);
            error.setText(errorMsg);
            errorMsg = "" ;
        }
    }

    private void saveBuisnessToFirebaseAuth() {
        progress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(inEmail, inPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveBuisnessToFirebaseDB();
                            progress.setVisibility(View.GONE);
                            Log.d("buisnessAuth", "createUserWithEmail:success");
                            Toast.makeText(BuisnessSignup.this, "Signup successfull welcome " + inName , Toast.LENGTH_LONG).show();
                            Intent i  = new Intent(BuisnessSignup.this,buisnessDashboard.class);
                            startActivity(i);
                        } else {
                            progress.setVisibility(View.GONE);
                            Log.w("buisnessAuth", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(BuisnessSignup.this, "Signup failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveBuisnessToFirebaseDB() {
        Buisness newBuisness = new Buisness(inName,inEmail,inPhone,inAddress);
        myRef.child("buisness").push().setValue(newBuisness);
    }

    private boolean validateDetails() {
        boolean isValidated = true ;
        inEmail = email.getText().toString();
        inPassword = password.getText().toString();
        inPhone = phone.getText().toString();
        inName = name.getText().toString();
        inAddress = address.getText().toString();

        if(!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(inEmail).matches()){
            email.setText("");
            errorMsg+="invalid Email.  ";
            isValidated = false;
        }

        if(!Pattern.compile("^.{4,15}$").matcher(inPassword).matches()){
            password.setText("");
            errorMsg+="Password length should be between 4 to 15 characters.  ";
            isValidated = false;
        }

        if(inPhone.length() != 10 ){
            phone.setText("");
            errorMsg+="phone no should have 10 digits  ";
            isValidated = false ;
        }

        if(inName.length()==0){
            name.setText("");
            errorMsg+="name cannot be kept blank  ";
            isValidated = false ;
        }

        if(inAddress.length()==0){
            address.setText("");
            errorMsg+="address cannot be kept blank  ";
            isValidated = false ;
        }

        return isValidated;
    }
}
