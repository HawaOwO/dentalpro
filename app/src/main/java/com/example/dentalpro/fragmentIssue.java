package com.example.dentalpro;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class fragmentIssue extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Issue, IssueViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_issue, container, false);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Issue");

        // Set up FirebaseUI adapter
        FirebaseRecyclerOptions<Issue> options =
                new FirebaseRecyclerOptions.Builder<Issue>()
                        .setQuery(databaseReference, Issue.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Issue, IssueViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IssueViewHolder holder, int position, @NonNull Issue model) {
                // Bind Issue object to the ViewHolder
                holder.bindIssue(model, position, getRef(position).getKey());
            }

            @NonNull
            @Override
            public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new ViewHolder for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_issue, parent, false);
                return new IssueViewHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //startActivity(new Intent(getActivity(), addIssue.class));
                startActivityForResult(new Intent(getActivity(), addIssue.class), 1);


            }
        });

        return view;
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
                Toast.makeText(requireContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                // Refresh the data or perform any other necessary actions
                adapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancellation or any other scenario
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }




    // ViewHolder class for each item
    private static class IssueViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDescription, textDate, textCreator, textStatus;
        Button btnEdit, btnDelete;
        View statusIndicator;

        private String issueKey;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textname);
            textDescription = itemView.findViewById(R.id.textdesc);
            textDate = itemView.findViewById(R.id.textdate);
            textCreator = itemView.findViewById(R.id.textcreator);
            textStatus= itemView.findViewById(R.id.textstatus);

            // Initialize other TextViews and the statusIndicator here

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
//            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        public void bindIssue(Issue issue, int position, String key) {
            textName.setText(issue.getName());
            textDescription.setText(issue.getDescription());
            textDate.setText(issue.getDate());
            textCreator.setText(issue.getUsername());
            textStatus.setText(issue.getStatus());

            // Bind other attributes here
            issueKey = key;
            // Set the status indicator color based on the 'status' value
//            statusIndicator.setBackgroundResource((issue.getStatus() == 0) ? R.drawable.circle_red : R.drawable.circle_green);

            btnEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    final DialogPlus dialogPlus = DialogPlus.newDialog(itemView.getContext()).setContentHolder(new ViewHolder(R.layout.update_issue)).setExpanded(true, 1200).create();
                    //

                    // Access the inflated view and set data or handle interactions
                    View dialogView = dialogPlus.getHolderView();
                    // For example, if you have an EditText in your dialog layout:
                    EditText name = dialogView.findViewById(R.id.txtName);
                    EditText description = dialogView.findViewById(R.id.txtDesc);
                    EditText date = dialogView.findViewById(R.id.txtDate);
                    EditText creator = dialogView.findViewById(R.id.txtUsername);
                    EditText status = dialogView.findViewById(R.id.txtStatus);
//                    Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

                    Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

                    name.setText(issue.getName());
                    description.setText(String.valueOf(issue.getDescription()));
                    date.setText(issue.getDate());
                    creator.setText(issue.getUsername());
                    status.setText(issue.getStatus());


                    dialogPlus.show();
                    // Handle other views and actions as needed
                    btnUpdate.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", name.getText().toString());
                            map.put("description", description.getText().toString());
                            map.put("date", date.getText().toString());
                            map.put("creator", creator.getText().toString());
                            map.put("status", status.getText().toString());



                            FirebaseDatabase.getInstance().getReference().child("Issue")
                                    .child(issueKey).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(name.getContext(), "Issue Updated Succesfully", Toast.LENGTH_SHORT).show();
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
                            FirebaseDatabase.getInstance().getReference().child("Issue")
                                    .child(issueKey).removeValue();
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
}