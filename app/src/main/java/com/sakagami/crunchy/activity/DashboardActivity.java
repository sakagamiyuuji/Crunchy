package com.sakagami.crunchy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sakagami.crunchy.R;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button btnLogout;
    FirebaseUser mFirebaseUser;

    TextView tvName;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initialComponent();
        GsoBuilder();
        getUserData();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void GsoBuilder(){
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("488101877408-jupvh6kvdmn4emo2cotqj1n18te2lt61.apps.googleusercontent.com")
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void logOut() {
        auth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void getUserData(){
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser != null){
            tvName.setText(mFirebaseUser.getEmail());
        } else{
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initialComponent(){
        btnLogout = findViewById(R.id.btn_logout);
        tvName = findViewById(R.id.tv_username);
    }
}