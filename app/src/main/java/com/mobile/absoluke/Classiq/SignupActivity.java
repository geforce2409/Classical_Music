package com.mobile.absoluke.Classiq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dataobject.UserInfo;
import tool.Tool;

/**
 * Created by Yul Lucia on 03/19/2018.
 */

public class SignupActivity extends AppCompatActivity {

    //Components
    Button btnRegister;
    EditText edtFirstName, edtLastName,edtEmail, edtPassword, edtPasswordConfirm;
    TextView tvBack;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo = FirebaseDatabase.getInstance().getReference().child("users_info");
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Anh xa component
        matchComponents();

        //Init FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    protected void matchComponents() {
        //Match components
        edtFirstName = findViewById(R.id.etRegisterFirstName);
        edtLastName = findViewById(R.id.etRegisterLastName);
        edtEmail = findViewById(R.id.etRegisterEmail);
        edtPassword = findViewById(R.id.etRegisterPassword);
        edtPasswordConfirm = findViewById(R.id.etRegisterPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegisterConfirm);

        // Set events
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterConfirm();
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(SignupActivity.this, SigninActivity.class);
            }
        });
    }

    private void RegisterConfirm() {
        //Check length of password (>6)
        if (edtPassword.getText().length() <= 6){
            Toast.makeText(this, R.string.wrong_password_length, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password confirm
        if (!edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString())) {
            Toast.makeText(this, R.string.wrong_password_confirm, Toast.LENGTH_SHORT).show();
            return;
        }

        //Collect info from user
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Colect data and add to database
                            final UserInfo userInfo = new UserInfo();
                            userInfo.setFirstname(edtFirstName.getText().toString());
                            userInfo.setLastname(edtLastName.getText().toString());
                            userInfo.setEmail(edtEmail.getText().toString());
                            userInfo.setCoverLink("null");
                            userInfo.setAvatarLink("null");
                            userInfo.setRank(0);

                            userInfo.setUserid(mAuth.getCurrentUser().getUid());
                            mUserInfo.child(mAuth.getCurrentUser().getUid()).setValue(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Now add "null link" of cover and profile pics to storage

                                            //Direct user to main activity
                                            Toast.makeText(SignupActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                                            Tool.changeActivity(SignupActivity.this, MainActivity.class);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Direct user to main activity
                                            Toast.makeText(SignupActivity.this, R.string.signup_fail, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, R.string.signup_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
