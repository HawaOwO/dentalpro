package com.example.dentalpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class fragmentHome extends Fragment {
    Button t1, t2, t3, t4, t5, t6, t7, t8, t9, t10;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        t1 = view.findViewById(R.id.type1);
        t2 = view.findViewById(R.id.type2);
        t3 = view.findViewById(R.id.type3);
        t4 = view.findViewById(R.id.type4);
        t5 = view.findViewById(R.id.type5);
        t6 = view.findViewById(R.id.type6);
        t7 = view.findViewById(R.id.type7);
        t8 = view.findViewById(R.id.type8);
        t9 = view.findViewById(R.id.type9);
        t10 = view.findViewById(R.id.type10);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "TABLET"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "LIQUID (INTERNAL USE)"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "INHALATIONS"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "EYE DROP/ OINTMENT"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "SUPPOSITORY/ENEMA/PESSARY)"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "CREAM/LOTION"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "NOSE DROP/ SPRAY"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "INJECTIONS"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "LIQUID (EXTERNAL USE)"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        t10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = "CAPSULE"; // Set the selected type based on your logic
                MedicationFragment medicationFragment = new MedicationFragment();
                changeFragment(medicationFragment, selectedType);
            }

        });
        return view;

    }

    // Function to change the fragment
    private void changeFragment(Fragment newFragment, String selectedType) {
        // Get the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Begin a new FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Pass the selected type to the new fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("selectedType", selectedType);
        newFragment.setArguments(bundle);

        // Replace the current fragment with the new fragment
        fragmentTransaction.replace(R.id.frame1, newFragment);

        // Add the transaction to the back stack (optional, but useful for navigation)
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }


}


