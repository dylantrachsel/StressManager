package com.example.jodrew.heartratebandapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private SensorData mSensorData;
    private Button mHistButton;
    private Button mStartButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSensorData = new SensorData();

        mSensorData.startHRCon(this);

        mHistButton = (Button) findViewById(R.id.histButton);
        mStartButton = (Button) findViewById(R.id.startButton);

        mHistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSpinner ms = (MultiSpinner)findViewById(R.id.multi_spinner);
                List<String> list = new ArrayList<String>();
                list.add("High Blood Pressure");
                list.add("Diabetic");
                ms.setItems(list);
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainMenuIntent = new Intent(getApplication(), MainMenu.class);
                MainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(MainMenuIntent);

            }
        });
    }
}
