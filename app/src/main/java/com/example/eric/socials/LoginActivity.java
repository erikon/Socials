package com.example.eric.socials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button loginButton;
    private Button signUpButton;

    private EditText emailView;
    private EditText passwordView;

    @Override
    public void onClick(View v){            // Handles Button clicks for login and signup
        switch (v.getId()) {
            case R.id.loginButton:
                Utils.progressBar(this, getString(R.string.login_progressbar));
                attemptLogin();
                break;

            case R.id.signUpButton:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;

            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {                          // Auth Listener to check if user is already signed in
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    startActivity(intent);
                    Log.d("Login Status", "onAuthChanged: signed_in" + user.getUid());
                } else {
                    Log.d("Login Status", "onAuthChanged: signed_out");
                }
            }
        };

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);

    }

    private void attemptLogin(){                                // Helper function for logging in a user
        emailView = (EditText) findViewById(R.id.emailView);
        final String email = emailView.getText().toString();

        passwordView = (EditText) findViewById(R.id.passwordView);
        String password = passwordView.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Sign In", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("Sign In", "signInWithEmail", task.getException());
                                Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
