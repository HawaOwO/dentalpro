package com.example.dentalpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReminderDialog extends DialogFragment {

    private FirebaseRecyclerAdapter<Medication, MedicationViewHolder> adapter;
    private DatabaseReference databaseReference;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        // Set the title and content of the notification
        builder.setTitle("Running low on Medications: ");

        // Set up the RecyclerView to display the list of expiring medications
        RecyclerView recyclerView = new RecyclerView(requireActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        builder.setView(recyclerView);

        // Set up Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Medication");

        // Set up FirebaseUI adapter
        FirebaseRecyclerOptions<Medication> options =
                new FirebaseRecyclerOptions.Builder<Medication>()
                        .setQuery(databaseReference.orderByChild("quantity").endAt(20), Medication.class)
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
                        .inflate(R.layout.item_medication, parent, false);
                return new MedicationViewHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);


//        // Set a positive button to close the dialog
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//
//        // Set the background color of the AlertDialog
//        int dialogBackgroundColor = getResources().getColor(R.color.lavender);
//        ((ViewGroup) recyclerView.getParent()).setBackgroundColor(dialogBackgroundColor);
        return builder.create();
    }

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

    public class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textQuantity;


        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);

        }

        public void bindMedication(Medication medication, int position, String key) {
            if (textName != null) {
                textName.setText("Medicine Name: " + medication.getName());
            }

            if (textQuantity != null) {
                textQuantity.setText("Quantity: " + medication.getQuantity());
            }

        }
    }

}
