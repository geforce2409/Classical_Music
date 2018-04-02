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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import tool.Tool;


/**
 * Created by Yul Lucia on 03/19/2018.
 */

public class SigninActivity extends AppCompatActivity {

    //Components
    Button btnLogin;
    EditText edtEmail, edtPassword;
    TextView tvRegister;

    //Firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //Anh xa component
        matchComponents();

        //Init FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

    private void matchComponents(){
        //Match components
        btnLogin = findViewById(R.id.btnLoginConfirm);
        edtEmail = findViewById(R.id.etLoginEmail);
        edtPassword = findViewById(R.id.etLoginPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Set events
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { LoginConfirm(); }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.changeActivity(SigninActivity.this, SignupActivity.class);
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
                            Toast.makeText(SigninActivity.this, "Authentication successed.", Toast.LENGTH_SHORT).show();
                            Tool.changeActivity(SigninActivity.this, MainActivity.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
