package com.example.dentalpro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class fragmentScanner extends Fragment {

    Button btn_scan;
    View view;
    String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_scanner, container, false);
        btn_scan = view.findViewById(R.id.btn_scan);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail(); // Use email as the unique identifier
        }

        btn_scan.setOnClickListener(v -> {
            scanCode();
        });
        return view;

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);


    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null) {
            String scannedBarcode = result.getContents();
            retrieveMedicationData(scannedBarcode);
        }
    });

    private void retrieveMedicationData(String scannedBarcode) {
        DatabaseReference medicationsRef = FirebaseDatabase.getInstance().getReference().child("Medication");

        // Query medications by name (assuming name is the unique identifier)
        medicationsRef.orderByChild("name").equalTo(scannedBarcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Medication found
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Medication medication = snapshot.getValue(Medication.class);
                        if (medication != null) {
                            // Display AlertDialog for Quantity Editing
                            showQuantityEditDialog(medication);

                            return;
                        }
                    }
                } else {
                    // Medication not found for scanned barcode
                    showAlert("Medication not found for scanned barcode");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                showAlert("Error retrieving medication data");
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Note");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void showQuantityEditDialog(Medication medication) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_quantity, null);
        EditText quantityEditText = dialogView.findViewById(R.id.editQuantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Edit Quantity")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newQuantityStr = quantityEditText.getText().toString();

                        if (!newQuantityStr.isEmpty()) {
                            int newQuantity = Integer.parseInt(newQuantityStr);
                            int originalQuantity = medication.getQuantity();
                            int updatedQuantity = originalQuantity - newQuantity;

                            if (updatedQuantity >= 0) {
                                medication.setQuantity(updatedQuantity);
                                updateQuantityInDatabase(medication.getName(), updatedQuantity);

//                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                                String userEmail = currentUser.getEmail();

                                updateIntoRecord(medication.getName(), newQuantity);

                                dialogInterface.dismiss();
                            } else {
                                showAlert("Quantity entered is greater than available quantity");
                            }
                        } else {
                            showAlert("Please enter a valid quantity");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.show();
    }

    private void updateQuantityInDatabase(String scannedBarcode, int updatedQuantity) {
        DatabaseReference medicationsRef = FirebaseDatabase.getInstance().getReference().child("Medication");

        medicationsRef.orderByChild("name").equalTo(scannedBarcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String medicationKey = snapshot.getKey();
                        medicationsRef.child(medicationKey).child("quantity").setValue(updatedQuantity)
                                .addOnFailureListener(e -> showAlert("Error updating quantity"));
                        return;
                    }
                } else {
                    showAlert("Medication not found for scanned barcode");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showAlert("Error retrieving medication data");
            }
        });
    }

    private void updateIntoRecord(String scannedBarcode, int usedQuantity) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid(); // Use UID as the unique identifier

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

            // Query users by username
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                        if (user != null) {
                            String username = user.getUsername();
                            Record record = new Record();
                            // replace with the actual username
                            record.setName(scannedBarcode);
                            record.setQuantityR(String.valueOf(usedQuantity)); // assuming quantityR is a string
                            // Capture the current date
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String currentDate = dateFormat.format(new Date());
                            record.setDate(currentDate);

                            // Push the record to the "Records" node in the database
                            DatabaseReference recordsRef = FirebaseDatabase.getInstance().getReference().child("Record");
                            recordsRef.push().setValue(record)
                                    .addOnSuccessListener(aVoid -> showAlert("Record updated successfully"))
                                    .addOnFailureListener(e -> showAlert("Error updating record"));

                        }
                        else {
                            showAlert("Username not found for the current user");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                        Record record = new Record();
//                        //record.setUsername(userEmail); // replace with the actual username
//                        record.setName(scannedBarcode);
//                        record.setQuantityR(String.valueOf(usedQuantity)); // assuming quantityR is a string
//                        // Capture the current date
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                        String currentDate = dateFormat.format(new Date());
//                        record.setDate(currentDate);
//
//                        // Push the record to the "Records" node in the database
//                        DatabaseReference recordsRef = FirebaseDatabase.getInstance().getReference().child("Record");
//                        recordsRef.push().setValue(record)
//                                .addOnSuccessListener(aVoid -> showAlert("Record updated successfully"))
//                                .addOnFailureListener(e -> showAlert("Error updating record"));
//
//    }

    }
}