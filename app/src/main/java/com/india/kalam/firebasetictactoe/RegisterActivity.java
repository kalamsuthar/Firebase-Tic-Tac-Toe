package com.india.kalam.firebasetictactoe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUsername;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseDatabaseInstance;

    private String userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance();

        // if already logged in go to choose player screen
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, ChoosePlayerActivity.class));
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername = (EditText) findViewById(R.id.username);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    public void onRegisterClicked(View view) {

        String emailInput = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        final String username = inputUsername.getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        mAuth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("MYTag", task.getException().toString());
                        } else {

                            // get reference to 'users' node
                            mFirebaseDatabase = mFirebaseDatabaseInstance.getReference("users");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // add username, email to database
                            userId = user.getUid();
                            email = user.getEmail();

                            User myUser = new User(username, email, userId);

                            mFirebaseDatabase.child(userId).setValue(myUser);

                            startActivity(new Intent(RegisterActivity.this, ChoosePlayerActivity.class));
                            finish();
                        }
                    }
                });
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
