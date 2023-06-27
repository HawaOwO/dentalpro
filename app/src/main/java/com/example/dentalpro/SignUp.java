package com.example.dentalpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    EditText userFName, userLName ,userEmail,userPw,userPhone, userPwConfirm;
    Button SignUpBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userFName     = findViewById(R.id.Fname);
        userLName     = findViewById(R.id.Lname);
        userEmail     = findViewById(R.id.EmailSignup);
        userPw        = findViewById(R.id.PwSignup);
        userPwConfirm = findViewById(R.id.ConfirmPwSignup);
        userPhone     = findViewById(R.id.Mnumber);
        SignUpBtn     = findViewById(R.id.Signupbutton);
        progressBar   = findViewById(R.id.progressBar3);

        fAuth = FirebaseAuth.getInstance();

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//            finish();
//        }

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = userEmail.getText().toString().trim();
                String password = userPw.getText().toString().trim();
                String passwordC = userPwConfirm.getText().toString().trim();
                final String firstName = userFName.getText().toString();
                final String lastName = userLName.getText().toString();
                final String phone    = userPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    userPw.setError("Password is required");
                    return;
                }

                if(password.length() < 8){
                    userPw.setError("Password must be more than 8 characters");
                    return;
                }
                if(!password.equals(passwordC)){
                    userPwConfirm.setError("Password must be the same");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this, "Account is created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity2.class));
                        }else {
                            Toast.makeText(SignUp.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

}