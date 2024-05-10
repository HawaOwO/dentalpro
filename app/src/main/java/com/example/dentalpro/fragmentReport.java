package com.example.dentalpro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class fragmentReport extends Fragment {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private TextView textView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Record, ReportViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        textView = view.findViewById(R.id.textViewPopular);

        // Set onClickListeners for the date pickers
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Record");

        // Set up FirebaseUI adapter
        FirebaseRecyclerOptions<Record> options =
                new FirebaseRecyclerOptions.Builder<Record>()
                        .setQuery(databaseReference, Record.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Record, ReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReportViewHolder holder, int position, @NonNull Record model) {
                holder.bindReport(model);
            }

            @NonNull
            @Override
            public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_report, parent, false);
                return new ReportViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        return view;
    }

    // Method to show date picker dialog
    private void showDatePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    // Update the EditText with the selected date
                    calendar.set(year1, monthOfYear, dayOfMonth1);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    editText.setText(sdf.format(calendar.getTime()));

                    // Update the TextView with the most used medicine
                    updateMostUsedMedicine();
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method to update the TextView with the most used medicine
    private void updateMostUsedMedicine() {
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        // Query the database to get records within the selected date range
        Query query = databaseReference.orderByChild("date").startAt(startDate).endAt(endDate);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Initialize a map to store medicine quantities
                    Map<String, Integer> medicineQuantities = new HashMap<>();

                    // Loop through each record
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Record record = dataSnapshot.getValue(Record.class);
                        String medicineName = record.getName();

                        // Increment the quantity for the corresponding medicine
                        medicineQuantities.put(medicineName, medicineQuantities.getOrDefault(medicineName, 0) + Integer.parseInt(record.getQuantityR()));
                    }

                    // Find the medicine with the highest quantity
                    String mostUsedMedicine = "";
                    int maxQuantity = 0;
                    for (Map.Entry<String, Integer> entry : medicineQuantities.entrySet()) {
                        if (entry.getValue() > maxQuantity) {
                            mostUsedMedicine = entry.getKey();
                            maxQuantity = entry.getValue();
                        }
                    }

                    // Display the most used medicine in the TextView
                    if (!mostUsedMedicine.isEmpty()) {
                        textView.setText("Most Used Medicine: " + mostUsedMedicine);
                    } else {
                        textView.setText("No records found between selected dates");
                    }
                } else {
                    textView.setText("No records found between selected dates");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("Firebase", "Error fetching data", error.toException());
            }
        });
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

    // ViewHolder class for each item
    private static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDate, textQuantity;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textViewColumn1);
            textDate = itemView.findViewById(R.id.textViewColumn2);
            textQuantity = itemView.findViewById(R.id.textViewColumn3);
        }

        public void bindReport(Record report) {
            textName.setText("Name: " + report.getName());
            textDate.setText("Date: " + report.getDate());
            textQuantity.setText("Quantity: " + report.getQuantityR());
        }
    }
}
