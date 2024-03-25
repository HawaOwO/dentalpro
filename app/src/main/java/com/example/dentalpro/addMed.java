package com.example.dentalpro;

import android.graphics.Color;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addMed extends AppCompatActivity {
     EditText name, quantity, detail, expirydate, type, medPicture, day, month, year;
     Button btnAdd, btnBack, btnPickDate;
     Spinner spinnerType, spinnerMonth;
     private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        name = (EditText)findViewById(R.id.txtName);
        quantity = (EditText)findViewById(R.id.txtQuantity);
        detail = (EditText)findViewById(R.id.txtDetail);
        //expirydate = (EditText)findViewById(R.id.txtExpirydate);
        medPicture=(EditText)findViewById(R.id.txtPic);
        day =(EditText)findViewById(R.id.txtDay);
        year =(EditText)findViewById(R.id.txtYear);

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnBack=(Button)findViewById(R.id.btnBack);

        // Get a reference to the Spinner in your activity or fragment
        spinnerType = findViewById(R.id.spinnerType);
        spinnerMonth = findViewById(R.id.spinnerMonth);

        //btnPickDate = findViewById(R.id.btnPickDate);

//        btnPickDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //spinnerType.setAdapter(adapter);
        spinnerType.setAdapter(new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.type_array), new int[]{Color.BLACK}));


        // Set the selection for the Spinner based on the month
        ArrayAdapter<CharSequence> MonthAdapter = ArrayAdapter.createFromResource(addMed.this, R.array.month_array, android.R.layout.simple_spinner_item);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerMonth.setAdapter(MonthAdapter);
        spinnerMonth.setAdapter(new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.month_array), new int[]{Color.BLACK}));



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
//    // Method to show the DatePickerDialog
//    public void showDatePickerDialog() {
//        final Calendar currentDate = Calendar.getInstance();
//        int year = currentDate.get(Calendar.YEAR);
//        int month = currentDate.get(Calendar.MONTH);
//        int day = currentDate.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                selectedDate = Calendar.getInstance();
//                selectedDate.set(Calendar.YEAR, year);
//                selectedDate.set(Calendar.MONTH, monthOfYear);
//                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                // Format the selected date and set it to the EditText
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
//                expirydate.setText(sdf.format(selectedDate.getTime()));
//            }
//        }, year, month, day);
//
//        datePickerDialog.show();
//    }

    private void insertData()
    {
        int updatedQuantity= Integer.parseInt(quantity.getText().toString());

        Map<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("quantity", updatedQuantity);
        map.put("details", detail.getText().toString());
        //map.put("expiryDate", expirydate.getText().toString());
        map.put("type", spinnerType.getSelectedItem().toString()); // Add the selected type
        map.put("medPicture", medPicture.getText().toString());
        map.put("day", day.getText().toString());
        map.put("year", year.getText().toString());
        map.put("month", spinnerMonth.getSelectedItem().toString());

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
        //expirydate.setText("");
        spinnerType.setSelection(0);
        medPicture.setText("");
        day.setText("");
        year.setText("");
        spinnerMonth.setSelection(0);
    }

}