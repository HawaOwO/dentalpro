package com.example.dentalpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignUp extends AppCompatActivity {

    EditText userFName, userLName ,userEmail,userPw,userPhone, userPwConfirm;
    Button SignUpBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    ImageView profile_img, profile_btn;
    private static final int RESULT_LOAD_IMG = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);

        userFName = findViewById(R.id.Fname);
        userLName = findViewById(R.id.Lname);
        userEmail = findViewById(R.id.EmailSignup);
        userPw = findViewById(R.id.PwSignup);
        userPwConfirm = findViewById(R.id.ConfirmPwSignup);
        userPhone = findViewById(R.id.Mnumber);
        SignUpBtn = findViewById(R.id.Signupbutton);
        progressBar = findViewById(R.id.progressBar3);
        profile_img = findViewById(R.id.profile);
        profile_btn = findViewById(R.id.profilebutton);

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
                final String phone = userPhone.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    userPw.setError("Password is required");
                    return;
                }

                if (password.length() < 8) {
                    userPw.setError("Password must be more than 8 characters");
                    return;
                }
                if (!password.equals(passwordC)) {
                    userPwConfirm.setError("Password must be the same");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Account is created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                        } else {
                            Toast.makeText(SignUp.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Apply circular mask to the selected image
                Bitmap circularBitmap = Bitmap.createBitmap(selectedImage.getWidth(), selectedImage.getHeight(), Bitmap.Config.ARGB_8888);
                BitmapShader shader = new BitmapShader(selectedImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);

                Canvas canvas = new Canvas(circularBitmap);
                float radius = Math.min(selectedImage.getWidth(), selectedImage.getHeight()) / 2f;
                canvas.drawCircle(selectedImage.getWidth() / 2f, selectedImage.getHeight() / 2f, radius, paint);

                profile_img.setImageBitmap(circularBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SignUp.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(SignUp.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}