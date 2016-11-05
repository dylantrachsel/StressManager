package com.example.jodrew.heartratebandapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylantrachsel on 11/5/16.
 */

public class MainMenu extends Activity {
    private Button statusButton;
    private Button sButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.main_menu);

        statusButton = (Button) findViewById(R.id.statusButton);

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatsPage.class);
                startActivity(intent);
            }
        });

        sButton = (Button) findViewById(R.id.sButton);

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InterventionPage.class);
                startActivity(intent);
            }
        });
    }
}
