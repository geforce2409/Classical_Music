package com.mobile.absoluke.Classiq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import tool.Tool;


/**
 * Created by Yul Lucia on 03/19/2018.
 */

public class SigninActivity extends AppCompatActivity {

    //Components
    Button btnLogin, btnSignUp;
    private TextInputEditText edtEmail, edtPassword;
    private TextInputLayout edtlEmail, edtlPassword;
    private View ViewProgress;
    private View ViewSignInForm;

    //Firebase
    FirebaseAuth mAuth;

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        /////////////////// Check Internet connection///////////////////////

        if (!isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("You are not connected to Internet!!!");

            builder.setPositiveButton(R.string.permission_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    finish();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    finish();
                }
            });

            builder.show();
        } else {
            //Anh xa component
            matchComponents();

            //Init FirebaseAuth
            mAuth = FirebaseAuth.getInstance();
        }
    }

    private void matchComponents() {
        //Match components
        btnLogin = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.etLoginEmail);
        edtlEmail = findViewById(R.id.etlLoginEmail);
        edtPassword = findViewById(R.id.etLoginPassword);
        edtlPassword = findViewById(R.id.etlLoginPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        ViewProgress = findViewById(R.id.signInProgress);
        ViewSignInForm = findViewById(R.id.signInForm);

        // Set events
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginConfirm();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(SigninActivity.this, SignUpActivity.class);
            }
        });
    }

    //And outside the class define isConnected()

    private void LoginConfirm() {
        boolean cancel = false;
        View focusView = null;

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
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SigninActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                                Tool.changeActivity(SigninActivity.this, ProfileActivity.class);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ViewSignInForm.setVisibility(show ? View.GONE : View.VISIBLE);
        ViewSignInForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewSignInForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
