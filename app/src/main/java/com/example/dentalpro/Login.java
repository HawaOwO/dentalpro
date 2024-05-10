package com.example.dentalpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {
    EditText userEmail, userPw;
    Button LoginBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    //ImageView google_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.Email);
        userPw = findViewById(R.id.Password);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        LoginBtn = findViewById(R.id.button);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userEmail.getText().toString().trim();
                String password = userPw.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    userPw.setError("Password is required");
                    return;
                }

                if (password.length() < 8) {
                    userPw.setError("Password must be over than 8 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Login.this, "Welcome back user", Toast.LENGTH_SHORT).show();
//
//                            String userEmail = fAuth.getCurrentUser().getEmail();
//                            retrieveUserDataFromDatabase(userEmail);
//                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
//
//                        } else {
//                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
                        if (task.isSuccessful()) {
                            // Step 2: Retrieve user's email from authenticated user
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userEmail = currentUser.getEmail();

                                // Step 3: Query Realtime Database to get additional user information
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
                                usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            User user = userSnapshot.getValue(User.class);

                                            // Now 'user' contains the additional information from the Realtime Database

                                            Toast.makeText(Login.this, "Welcome back, " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle errors
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(Login.this, "Invalid credentials or account doesn't exist", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }


                });

            }
        });

//        google_img = findViewById(R.id.google);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        google_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SignInWithGoogle();
//            }
//        });



    }
//    private void SignInWithGoogle() {
//        Intent intent = gsc.getSignInIntent();
//        startActivityForResult(intent, 100);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100) {
           Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
           try {
               //task.getResult(ApiException.class);
//               HomeActivity();
               //Toast.makeText(Login.this, "Welcome back user", Toast.LENGTH_SHORT).show();
               //startActivity(new Intent(getApplicationContext(), MainActivity2.class));
               GoogleSignInAccount account = task.getResult(ApiException.class);
               firebaseAuthWithGoogle(account);
           } catch (ApiException e) {
               //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
               Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               progressBar.setVisibility(View.GONE);
           }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            // Continue with your app logic or redirect to the main activity
                            Toast.makeText(Login.this, "Welcome back user", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                        } else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void retrieveUserDataFromDatabase(String userEmail) {
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
//
//        // Use orderByChild and equalTo to find the user with the matching email
//        userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Iterate through the results (there should be only one result)
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//
//                    // Now you have the additional user data (username, number, role)
//                    if (user != null) {
//                        String username = user.getUsername();
//                        String firstName = user.getFirstName();
//                        String lastName = user.getLastName();
//                        String email = user.getEmail();
//                        String phone = user.getPhone();
//                        String role = user.getRole();
//                        String profilePicture = user.getProfilePicture();
//
//
//                        // Do something with the additional user data
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors
//            }
//        });
//    }


}