package com.example.dentalpro;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addIssue extends AppCompatActivity {
    EditText name, username, description, date, status, solver, day, month, year;
    Button btnAdd, btnBack;
    Spinner statusSpinner, spinnerMonth;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        name = (EditText) findViewById(R.id.txtName);
        //username = (EditText) findViewById(R.id.txtUsername);
        description = (EditText) findViewById(R.id.txtDesc);
        day = (EditText) findViewById(R.id.txtDay);
        year = (EditText) findViewById(R.id.txtYear);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBack);
        spinnerMonth = findViewById(R.id.spinnerMonth);


        // Set the selection for the Spinner based on the month
        ArrayAdapter<CharSequence> MonthAdapter = ArrayAdapter.createFromResource(addIssue.this, R.array.month_array, android.R.layout.simple_spinner_item);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerMonth.setAdapter(MonthAdapter);
        // Set the custom adapter for the spinner
        spinnerMonth.setAdapter(new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.month_array), new int[]{Color.BLACK}));



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

            //validation

            // Check if any of the required fields are empty
            String nameText = name.getText().toString();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid(); // Use UID as the unique identifier
                userEmail = currentUser.getEmail();
                //DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                //String usernameText = userEmail;

            }
            String descriptionText = description.getText().toString();
            String dayText = day.getText().toString();
            String yearText = year.getText().toString();

            if (TextUtils.isEmpty(nameText) ||
                    TextUtils.isEmpty(descriptionText) ||
                    TextUtils.isEmpty(dayText) || TextUtils.isEmpty(yearText)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if day and year are valid numbers
            int dayValue, yearValue;
            try {
                dayValue = Integer.parseInt(dayText);
                yearValue = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Day and year must be valid numbers", Toast.LENGTH_SHORT).show();
                return;
            }


            //
            Map<String, Object> map = new HashMap<>();
            map.put("name", name.getText().toString());
            //map.put("username", username.getText().toString());
            map.put("username", userEmail);
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


        private void clearAll () {
            name.setText("");
            //username.setText("");
            description.setText("");
            day.setText("");
            year.setText("");
            spinnerMonth.setSelection(0);
            //date.setText("");
            //statusSpinner.setSelection(0);
        }

    }


