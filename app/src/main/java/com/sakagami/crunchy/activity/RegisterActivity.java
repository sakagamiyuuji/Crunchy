package com.sakagami.crunchy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sakagami.crunchy.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFirstname, edtLastname, edtEmail, edtPass, edtConfirmPass;
    CardView cvRegister;

    String firstName, lastName, email, password, confirmPassword;
    String userId;

    FirebaseAuth fbAuth;
    FirebaseFirestore fbFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialComponent();
        onClick();


    }

    private void onClick(){
        cvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInputData();
                if (getInputData() == true){

                    fbAuth = FirebaseAuth.getInstance();
                    fbFirestore = FirebaseFirestore.getInstance();

                    fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Failed Registrasi", Toast.LENGTH_SHORT).show();
                            } else{
                                //Toast.makeText(RegisterActivity.this, "Success Registrasi", Toast.LENGTH_SHORT).show();


                                userId = fbAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("firstname", firstName);
                                user.put("lastname", lastName);
                                user.put("email", email);
                                user.put("password", password);

                                fbFirestore.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("userCreated", "User Create added with ID: " + documentReference.getId());

                                                Toast.makeText(RegisterActivity.this, "Succes Register", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                Log.w("userError", "Error adding document", e);
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean getInputData() {
        firstName = edtFirstname.getText().toString().trim();
        lastName = edtLastname.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        password = edtPass.getText().toString().trim();
        confirmPassword = edtConfirmPass.getText().toString().trim();

        boolean isValid = true;

        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError("Email is Required !");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            edtPass.setError("Password is Required !");
            isValid = false;
        }

        if (password.length() < 6) {
            edtPass.setError("Password must be >= 6 characters");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPass.setError("Password tidak cocok");
            isValid = false;

        }

        return isValid;

    }



    private void initialComponent(){
        edtFirstname = findViewById(R.id.edt_firstname);
        edtLastname = findViewById(R.id.edt_lastname);
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_password);
        edtConfirmPass = findViewById(R.id.edt_confirmpassword);

        cvRegister = findViewById(R.id.cv_register);
    }
}