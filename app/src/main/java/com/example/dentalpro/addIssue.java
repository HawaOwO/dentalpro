package com.example.dentalpro;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addIssue extends AppCompatActivity {
    EditText name, username, description, date, status, solver, day, month, year;
    Button btnAdd, btnBack;
    Spinner statusSpinner, spinnerMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        name = (EditText) findViewById(R.id.txtName);
        username = (EditText)findViewById(R.id.txtUsername);
        description = (EditText) findViewById(R.id.txtDesc);
        //date = (EditText)findViewById(R.id.txtDate);
        //status= (EditText)findViewById(R.id.txtStatus);
        day =(EditText)findViewById(R.id.txtDay);
        year =(EditText)findViewById(R.id.txtYear);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBack);
        spinnerMonth = findViewById(R.id.spinnerMonth);
//        statusSpinner = findViewById(R.id.spinnerStatus);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        statusSpinner.setAdapter(adapter);

        // Set the selection for the Spinner based on the month
        ArrayAdapter<CharSequence> MonthAdapter = ArrayAdapter.createFromResource(addIssue.this, R.array.month_array, android.R.layout.simple_spinner_item);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(MonthAdapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                clearAll();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void insertData() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("username", username.getText().toString());
        map.put("description", description.getText().toString());
        //map.put("date", date.getText().toString());
        //map.put("date", ServerValue.TIMESTAMP);
        map.put("status", "UNSOLVED");
        //map.put("solver", "UNSOLVED");
        map.put("day", day.getText().toString());
        map.put("year", year.getText().toString());
        map.put("month", spinnerMonth.getSelectedItem().toString());

        FirebaseDatabase.getInstance().getReference().child("Issue")
                .push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(name.getContext(), "Data Insert Succesfully", Toast.LENGTH_SHORT).show();
                        // Set the result to indicate success
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(name.getContext(), "Error while inserting", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void clearAll() {
        name.setText("");
        username.setText("");
        description.setText("");
        day.setText("");
        year.setText("");
        spinnerMonth.setSelection(0);
        //date.setText("");
        //statusSpinner.setSelection(0);
    }

//    private String getCurrentUsername() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userEmail = currentUser.getEmail();
//
//            // Create an instance of the User class using the constructor that takes an email
//            User currentUserInstance = new User(userEmail);
//
//            // Now 'currentUserInstance' contains the additional information from the Realtime Database
//            return currentUserInstance.getUsername();
//        }
//        return null; // Handle the case when there is no current user
//    }

//    private void getCurrentUsername(UsernameCallback callback) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userEmail = currentUser.getEmail();
//            User currentUserInstance = new User(userEmail, new User.UserDataCallback() {
//                @Override
//                public void onUserDataReceived(User user) {
//                    // Callback is triggered here, but we handle it in the next callback
//
//                }
//            });
//
//            // Retrieve username using callback
//            currentUserInstance.retrieveUserDataFromDatabase(new User.UserDataCallback() {
//                @Override
//                public void onUserDataReceived(User user) {
//                    if (user != null) {
//                        String username = user.getUsername();
//                        callback.onUsernameReceived(username);
//                    } else {
//                        // Handle the case when user data is not available
//                        callback.onUsernameReceived(null);
//                    }
//                }
//            });
//        } else {
//            // Handle the case when there is no current user
//            callback.onUsernameReceived(null);
//        }
//    }

//    private String getCurrentUsername() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userEmail = currentUser.getEmail(); // Use email as the unique identifier
//
//            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
//
//            // Query users by email
//            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        User user = userSnapshot.getValue(User.class);
//
//                        // Display user data in the EditText fields
//                        if (user != null) {
//                            return user.getUsername();
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle errors
//                }
//            });
//        }
//    }

//    private void getCurrentUsername(UsernameCallback callback) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userEmail = currentUser.getEmail(); // Use email as the unique identifier
//
//            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
//
//            // Query users by email
//            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        User user = userSnapshot.getValue(User.class);
//
//                        // Display user data in the EditText fields
//                        if (user != null) {
//                            String username = user.getUsername();
//                            callback.onUsernameReceived(username);
//                            return; // Exit loop after finding the user
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle errors
//                    callback.onUsernameReceived(null); // Pass null to callback in case of error
//                }
//            });
//        } else {
//            // Handle the case when there is no current user
//            callback.onUsernameReceived(null); // Pass null to callback when there is no current user
//        }
//    }
//
//    // Define a callback interface to retrieve the username asynchronously
//    interface UsernameCallback {
//        void onUsernameReceived(String username);
//    }

}
