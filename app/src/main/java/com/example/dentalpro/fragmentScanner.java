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

public class fragmentScanner extends Fragment {

    Button btn_scan;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_scanner, container, false);
        btn_scan = view.findViewById(R.id.btn_scan);

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
        if(result.getContents()!=null)
        {
//            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Result");
//            builder.setMessage(result.getContents());
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//
//                }
//            }).show();
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

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = currentUser.getEmail();

                                updateIntoRecord(medication.getName(), updatedQuantity, userEmail);

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
                                .addOnSuccessListener(aVoid -> showAlert("Quantity updated successfully"))
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

    private void updateIntoRecord(String scannedBarcode, int usedQuantity, String userEmail) {

        // Use email as the unique identifier
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");

        // Query users by email
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    // Display user data in the EditText fields
                    if (user != null) {

                        Record record = new Record();
                        record.setUsername(user.getUsername()); // replace with the actual username
                        record.setName(scannedBarcode);
                        record.setQuantityR(String.valueOf(usedQuantity)); // assuming quantityR is a string

                        // Push the record to the "Records" node in the database
                        DatabaseReference recordsRef = FirebaseDatabase.getInstance().getReference().child("Record");
                        recordsRef.push().setValue(record);
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