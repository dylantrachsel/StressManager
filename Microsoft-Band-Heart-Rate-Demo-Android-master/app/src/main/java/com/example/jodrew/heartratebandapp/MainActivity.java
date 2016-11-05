package com.example.jodrew.heartratebandapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

//Band References
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    private BandClient client = null;
    private Button btnStart, btnConsent;
    private TextView txtStatus;
    private TextView txtRR;
    private TextView txtTemp;
    private TextView txtGsr;
    private TextView txtAcc;

    private boolean isLocked;
    private boolean accSpike;

    private float accVal;

    private String extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

    private File mFolder = new File(extr);
    private File mRRFolder = new File(mFolder.getAbsolutePath(), "R-R Files");

    private String TAG = "HRV";

    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.INTERNET"};

    int permsRequestCode = 200;


    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                appendToUI(String.format("Heart Rate = %d beats per minute\n"
                        + "Quality = %s\n", event.getHeartRate(), event.getQuality()));
                // recorded at 1Hz



                if (event.getQuality().toString() == "LOCKED"){
                    isLocked = true;
                }
                else{
                    isLocked = false;
                }
            }
        }
    };

    private BandSkinTemperatureEventListener mBandSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
            if (event != null){
                appendTempttoUi(String.format("Skin Temp = %f", event.getTemperature()));
                //Current temperature in degrees Celsius of the person wearing the Band


            }
        }
    };

    private BandRRIntervalEventListener mBandRRIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(BandRRIntervalEvent bandRRIntervalEvent) {
            if (bandRRIntervalEvent != null) {
                appendRRtoUI(String.format("RR Interval = %f", bandRRIntervalEvent.getInterval()));
                //Current RR interval in seconds as read by the Band
                //Provides the interval in seconds between the last two continuous heart beats.

                File RRFile = lastFolderModified(mRRFolder.getAbsolutePath());


                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();

                //String RRvalueLS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "LOCKED,Spike\n");
                //String RRvalueANS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "AQUIRING,No Spike\n");
                String RRvalueLNS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "LOCKED,No Spike\n");
                String RRvalueAS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "AQUIRING,Spike\n");

                String RRvalueLS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "LOCKED," + accVal + ",\n");
                String RRvalueANS = String.valueOf(ts + "," + bandRRIntervalEvent.getInterval() + "," + "AQUIRING," + accVal + ",\n");

                if (isLocked & accSpike) {
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueLS);
                        Log.d(TAG, RRvalueLS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueANS);
                        Log.d(TAG, RRvalueANS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                /*if (isLocked & accSpike) {
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueLS);
                        Log.d(TAG, RRvalueLS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!isLocked & accSpike){
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueAS);
                        Log.d(TAG, RRvalueAS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isLocked & !accSpike){
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueLNS);
                        Log.d(TAG, RRvalueLNS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        FileWriter writer = new FileWriter(RRFile, true);
                        writer.write(RRvalueANS);
                        Log.d(TAG, RRvalueANS);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                }
            }
    };

    private BandGsrEventListener mBandGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent bandGsrEvent) {
            appendGsrtoUi(String.format("Galvanic Skin Reponse: %d", bandGsrEvent.getResistance()));
            // Provides the current skin resistance of the wearer in kohms
        }
    };

    private BandAccelerometerEventListener mBandAccListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent bandAccelerometerEvent) {
            appendAcctoUi(String.format("Accelerometer X: %f g units \nAccelerometer Y: %f g units \n Accelerometer Z: %f g units",
                    bandAccelerometerEvent.getAccelerationX(), bandAccelerometerEvent.getAccelerationY(), bandAccelerometerEvent.getAccelerationZ()));

             accVal = bandAccelerometerEvent.getAccelerationX();
            /*accSpike = false;

            if (Math.abs(bandAccelerometerEvent.getAccelerationX()) > 2 || Math.abs(bandAccelerometerEvent.getAccelerationY()) > 2 || Math.abs(bandAccelerometerEvent.getAccelerationZ()) > 2){
                accSpike = true;
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set heart rate
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        //set RR int
        txtRR = (TextView) findViewById(R.id.txtRR);

        //set Temp

        txtTemp = (TextView) findViewById(R.id.txtTemp);

        //set Gsr

        txtGsr = (TextView) findViewById(R.id.txtGsr);

        //set Accelerometer

        txtAcc = (TextView) findViewById(R.id.txtAcc);

        //set consent
        btnConsent = (Button) findViewById(R.id.btnConsent);

        //set start heart rate
        btnStart = (Button) findViewById(R.id.btnStart);



        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        btnConsent.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                new HeartRateConsentTask().execute(reference);
            }
        });

        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setText("");
                txtRR.setText("");
                txtAcc.setText("");
                new HeartRateSubscriptionTask().execute();


                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat fmt = new SimpleDateFormat("yyy-MM-dd-k-mm-ss");
                String today = fmt.format(date);


                if (!mRRFolder.exists()){
                    mRRFolder.mkdirs();
                }

                File rawData = new File(mRRFolder + "/" + today + ".csv");

                try {
                    rawData.createNewFile();
                    Log.d(TAG, "file created");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileWriter writer = new FileWriter(rawData, true);
                    writer.write("Timestamp,R-R Interval,HR Status,Accelerometer Spike\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

            requestPermissions(perms, permsRequestCode);


    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

                break;

        }

    }

    //Kick off the heart rate reading
    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                        client.getSensorManager().registerRRIntervalEventListener(mBandRRIntervalEventListener);
                        client.getSensorManager().registerSkinTemperatureEventListener(mBandSkinTemperatureEventListener);
                        client.getSensorManager().registerGsrEventListener(mBandGsrEventListener);
                        client.getSensorManager().registerAccelerometerEventListener(mBandAccListener, SampleRate.MS16);
                    } else {
                        appendToUI("You have not given this application consent to access heart rate data yet."
                                + " Please press the Heart Rate Consent button.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }



    //Need to get user consent
    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }


    //Get connection to band
    private boolean getConnectedBandClient() throws InterruptedException, BandException {

        if (client == null) {
            //Find paired bands
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                //No bands found...message to user
                return false;
            }
            //need to set client if there are devices
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if(ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        //need to return connected status
        return ConnectionState.CONNECTED == client.connect().await();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtStatus.setText("");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }

    private void appendRRtoUI(final String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtRR.setText(string);

            }
        });
    }

    private void appendTempttoUi(final String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTemp.setText(string);
            }
        });
    }

    private void appendGsrtoUi(final String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtGsr.setText(string);
            }
        });
    }
    private void appendAcctoUi(final String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtAcc.setText(string);
            }
        });
    }

    public static File lastFolderModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }


}
