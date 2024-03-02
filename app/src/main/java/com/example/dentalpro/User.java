package com.example.dentalpro;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String profilePicture;
    private String username;
    // Add more fields if needed

    public User() {
        // Required empty constructor for Firebase
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(String email) {
        this.email = email;
        retrieveUserDataFromDatabase();
    }
    private void retrieveUserDataFromDatabase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    // Update the fields of the current user instance
                    if (user != null) {
                        username = user.getUsername();
                        firstName= user.getFirstName();
                        lastName = user.getLastName();
                        phone= user.getPhone();
                        role= user.getRole();
                        profilePicture= user.getProfilePicture();
                        // Update other fields as needed
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

//    public interface UserDataCallback {
//        void onUserDataReceived(User user);
//    }
//
//    public void retrieveUserDataFromDatabase(UserDataCallback callback) {
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
//        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//
//                    // Update the fields of the current user instance
//                    if (user != null) {
//                        username = user.getUsername();
//                        firstName = user.getFirstName();
//                        lastName = user.getLastName();
//                        phone = user.getPhone();
//                        role = user.getRole();
//                        profilePicture = user.getProfilePicture();
//                        // Update other fields as needed
//                        callback.onUserDataReceived(user);
//                        return; // Return after finding the matching user
//                    }
//                }
//                // If no matching user is found
//                callback.onUserDataReceived(null);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle errors
//                callback.onUserDataReceived(null);
//            }
//        });
//    }

}
