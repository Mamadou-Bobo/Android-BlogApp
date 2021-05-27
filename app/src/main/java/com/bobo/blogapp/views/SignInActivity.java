package com.bobo.blogapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bobo.blogapp.R;
import com.bobo.blogapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private TextView txtSignUp;
    private EditText email, password;
    private Button btnSignIn;
    private TextView alertMessage;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        txtSignUp = findViewById(R.id.txtSignUp);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        btnSignIn = findViewById(R.id.btnSignIn);
        alertMessage = findViewById(R.id.errorMessage);

        txtSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> {
            User user = new User();

            user.setEmail(email.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());

            if(!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {
                login(user.getEmail(),user.getPassword());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void login(String email, String password) {
        user = mAuth.getCurrentUser();
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                     if (task.isSuccessful()) {
                         this.db.collection("users")
                                .document(user.getUid())
                                .get()
                                .addOnCompleteListener(this, task1 -> {
                                    if (task1.isSuccessful()) {
                                        System.out.println(task1.getResult());
                                        DocumentSnapshot documentSnapshot = task1.getResult();
                                            Boolean isActivate = (Boolean) documentSnapshot.get("isActivate");
                                            System.out.println(isActivate);
                                            if (isActivate) {
                                                // Sign in success, update UI with the signed-in user's information
                                                FirebaseUser user = this.mAuth.getCurrentUser();
                                                Log.d("test",documentSnapshot.get("role").toString());
                                                if(!documentSnapshot.get("role").equals("admin")) {
                                                    updateUI(user,"notAdmin");
                                                } else {
                                                    updateUI(user,"isAdmin");
                                                }
                                            } else {
                                                alertMessage.setText("Votre compte est désactivé");
                                                updateUI(null,null);
                                            }
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        alertMessage.setText(task.getException().getMessage());
                        //txtSignUp.setText(task.getException().getMessage());
                        updateUI(null, null);
                    }
                });
    }

    private void updateUI(FirebaseUser user, String role) {
        if(user != null) {
            startActivity(role);
        }
    }

    private void startActivity(String role) {
        Intent intent;
        if(role.equals("isAdmin")) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}
