package com.example.dentalpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class fragmentProfile extends Fragment {

    private DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText txtUsername, txtEmail, txtPhoneNumber, txtNewPassword, txtNewPicture; // Add other EditText fields as needed
    Button btnUpdate, btnUpdatePw; // Add the update button reference
    ImageView profilepic;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize EditText fields
        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhoneNumber = view.findViewById(R.id.txtPNumber);
        txtNewPassword = view.findViewById(R.id.txtPassword);
        txtNewPicture = view.findViewById(R.id.txtPic);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdatePw = view.findViewById(R.id.btnUpdatePw);
        profilepic= view.findViewById(R.id.profile);
        // Add other EditText fields as needed

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        // Retrieve and display user data
        displayUserData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        btnUpdatePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        return view;
    }
    private void displayUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Use email as the unique identifier

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");

            // Query users by email
            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);

                        // Display user data in the EditText fields
                        if (user != null) {
                            txtUsername.setText(user.getUsername());
                            txtEmail.setText(user.getEmail());
                            txtPhoneNumber.setText(user.getPhone());
                            txtNewPicture.setText(user.getProfilePicture());

                            // Load profile picture using Picasso
                            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                                Picasso.get().load(user.getProfilePicture()).into(profilepic);
                            } else {
                                // Set a default image if no profile picture URL is available
                                profilepic.setImageResource(R.drawable.baseline_person_24);
                            }
                            // Set other fields as needed
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }
    }
    private void updateUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Use email as the unique identifier

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");

            // Query users by email
            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);

                        // Update user data
                        if (user != null) {
                            user.setUsername(txtUsername.getText().toString());
                            user.setPhone(txtPhoneNumber.getText().toString());
                            user.setProfilePicture(txtNewPicture.getText().toString());
                            // Update other fields as needed

                            // Save changes to the database
                            userSnapshot.getRef().setValue(user);
                            Toast.makeText(requireContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                    Toast.makeText(requireContext(), "Error updating data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // Add a new method for updating the password
    private void updatePassword() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String newPassword = txtNewPassword.getText().toString();

            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}