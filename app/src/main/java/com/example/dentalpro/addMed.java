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

public class addMed extends AppCompatActivity {
     EditText name, quantity, detail, expirydate, type, medPicture;
     Button btnAdd, btnBack;
     Spinner spinnerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        name = (EditText)findViewById(R.id.txtName);
        quantity = (EditText)findViewById(R.id.txtQuantity);
        detail = (EditText)findViewById(R.id.txtDetail);
        expirydate = (EditText)findViewById(R.id.txtExpirydate);
        medPicture=(EditText)findViewById(R.id.txtPic);

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnBack=(Button)findViewById(R.id.btnBack);

        // Get a reference to the Spinner in your activity or fragment
        spinnerType = findViewById(R.id.spinnerType);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerType.setAdapter(adapter);


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
        int updatedQuantity= Integer.parseInt(quantity.getText().toString());

        Map<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("quantity", updatedQuantity);
        map.put("details", detail.getText().toString());
        map.put("expiryDate", expirydate.getText().toString());
        map.put("type", spinnerType.getSelectedItem().toString()); // Add the selected type
        map.put("medPicture", medPicture.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Medication")
                .push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(name.getContext(), "Data Insert Succesfully1", Toast.LENGTH_SHORT).show();
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
        quantity.setText("");
        detail.setText("");
        expirydate.setText("");
        spinnerType.setSelection(0);
        medPicture.setText("");
    }

}