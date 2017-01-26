package com.vrem.wifianalyzer.odometry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Message;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.sensor_fusion.HardwareChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorSelectionActivity;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor1Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.OrientationProvider;
import com.vrem.wifianalyzer.sensor_fusion.representation.EulerAngles;
import com.vrem.wifianalyzer.steps.StepAccel;
import com.vrem.wifianalyzer.steps.StepCounter;
import com.vrem.wifianalyzer.steps.StepCounterService;
import com.vrem.wifianalyzer.steps.StepDetector;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Dário on 25/01/2017.
 */

public class Odom extends Thread {


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

    public Odom() {
        init();
        mCoords = new Coordinates();
    }

    public Odom(float x,float y) {
        init();
        mCoords = new Coordinates(x,y);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        mKeepRunning = true;
        while (mKeepRunning) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**
             * They are different when the user walks 1 step
             */
            if(mSteps != StepDetector.CURRENT_STEP)
            {
                mSteps = StepDetector.CURRENT_STEP;
                float yaw = currentOrientationProvider.getEulerAngles().getYaw();
                mCoords.setX( (float) (stepLength*Math.cos(yaw)) );
                mCoords.setY( (float) (stepLength*Math.sin(yaw)) );
            }
        }
    }

    public Coordinates getmCoords()
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

        stepLength =  MainContext.INSTANCE.getSettings().getStepLength();

        StepCounterService.isALL = false;
        mSteps = 0;
        Intent service = new Intent(activity, StepCounterService.class);

        activity.startService(service);
        StepCounter.reset_counter();

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


    /**
     * Coordenates struct class
     */
    public class Coordinates{

        public Coordinates() {
            this.x = 0;
            this.y = 0;
        }

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        private float x;
        private float y;
    }

}
