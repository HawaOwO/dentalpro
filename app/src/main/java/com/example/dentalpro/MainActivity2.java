package com.example.dentalpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity2 extends AppCompatActivity {
    ImageView btn_gotohome;
    ImageView btn_gotoscan;
    ImageView btn_gotoissue;
    ImageView btn_gotoprofile;
    ImageView btn_logout;
    ImageView btn_noti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btn_gotohome= findViewById(R.id.homeicon);
        btn_gotoissue = findViewById(R.id.issueicon);
        btn_gotoprofile= findViewById(R.id.profileicon);
        btn_gotoscan = findViewById(R.id.scanicon);
        btn_logout = findViewById(R.id.backicon);
        btn_noti = findViewById(R.id.notificationicon);

//        btn_gotoscan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                Intent intent = new Intent(MainActivity2.this, scanner.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btn_gotohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
               replaceFragment(new fragmentHome());
            }
        });
        btn_gotoscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                replaceFragment(new fragmentScanner());
            }
        });
        btn_gotoissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                replaceFragment(new fragmentIssue());
            }
        });
        btn_gotoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                replaceFragment(new fragmentProfile());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction  fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment);
        fragmentTransaction.commit();
    }
}