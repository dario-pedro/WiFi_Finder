package com.vrem.wifianalyzer.odometry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.sensor_fusion.HardwareChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorSelectionActivity;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor1Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.OrientationProvider;


import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Dário on 25/01/2017.
 */

public class Odom implements SensorEventListener  {


    /**
     * Activity using the Odom
     */
    Activity activity;

    /**
     * The current orientation provider that delivers device orientation.
     */
    private OrientationProvider currentOrientationProvider;


    /**
     * Current Position
     */
    private Coordinates mCoords;


    /**
     * Thread Loop Control
     */
    private Boolean mKeepRunning = false;

    /**
     * Steps Counter
     */
    private int mSteps;

    /**
     * Step Lenght in cm
     */
    private double stepLength;


    private SensorManager mSensorManager;

    private float yaw = 0;




    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();

        synchronized (this) {

            if (type == Sensor.TYPE_STEP_DETECTOR) {
                mSteps++;
                yaw = currentOrientationProvider.getEulerAngles().getYaw();

                float x = (float) (stepLength*Math.cos(yaw));
                float y = (float) (stepLength*Math.sin(yaw));

                mCoords.increment( x , y );

            }
        }
    }







    public Odom() {
        super();
        init();
        mCoords = new Coordinates();
    }

    public Odom(float x,float y) {
        super();
        init();
        mCoords = new Coordinates(x,y);
    }



    public Coordinates getCoords()
    {
        return mCoords;
    }


    public void init(){
        //get a pointer to the current Activity
        activity = MainContext.INSTANCE.getMainActivity();

        /**
         * SENSOR FUNSION OBJECTS
         */

        // Check if device has a hardware gyroscope
        SensorChecker checker = new HardwareChecker((SensorManager) activity.getSystemService(SENSOR_SERVICE));
        if(!checker.IsGyroscopeAvailable()) {
            // If a gyroscope is unavailable, display a warning.
            displayHardwareMissingWarning();
        }

        currentOrientationProvider = new ImprovedOrientationSensor1Provider((SensorManager)
                activity.getSystemService(SensorSelectionActivity.SENSOR_SERVICE));

        currentOrientationProvider.start();

        /**
         * STEP COUNTER SERVICE
         */
        mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);

        stepLength =  MainContext.INSTANCE.getSettings().getStepLength();
        mSteps = 0;
        /*StepCounterService.isALL = false;

        StepDetector.CURRENT_STEP=0;
        Intent service = new Intent(activity, StepCounterService.class);
        activity.startService(service);*/


    }

    private void displayHardwareMissingWarning() {
        AlertDialog ad = new AlertDialog.Builder(activity).create();
        ad.setCancelable(false); // This blocks the 'BACK' button
        ad.setTitle(activity.getResources().getString(R.string.gyroscope_missing));
        ad.setMessage(activity.getResources().getString(R.string.gyroscope_missing_message));
        ad.setButton(DialogInterface.BUTTON_NEUTRAL, activity.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    public void unregisterListener(){
        mSensorManager.unregisterListener(this);
    }











    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
