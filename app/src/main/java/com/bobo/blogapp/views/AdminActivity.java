package com.bobo.blogapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bobo.blogapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        btnLogout = findViewById(R.id.btnLogout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        //gcloud projects add-iam-policy-binding blogapp-90aa5 \--member=serviceAccount:service-project_number@firebase-rules.iam.gserviceaccount.com \--role=roles/firebaserules.system

        db.collection("users")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                            Log.d("test", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        }
                    } else {
                        Log.d("test", "Error getting documents: ", task1.getException());
                    }
                });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });


    }


}