package com.example.dentalpro;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;


public class MedicationFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Medication, MedicationViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tablet, container, false);
        // Retrieve the selected type from the arguments
        String selectedType = getArguments().getString("selectedType", "TABLET"); // Default to "T1" if not provided
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Medication");

        // Set up FirebaseUI adapter
        FirebaseRecyclerOptions<Medication> options =
                new FirebaseRecyclerOptions.Builder<Medication>()
                        .setQuery(databaseReference.orderByChild("type").equalTo(selectedType), Medication.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Medication, MedicationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MedicationViewHolder holder, int position, @NonNull Medication model) {
                // Bind Tablet object to the ViewHolder
                holder.bindMedication(model, position, getRef(position).getKey());
            }

            @NonNull
            @Override
            public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new ViewHolder for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tablet, parent, false);
                return new MedicationViewHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               //startActivity(new Intent(getActivity(), addMed.class));
                startActivityForResult(new Intent(getActivity(), addMed.class), 1);

            }
        });

        return view;

    }

//

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Assuming 1 is the requestCode you used when starting addMed
            if (resultCode == RESULT_OK) {
                // Handle success
                Toast.makeText(requireContext(), "Data Inserted Successfully2", Toast.LENGTH_SHORT).show();
                // Refresh the data or perform any other necessary actions
                adapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancellation or any other scenario
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }




    // ViewHolder class for each item
    private static class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textQuantity, textDetails, textExpirydate, textType;
        Button btnEdit, btnDelete;


        private String tabletKey;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textquantity);
            textDetails = itemView.findViewById(R.id.textdetails);
            textExpirydate = itemView.findViewById(R.id.textexpiryDate);
            // Initialize other TextViews here for other attributes
            btnEdit=itemView.findViewById(R.id.btnEdit);
            btnDelete=itemView.findViewById(R.id.btnDelete);

            textType = itemView.findViewById(R.id.textType);

        }

        public void bindMedication(Medication medication, int position, String key) {
            textName.setText(medication.getName());
            textQuantity.setText(String.valueOf(medication.getQuantity()));
            textDetails.setText(medication.getDetails());
            textExpirydate.setText(medication.getExpiryDate());
            textType.setText(medication.getType());
            // Bind other attributes here
             // Declare a final variable
            tabletKey=key;

            btnEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    final DialogPlus dialogPlus = DialogPlus.newDialog(itemView.getContext()).setContentHolder(new ViewHolder(R.layout.update_popup)).setExpanded(true, 1200).create();
                    //

                    // Access the inflated view and set data or handle interactions
                    View dialogView = dialogPlus.getHolderView();
                    // For example, if you have an EditText in your dialog layout:
                    EditText name = dialogView.findViewById(R.id.txtName);
                    EditText quantity = dialogView.findViewById(R.id.txtQuantity);
                    EditText details = dialogView.findViewById(R.id.txtDetail);
                    EditText expirydate = dialogView.findViewById(R.id.txtExpirydate);
                    Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

                    Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

                    name.setText(medication.getName());
                    quantity.setText(String.valueOf(medication.getQuantity()));
                    details.setText(medication.getDetails());
                    expirydate.setText(medication.getExpiryDate());// Set data from the tablet object

                    // Set the selection for the Spinner based on the tablet's type
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(itemView.getContext(), R.array.type_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerType.setAdapter(adapter);
                    if (medication.getType() != null) {
                        int spinnerPosition = adapter.getPosition(medication.getType());
                        spinnerType.setSelection(spinnerPosition);
                    }

                    dialogPlus.show();
                    // Handle other views and actions as needed
                    btnUpdate.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            int updatedQuantity= Integer.parseInt(quantity.getText().toString());

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", name.getText().toString());
                            map.put("quantity", updatedQuantity);
                            map.put("details", details.getText().toString());
                            map.put("expiryDate", expirydate.getText().toString());
                            map.put("type", spinnerType.getSelectedItem().toString()); // Set the type


                            FirebaseDatabase.getInstance().getReference().child("Medication")
                                    .child(tabletKey).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(name.getContext(), "Data Updated Succesfully", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(name.getContext(), "Error while updating", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();

                                        }
                                    });


                        }

                    });


                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlertDialog.Builder builder = new AlertDialog.Builder(textName.getContext());
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Deleted data cant be undo");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int which){
                        FirebaseDatabase.getInstance().getReference().child("Medication")
                                .child(tabletKey).removeValue();
                    }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            Toast.makeText(textName.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            });
        }
    }

//    // Add this method to update quantity based on barcode
//    public void updateQuantity(String name) {
//        DatabaseReference medicationRef = FirebaseDatabase.getInstance().getReference().child("Medication");
//
//        medicationRef.orderByChild("name").equalTo(name.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Medication medication = dataSnapshot.getValue(Medication.class);
//                    if (medication != null) {
//                        int currentQuantity = medication.getQuantity();
//                        if (currentQuantity > 0) {
//                            // Deduct one from the quantity
//                            medicationRef.child(dataSnapshot.getKey()).child("quantity").setValue(currentQuantity - 1);
//                            Toast.makeText(getActivity(), "Quantity Updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Medication out of stock", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Error updating quantity", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}

