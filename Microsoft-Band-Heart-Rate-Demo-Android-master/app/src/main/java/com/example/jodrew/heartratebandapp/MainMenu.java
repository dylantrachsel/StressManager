package com.example.jodrew.heartratebandapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

/**
 * Created by dylantrachsel on 11/5/16.
 */

public class MainMenu extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_menu,container, false);
    }

    public void statusCLick(View v) {
        Intent intent = new Intent(getActivity(), StatsPage.class);
        startActivity(intent);
    }

    public void sClick(View v) {
        Intent intent = new Intent(getActivity(), InterventionPage.class);
        startActivity(intent);
    }
}
