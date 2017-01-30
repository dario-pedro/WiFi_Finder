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
import android.util.Log;


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
    public OrientationProvider currentOrientationProvider;


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

    public float yaw = 0;


    public static float SENSITIVITY = 0;

    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;


    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();

        synchronized (this) {

            if (type == Sensor.TYPE_ACCELEROMETER) {
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1: (v < mLastValues[k] ? -1 : 0));
                if (direction == -mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or
                    // maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k]- mLastExtremes[1 - extType][k]);

                    if (diff > SENSITIVITY) {
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            end = System.currentTimeMillis();
                            if (end - start > 500) {

                                mSteps++;
                                yaw = currentOrientationProvider.getEulerAngles().getYaw();

                                float x = (float) (stepLength*Math.cos(yaw));
                                float y = (float) (stepLength*Math.sin(yaw));

                                mCoords.increment( x , y );

                                mLastMatch = extType;
                                start = end;
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;



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

        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        SENSITIVITY = 3;

        mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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




    public float getAngle()
    {
        return currentOrientationProvider.getEulerAngles().getYaw();
    }






    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
