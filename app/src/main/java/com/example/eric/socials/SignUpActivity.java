package com.example.eric.socials;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button signUpButton2;
    private EditText emailView2;
    private EditText passwordView2;

    @Override
    public void onClick(View v){            // Handles Button clicks for signing up
        switch (v.getId()) {
            case R.id.signUpButton2:
                attemptSignUp();
                Utils.progressBar(this, getString(R.string.signup_progressbar));
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d("Login Status", "onAuthChanged: signed_in" + user.getUid());
                } else {
                    Log.d("Login Status", "onAuthChanged: signed_out");
                }
            }
        };

        signUpButton2 = (Button) findViewById(R.id.signUpButton2);
        signUpButton2.setOnClickListener(this);
    }

    private void attemptSignUp(){                                   // Helper Function for signing a user up
        emailView2 = (EditText) findViewById(R.id.emailView2);
        final String email = emailView2.getText().toString();

        passwordView2 = (EditText) findViewById(R.id.passwordView2);
        String password = passwordView2.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Sign Up", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.signup_successful), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, UpdateProfile.class);
                                intent.putExtra(getString(R.string.email), email);
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
