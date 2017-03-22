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
import com.vrem.wifianalyzer.localization.PositionPoint;
import com.vrem.wifianalyzer.sensor_fusion.HardwareChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorChecker;
import com.vrem.wifianalyzer.sensor_fusion.SensorSelectionActivity;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.AccelerometerCompassProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.CalibratedGyroscopeProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.GravityCompassProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor1Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.OrientationProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.RotationVectorProvider;
import com.vrem.wifianalyzer.steps.StepAccel;


import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Dário on 25/01/2017.
 */

public class Odom implements OdomInterface  {

    public static final short NOT_STEP = -1;

    public static final short ACCELEROMETERCOMPASSPROVIDER = 0;
    public static final short CALIBRATEDGYROSCOPEPROVIDER = 1;
    public static final short GRAVITYCOMPASSPROVIDER = 2;

    public static final short IMPROVEDORIENTATIONSENSOR1PROVIDER = 3;
    public static final short IMPROVEDORIENTATIONSENSOR2PROVIDER = 4;
    public static final short ROTATIONVECTORPROVIDER = 5;

    public static final short ACCELEROMETER = 6;
    public static final short STEPCOUNTER = 7;
    public static final short STEPDETECTOR = 8;

    public static final short DEFAULT = 5; // ORIENTATION DEFAULT
    public static final short STEPDEFAULT = 6;




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

    /**
     * SensorManager is require to implement callbacks with the sensors
     */
    private SensorManager mSensorManager;

    /**
     * Orientation and Step Types
     * -1 means not set
     */
    private short mOrientationType = -1;
    private short mStepsType = -1;

    /**
     * Current Phone Yaw Orientation
     */
    public float yaw = 0;


    public Odom(short TYPE_ORIENTATION, short TYPE_STEPS) {

        super();

        this.mStepsType = TYPE_STEPS;
        this.mOrientationType = TYPE_ORIENTATION;

        init();
        mCoords = new Coordinates();

    }


    public Odom() {
        super();

        this.mStepsType = STEPDEFAULT;
        this.mOrientationType = DEFAULT;

        init();
        mCoords = new Coordinates();

    }

    public Odom(float x,float y) {
        super();

        this.mStepsType = STEPDEFAULT;
        this.mOrientationType = DEFAULT;

        init();
        mCoords = new Coordinates(x,y);
    }



    public Coordinates getCoords()
    {
        return mCoords;
    }

    public void resetCoords()
    {
        mCoords = new Coordinates();
    }

    public void saveCoords()
    {
        double angle = Math.toDegrees(yaw);
        Coordinates storeCoords = new Coordinates(mCoords);
        PositionPoint storePP = new PositionPoint(storeCoords,angle);
        MainContext.INSTANCE.addPositionPoint(storePP,mOrientationType);
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

        switch (mOrientationType) {
            case IMPROVEDORIENTATIONSENSOR1PROVIDER:
                currentOrientationProvider = new ImprovedOrientationSensor1Provider((SensorManager) activity
                        .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
                break;
            case IMPROVEDORIENTATIONSENSOR2PROVIDER:
                currentOrientationProvider = new ImprovedOrientationSensor2Provider((SensorManager) activity
                        .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
                break;
            case ROTATIONVECTORPROVIDER:
                currentOrientationProvider = new RotationVectorProvider((SensorManager) activity.getSystemService(
                        SensorSelectionActivity.SENSOR_SERVICE));
                break;
            case CALIBRATEDGYROSCOPEPROVIDER:
                currentOrientationProvider = new CalibratedGyroscopeProvider((SensorManager) activity
                        .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
                break;
            case GRAVITYCOMPASSPROVIDER:
                currentOrientationProvider = new GravityCompassProvider((SensorManager) activity.getSystemService(
                        SensorSelectionActivity.SENSOR_SERVICE));
                break;
            case ACCELEROMETERCOMPASSPROVIDER:
                currentOrientationProvider = new AccelerometerCompassProvider((SensorManager) activity
                        .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
                break;
            default:
                break;
        }


        currentOrientationProvider.start();

        /**
         * STEP COUNTER SERVICE
         */

        stepLength =  MainContext.INSTANCE.getSettings().getStepLength();
        mSteps = 0;

        StepAccel sa = new StepAccel(this);

        mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(sa,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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



    public float getAngle()
    {
        return currentOrientationProvider.getEulerAngles().getYaw();
    }




    @Override
    public void update(int steps) {
        saveCoords();
        mSteps++;
        yaw = currentOrientationProvider.getEulerAngles().getYaw();

        float x = (float) (stepLength*Math.cos(yaw));
        float y = (float) (stepLength*Math.sin(yaw));

        mCoords.increment( x , y );

    }

}
