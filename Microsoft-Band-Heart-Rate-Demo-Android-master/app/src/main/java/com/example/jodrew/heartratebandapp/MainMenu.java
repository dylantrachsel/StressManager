package com.example.jodrew.heartratebandapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

/**
 * Created by dylantrachsel on 11/5/16.
 */

public class MainMenu extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.main_menu);
    }

    public void statusCLick(View v) {
        Intent intent = new Intent(getApplicationContext(), StatsPage.class);
        startActivity(intent);
    }

    public void sClick(View v) {
        Intent intent = new Intent(getApplicationContext(), InterventionPage.class);
        startActivity(intent);
    }
}
