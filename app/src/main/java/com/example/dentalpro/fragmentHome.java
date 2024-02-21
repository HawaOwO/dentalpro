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
//    FirebaseAuth fAuth;
//    FirebaseDatabase db;
//    DatabaseReference reference;

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
                changeFragment(new TabletFragment());
            }

        });
        return view;

    }

    // Function to change the fragment
    private void changeFragment(Fragment newFragment) {
        // Get the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Begin a new FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new fragment
        fragmentTransaction.replace(R.id.frame1, newFragment);

        // Add the transaction to the back stack (optional, but useful for navigation)
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }


}


