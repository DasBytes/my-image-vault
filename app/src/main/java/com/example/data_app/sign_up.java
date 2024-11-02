package com.example.data_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class sign_up extends AppCompatActivity {
    TextInputEditText etRegName;
    TextInputEditText etRegemail;
    TextInputEditText etRegpassword;
    TextView tvloginhere;
    Button btnregister;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etRegName = findViewById(R.id.username);
        etRegemail = findViewById(R.id.email);
        etRegpassword = findViewById(R.id.password);
        tvloginhere = findViewById(R.id.log_in_page);
        btnregister = findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createuser();
            }
        });

        tvloginhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(sign_up.this, login.class));
            }
        });
    }

    private void createuser() {
        String name = etRegName.getText().toString();
        String email = etRegemail.getText().toString();
        String password = etRegpassword.getText().toString();


        if (TextUtils.isEmpty(name)) {
            etRegName.setError("Name cannot be empty");
            etRegName.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            etRegemail.setError("Email cannot be empty");
            etRegemail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegpassword.setError("Password cannot be empty");
            etRegpassword.requestFocus();
        } else if (password.length() < 6) {
            etRegpassword.setError("Password must be at least 6 characters long");
            etRegpassword.requestFocus();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, String> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);

                        databaseReference.child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> verificationTask) {
                                            if (verificationTask.isSuccessful()) {
                                                Toast.makeText(sign_up.this,
                                                        "Registration successful. Please check your email for verification.",
                                                        Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(sign_up.this, login.class));
                                                finish();
                                            } else {
                                                Toast.makeText(sign_up.this,
                                                        "Failed to send verification email: " + verificationTask.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(sign_up.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(sign_up.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
