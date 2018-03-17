package com.mobile.absoluke.Classiq;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FacebookAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import tool.Tool;
//
//public class LoginActivity extends AppCompatActivity {
//
//    //Debug
//    private String TAG = "Facebook";
//    private LoginButton fbSignInBtn;
//    private CallbackManager mCallbackManager;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //facebook
//        FacebookSdk.sdkInitialize(this.getApplicationContext());
//        AppEventsLogger.activateApp(this);
//
//        setContentView(R.layout.activity_login);
//
//        //Anh xa component
//        matchComponents();
//
//        //Init FirebaseAuth
//        mAuth = FirebaseAuth.getInstance();
//    }
//
//    //See if the user had already logged in
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Check if user is signed in (non-null) and update UI accordingly.
////        Comment lines below for testing
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Log.i(TAG, "userid: " + currentUser.getUid());
//        if (currentUser != null) {
//
//            //CAUTION: Code below only for testing!!!
////            Bundle bundle = new Bundle();
////            bundle.putString("ID", currentUser.getUid());
////            bundle.putString("NAME", currentUser.getDisplayName());
////            bundle.putString("EMAIL", currentUser.getEmail());
////            bundle.putString("PHONE", currentUser.getPhoneNumber());
////            bundle.putString("IMAGE", currentUser.getPhotoUrl().toString());
////            Tool.pushDataAndChangeActivity(LoginActivity.this, RegisterActivity.class, bundle);
//            Tool.changeActivity(this, MainActivity.class);
//        }
////        Stop comment
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void matchComponents(){
//
//        fbSignInBtn = findViewById(R.id.fb_sign_in_button);
//        mCallbackManager = CallbackManager.Factory.create();
//        fbSignInBtn.setReadPermissions("email", "public_profile");
//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//
//                //Xu ly
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//            }
//        });
//
//    }
//
//    private void handleFacebookAccessToken(final AccessToken token){
//        Log.d(TAG, "handleFacebookAccessToken: " + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                            //check if the user had account
//                            if (currentUser != null) {
//                                Bundle bundle = new Bundle();
//                                bundle.putString("ID", currentUser.getUid());
//                                bundle.putString("NAME", currentUser.getDisplayName());
//                                bundle.putString("EMAIL", currentUser.getEmail());
//                                bundle.putString("PHONE", currentUser.getPhoneNumber());
//                                bundle.putString("IMAGE", currentUser.getPhotoUrl().toString());
//
//
//                                Tool.pushDataAndChangeActivity(LoginActivity.this, RegisterActivity.class, bundle);
//                            } else {
//
//                            }
//                        } else {
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//    }
//}

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tool.Tool;

public class LoginActivity extends AppCompatActivity {

    //Components
    Button btnLogin, btnRegister;
    EditText edtEmail, edtPassword;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Anh xa component
        matchComponents();

        //Init FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    private void matchComponents(){
        //Match components
        btnLogin = findViewById(R.id.btnLoginConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        edtEmail = findViewById(R.id.txtLoginEmail);
        edtPassword = findViewById(R.id.txtLoginPassword);


        // Set events
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginConfirm();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(LoginActivity.this, RegisterActivity.class);
            }
        });
    }

    private void LoginConfirm() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Authentication successed.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}