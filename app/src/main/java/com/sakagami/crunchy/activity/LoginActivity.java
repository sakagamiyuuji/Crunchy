package com.sakagami.crunchy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sakagami.crunchy.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail;
    TextInputEditText edtPassword;
    Button btnLogin;
    TextView tvRegister;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener fireAuthListener;
    FirebaseUser mFirebaseUser;
    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;

    private int CODE_SIGN_IN = 1;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialComponent();
        GsoBuilder();
        onClick();


    }

    private void onClick(){

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                if (edtEmail.getText().toString().isEmpty()){
                    edtEmail.setError("Email tidak boleh kosong");
                }else if (password.isEmpty()){
                    edtPassword.setError("Password tidak boleh kosong");
                }else{
                    loginCek();
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent =googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, CODE_SIGN_IN);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginCek(){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "Gagal Login, Cek Email dan Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginValidate();
    }

    private void GsoBuilder(){
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("78563478266-82qu6hrsgg5436ptcc43p39bn4kvb4ie.apps.googleusercontent.com")
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginValidate(){
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser!=null){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialComponent(){
        edtEmail = findViewById(R.id.input_email);
        edtPassword = findViewById(R.id.input_password);
        tvRegister = findViewById(R.id.tv_register);
        btnLogin = findViewById(R.id.btn_login);
        signInButton = findViewById(R.id.btn_login_gooogle);
    }

    private void getData(){
        email = edtEmail.getText().toString();
        password = String.valueOf(edtPassword.getText());
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleLoginGoogle(task);
        }
    }

    private void handleLoginGoogle(Task<GoogleSignInAccount> completedTask){

        try {
            GoogleSignInAccount gsi = completedTask.getResult(ApiException.class);
//            Toast.makeText(this, "Berhasil Login", Toast.LENGTH_SHORT).show();
            firebaseGoogleAuth(gsi);
        }catch (ApiException e){
            Toast.makeText(this, "Gagal Login", Toast.LENGTH_SHORT).show();
//            firebaseGoogleAuth(null);
        }
    }

    private void firebaseGoogleAuth(GoogleSignInAccount gsi){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(gsi.getIdToken(), null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                }else{
                    Toast.makeText(LoginActivity.this, "Gagal Login", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String email = account.getEmail();
            String idUser = account.getId();
            Uri photoUser = account.getPhotoUrl();
//            Toast.makeText(this, personName, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.putExtra("name", personName);
            startActivity(intent);
            finish();

        }
    }
}