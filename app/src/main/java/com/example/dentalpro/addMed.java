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

public class addMed extends AppCompatActivity {
     EditText name, quantity, detail, expirydate;
     Button btnAdd, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        name = (EditText)findViewById(R.id.txtName);
        quantity = (EditText)findViewById(R.id.txtQuantity);
        detail = (EditText)findViewById(R.id.txtDetail);
        expirydate = (EditText)findViewById(R.id.txtExpirydate);

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnBack=(Button)findViewById(R.id.btnBack);

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

        FirebaseDatabase.getInstance().getReference().child("Tablet")
                .push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(name.getContext(), "Data Insert Succesfully", Toast.LENGTH_SHORT).show();
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
    }

}