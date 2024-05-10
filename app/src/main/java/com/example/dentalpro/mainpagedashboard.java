package com.example.dentalpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class mainpagedashboard extends Fragment {

    View view;
    ImageView imageView;
    TextView textViewNumber;
    int[] imageResources = {R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5};
    int currentIndex = 0;

    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mainpagedashboard, container, false);

        Button homeButton = rootView.findViewById(R.id.home);
        Button scannerButton = rootView.findViewById(R.id.scanner);
        Button issueButton = rootView.findViewById(R.id.feedback);
        Button profileButton = rootView.findViewById(R.id.profile);
        Button downloadButton= rootView.findViewById(R.id.download);
        imageView = rootView.findViewById(R.id.imageViewCard);
        textViewNumber = rootView.findViewById(R.id.textViewNumber);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageViewClicked(v); // Call the method when the ImageView is clicked
            }
        });

//        ImageSlider imageSlider = rootView.findViewById(R.id.imageSlider);
//        ArrayList<SlideModel> slideModels = new ArrayList<>();
//
//        slideModels.add(new SlideModel(R.drawable.img2, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.img3, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.img4, ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.img5, ScaleTypes.FIT));
//
//        imageSlider.setImageList(slideModels, ScaleTypes.FIT);



        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the fragmentHome
                replaceFragment(new fragmentHome());
            }
        });

        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the fragmentScanner
                replaceFragment(new fragmentScanner());
            }
        });

        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the fragmentIssue
                replaceFragment(new fragmentIssue());
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the fragmentProfile
                replaceFragment(new fragmentProfile());
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View report
                replaceFragment(new fragmentReport());
            }
        });

        return rootView;
    }

    public void onImageViewClicked(View view) {
        // Increment the index and reset to 0 if it exceeds the number of images
        currentIndex = (currentIndex + 1) % imageResources.length;
        // Change the image resource based on the current index
        imageView.setImageResource(imageResources[currentIndex]);

        switch (currentIndex) {
            case 0: // First image
                updateUseCount();
                break;
            case 1: // Second image
                updateMedicationsNeedToOrder();
                break;
            case 2: // Third image
                updateTotalMedicationsInStock();
                break;
            case 3: // Fourth image
                updateTotalIssuesUnsolved();
                break;
        }
    }

    private void updateTotalIssuesUnsolved() {
        // Define a variable to store the total number of unsolved issues
        databaseRef.child("Issue")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalUnsolvedIssues = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Deserialize the Issue object
                            Issue issue = snapshot.getValue(Issue.class);
                            // Check if the status is UNSOLVED
                            if (issue.getStatus().equals("UNSOLVED")) {
                                totalUnsolvedIssues++; // Increment count if status is UNSOLVED
                            }
                        }
                        // Update the appropriate TextView with the totalUnsolvedIssues count
                        // For example, if you have a textViewTotalUnsolvedIssues:
                        textViewNumber.setText(String.valueOf(totalUnsolvedIssues));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void updateTotalMedicationsInStock() {

        databaseRef.child("Medication")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalMed = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Deserialize the Medication object
                            Medication medication = snapshot.getValue(Medication.class);
                            // Add the quantity of each medication to the total
                            totalMed += medication.getQuantity();
                        }
                        textViewNumber.setText(String.valueOf(totalMed));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

    }

    private void updateMedicationsNeedToOrder() {
        databaseRef.child("Medication")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int medOutOfStock = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Deserialize the Medication object
                            Medication medication = snapshot.getValue(Medication.class);
                            // Check if the quantity is less than 20
                            if (medication.getQuantity() < 20) {
                                medOutOfStock++; // Increment count if quantity is less than 20
                            }
                        }
                        // Update the appropriate TextView with the medOutOfStock count
                        // For example, if you have a textViewMedOutOfStock:
                        textViewNumber.setText(String.valueOf(medOutOfStock));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

    }

    private void updateUseCount() {
        // Get today's date in "YYYY-MM-DD" format
       // String today = LocalDate.now().toString();
        // Query database to count records for today
        databaseRef.child("Record")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int dailyUseCount = (int) dataSnapshot.getChildrenCount();
                        textViewNumber.setText(String.valueOf(dailyUseCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment);
        fragmentTransaction.commit();
    }


}