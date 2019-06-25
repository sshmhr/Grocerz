package com.example.hp.grocerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class BuisnessProfile extends AppCompatActivity {

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
    private FirebaseUser mUser;
    private String currentEmail;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buisness_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        initElements();
        populateTextfields();
    }

    private void initElements() {
        email = findViewById(R.id.prod_edit_store);
        password = findViewById(R.id.prod_edit_price);
        phone = findViewById(R.id.prod_edit_store);
        name = findViewById(R.id.prod_edit_name);
        address = findViewById(R.id.text_buisness_address);
        error = findViewById(R.id.text_buisness_error);
        progress = findViewById(R.id.progressBar1);
    }

    private void populateTextfields() {
        currentEmail = mUser.getEmail();
        email.setText(currentEmail);
        progress.setVisibility(VISIBLE);
        query = myRef.child("buisness").orderByChild("email").equalTo(currentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progress.setVisibility(GONE);
                    for (DataSnapshot buisness : dataSnapshot.getChildren()) {
                        Buisness x  = buisness.getValue(Buisness.class);
                        address.setText(x.address);
                        phone.setText(x.phone);
                        name.setText(x.name);
                    }
                }else{
                    progress.setVisibility(GONE);
                    Toast.makeText(BuisnessProfile.this, "This email does not correspond to a buisness", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progress.setVisibility(GONE);
                Toast.makeText(BuisnessProfile.this, "Error " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void buisnessProfileUpdate(View view) {
        if (validateDetails()) {
            progress.setVisibility(VISIBLE);
            mUser.updatePassword(password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (!email.getText().toString().equals(currentEmail)) {
                                    mUser.updateEmail(email.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(BuisnessProfile.this, "emailAndPasssword Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(BuisnessProfile.this, "Passsword Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
            final Buisness updatedBuisness = new Buisness(name.getText().toString(), email.getText().toString(), phone.getText().toString(), address.getText().toString());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot cust : dataSnapshot.getChildren()) {
                            myRef.child("buisness").child(cust.getKey()).setValue(updatedBuisness)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(BuisnessProfile.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(BuisnessProfile.this, buisnessDashboard.class);
                                            startActivity(i);
                                            BuisnessProfile.this.finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(BuisnessProfile.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            progress.setVisibility(View.GONE);
        }else{
            Toast.makeText(this, "error is " + errorMsg, Toast.LENGTH_SHORT).show();
            errorMsg = "" ;
    }
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
