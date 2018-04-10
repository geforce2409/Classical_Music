package com.mobile.absoluke.Classiq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class SignUpActivity extends AppCompatActivity {

    // Components
    Button btnRegister;
    private TextInputEditText edtFirstName, edtLastName, edtEmail, edtPassword, edtPasswordConfirm;
    private TextInputLayout edtlPassword, edtlPasswordConfirm, edtlEmail, edtlLastName, edtlFirstName;
    private View ViewProgress;
    private View ViewSignUpForm;

    // Firebase
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

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void matchComponents() {
        // Match components
        edtFirstName = findViewById(R.id.etRegisterFirstName);
        edtlFirstName = findViewById(R.id.etlRegisterFirstName);
        edtLastName = findViewById(R.id.etRegisterLastName);
        edtlLastName = findViewById(R.id.etlRegisterLastName);
        edtEmail = findViewById(R.id.etRegisterEmail);
        edtlEmail = findViewById(R.id.etlRegisterEmail);
        edtPassword = findViewById(R.id.etRegisterPassword);
        edtlPassword = findViewById(R.id.etlRegisterPassword);
        edtPasswordConfirm = findViewById(R.id.etRegisterPasswordConfirm);
        edtlPasswordConfirm = findViewById(R.id.etlRegisterPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegisterConfirm);
        ViewProgress = findViewById(R.id.signUpProgress);
        ViewSignUpForm = findViewById(R.id.svSignUp);

        // Set events
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterConfirm();
            }
        });
    }

    private void RegisterConfirm() {
        boolean cancel = false;
        View focusView = null;

        // Check empty Last name
        if (TextUtils.isEmpty(edtLastName.getText().toString())) {
            edtlLastName.setError(getText(R.string.required_field));
            focusView = edtlLastName;
            cancel = true;
        } else edtlLastName.setError(null);

        // Check empty First name
        if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
            edtlFirstName.setError(getText(R.string.required_field));
            focusView = edtlFirstName;
            cancel = true;
        } else edtlFirstName.setError(null);

        // Check password confirm
        if (!edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString())) {
            edtlPasswordConfirm.setError(getText(R.string.invalid_password_confirm));
            focusView = edtlPasswordConfirm;
            cancel = true;
        } else edtlPasswordConfirm.setError(null);

        // Check length of password (>6)
        if (edtPassword.getText().length() <= 6) {
            edtlPassword.setError(getText(R.string.invalid_password));
            focusView = edtlPassword;
            cancel = true;
        } else edtlPassword.setError(null);

        // Check valid Email
        if (!isValidEmail(edtEmail.getText().toString())) {
            edtlEmail.setError(getText(R.string.invalid_email));
            focusView = edtlEmail;
            cancel = true;
        } else edtlEmail.setError(null);

        // Check error
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            // Collect info from user
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Collect data and add to database
                                final UserInfo userInfo = new UserInfo();
                                userInfo.setFirstname(edtFirstName.getText().toString());
                                userInfo.setLastname(edtLastName.getText().toString());
                                userInfo.setEmail(edtEmail.getText().toString());
                                userInfo.setCoverLink("https://firebasestorage.googleapis.com/v0/b/classiq-server.appspot.com/o/Default%20avatar%2Fno_cover.png?alt=media&token=f0f16cb5-a6e9-41bf-8366-1b48ce1ba39b");
                                userInfo.setAvatarLink("https://firebasestorage.googleapis.com/v0/b/classiq-server.appspot.com/o/Default%20avatar%2Fimg_placeholder.png?alt=media&token=54494ecf-db27-4cba-b7ef-d930122305d1");
                                userInfo.setRank(0);

                                userInfo.setUserid(mAuth.getCurrentUser().getUid());
                                mUserInfo.child(mAuth.getCurrentUser().getUid()).setValue(userInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Now add "null link" of cover and profile pics to storage

                                                //Direct user to main activity
                                                Toast.makeText(SignUpActivity.this, R.string.sign_up_success, Toast.LENGTH_SHORT).show();
                                                Tool.changeActivity(SignUpActivity.this, ProfileActivity.class);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Direct user to main activity
                                                Toast.makeText(SignUpActivity.this, R.string.sign_up_fail, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, R.string.sign_up_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ViewSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
        ViewSignUpForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewSignUpForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        ViewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        ViewProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
