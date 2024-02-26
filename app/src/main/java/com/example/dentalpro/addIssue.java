package com.example.dentalpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addIssue extends AppCompatActivity {
    EditText name, username, description, date, status;
    Button btnAdd, btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        name = (EditText)findViewById(R.id.txtName);
        username = (EditText)findViewById(R.id.txtUsername);
        description = (EditText)findViewById(R.id.txtDesc);
        date = (EditText)findViewById(R.id.txtDate);
        status= (EditText)findViewById(R.id.txtStatus);

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnBack=(Button)findViewById(R.id.btnBack);


        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);

//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // Apply the adapter to the spinner
//        spinnerType.setAdapter(adapter);


        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                insertData();
                clearAll();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void insertData()
    {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("username", username.getText().toString());
        map.put("description", description.getText().toString());
        map.put("date", date.getText().toString());
        map.put("status", status.getText().toString()); // Add the selected type

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

    private void clearAll()
    {
        name.setText("");
        username.setText("");
        description.setText("");
        date.setText("");
        status.setText("");
    }

}
