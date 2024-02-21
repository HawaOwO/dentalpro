package com.example.dentalpro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;


public class TabletFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Tablet, TabletViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tablet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tablet");

        // Set up FirebaseUI adapter
        FirebaseRecyclerOptions<Tablet> options =
                new FirebaseRecyclerOptions.Builder<Tablet>()
                        .setQuery(databaseReference, Tablet.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Tablet, TabletViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TabletViewHolder holder, int position, @NonNull Tablet model) {
                // Bind Tablet object to the ViewHolder
                holder.bindTablet(model, position, getRef(position).getKey());
            }

            @NonNull
            @Override
            public TabletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new ViewHolder for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tablet, parent, false);
                return new TabletViewHolder(view);
            }


        };

        recyclerView.setAdapter(adapter);

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



    // ViewHolder class for each item
    private static class TabletViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textQuantity, textDetails, textExpirydate;
        Button btnEdit, btnDelete;

        private String tabletKey;

        public TabletViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textquantity);
            textDetails = itemView.findViewById(R.id.textdetails);
            textExpirydate = itemView.findViewById(R.id.textexpiryDate);
            // Initialize other TextViews here for other attributes
            btnEdit=itemView.findViewById(R.id.btnEdit);
            btnDelete=itemView.findViewById(R.id.btnDelete);
        }

        public void bindTablet(Tablet tablet, int position, String key) {
            textName.setText(tablet.getName());
            textQuantity.setText(String.valueOf(tablet.getQuantity()));
            textDetails.setText(tablet.getDetails());
            textExpirydate.setText(tablet.getExpiryDate());
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

                    Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

                    name.setText(tablet.getName());
                    quantity.setText(String.valueOf(tablet.getQuantity()));
                    details.setText(tablet.getDetails());
                    expirydate.setText(tablet.getExpiryDate());// Set data from the tablet object

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


                            FirebaseDatabase.getInstance().getReference().child("Tablet")
                                    .child(tabletKey).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(name.getContext(), "Data Updated Suddecfully", Toast.LENGTH_SHORT).show();
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
                        FirebaseDatabase.getInstance().getReference().child("Tablet")
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

}

