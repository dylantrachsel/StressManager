package com.example.jodrew.heartratebandapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
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

import java.lang.ref.WeakReference;

/**
 * Created by chrisboodoo on 2016-11-05.
 */

public class SensorData extends Activity{

    private BandClient client = null;

    private int HR;
    private String HR_quality;
    private int Gsr;
    private double RR_interval;
    private double Skin_temp;

    private boolean Check_consent = false;

    final WeakReference<Activity> reference = new WeakReference<Activity>(this);


    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {

                HR = event.getHeartRate();
                HR_quality = event.getQuality().toString();
                // recorded at 1Hz

            }
        }
    };

    private BandSkinTemperatureEventListener mBandSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
            if (event != null){

                Skin_temp = event.getTemperature();

            }
        }
    };

    private BandRRIntervalEventListener mBandRRIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(BandRRIntervalEvent event) {
            if (event != null) {

                RR_interval = event.getInterval();
            }
        }
    };

    private BandGsrEventListener mBandGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent event) {

            Gsr = event.getResistance();
            // Provides the current skin resistance of the wearer in kohms
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        new HeartRateConsentTask().execute(reference);

        while(!Check_consent){

        }
        new HeartRateSubscriptionTask().execute();

    }

    //Kick off the heart rate reading
    public class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                        client.getSensorManager().registerRRIntervalEventListener(mBandRRIntervalEventListener);
                        client.getSensorManager().registerSkinTemperatureEventListener(mBandSkinTemperatureEventListener);
                        client.getSensorManager().registerGsrEventListener(mBandGsrEventListener);
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
                                Check_consent = true;
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

    private void appendToUI(String string){
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    public int getHR(){
        return HR;
    }

    public String getHR_qual(){
        return HR_quality;
    }

    public int getGsr(){
        return Gsr;
    }

    public double getRR(){
        return RR_interval;
    }

    public double getSkinTemp(){
        return Skin_temp;
    }

}
