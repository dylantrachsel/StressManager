package com.example.jodrew.heartratebandapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dylantrachsel on 11/5/16.
 */

public class InterventionPage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.intervention_page);
    }
}
