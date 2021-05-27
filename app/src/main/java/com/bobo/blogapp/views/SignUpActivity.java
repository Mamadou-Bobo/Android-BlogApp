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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private Button btnSignUp;
    private TextView alertMessage, txtSignIn;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.password_editText);
        btnSignUp = findViewById(R.id.btnSignUp);
        alertMessage = findViewById(R.id.alertMessage);
        txtSignIn = findViewById(R.id.txtSignIn);

        mAuth = FirebaseAuth.getInstance();

        txtSignIn.setOnClickListener(v -> {
            startActivity();
        });

        btnSignUp.setOnClickListener(v -> {
            User user = new User();

            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());

            if(!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {
                createAccount(user.getEmail(),user.getPassword());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void createAccount(String email, String password) {
        db = FirebaseFirestore.getInstance();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Test", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        Map<String, Object> data = new HashMap<>();

                        data.put("id", user.getUid());
                        data.put("isActivate", true);
                        data.put("email", user.getEmail());
                        data.put("createdAt", new Date());
                        data.put("role", "admin");
                        data.put("user", true);

                        db.collection("users")
                                .document(user.getUid())
                                .set(data)
                                .addOnSuccessListener(documentReference -> Log.d("test", "DocumentSnapshot written with ID: " + documentReference))
                                .addOnFailureListener(e -> Log.w("test", "Error adding document", e));

                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        alertMessage.setText(task.getException().getMessage());
                        Log.w("test", "createUserWithEmail:failure", task.getException());
                        updateUI(null);
                    }
                });
}

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            startActivity();
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
