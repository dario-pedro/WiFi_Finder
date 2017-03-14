package com.vrem.wifianalyzer.sensor_fusion;

import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.AccelerometerCompassProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.CalibratedGyroscopeProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.GravityCompassProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor1Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.OrientationProvider;
import com.vrem.wifianalyzer.sensor_fusion.orientationProvider.RotationVectorProvider;
import com.vrem.wifianalyzer.sensor_fusion.representation.EulerAngles;

import java.text.DecimalFormat;

/**
 * A fragment that contains the same visualisation for different orientation providers
 */
public class OrientationVisualisationFragment extends Fragment {
    /**
     * The surface that will be drawn upon
     */
    private GLSurfaceView mGLSurfaceView;
    /**
     * The class that renders the cube
     */
    private CubeRenderer mRenderer;
    /**
     * The current orientation provider that delivers device orientation.
     */
    private OrientationProvider currentOrientationProvider;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    /**
     *  For the UI Euler Angles updates
     */
    private Handler mHandler = new Handler();
    private boolean mKeepRunning = false;

    /**
     *   UI Euler Angles displays
     */
    private Button mButtonR,mButtonP,mButtonY;


    @Override
    public void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        currentOrientationProvider.start();
        mGLSurfaceView.onResume();
        mKeepRunning=true;
    }

    @Override
    public void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        currentOrientationProvider.stop();
        mGLSurfaceView.onPause();
        mKeepRunning=false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mKeepRunning = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mKeepRunning = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mKeepRunning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        mKeepRunning = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialise the orientationProvider
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
        case 1:
            currentOrientationProvider = new ImprovedOrientationSensor1Provider((SensorManager) getActivity()
                    .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
            break;
        case 2:
            currentOrientationProvider = new ImprovedOrientationSensor2Provider((SensorManager) getActivity()
                    .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
            break;
        case 3:
            currentOrientationProvider = new RotationVectorProvider((SensorManager) getActivity().getSystemService(
                    SensorSelectionActivity.SENSOR_SERVICE));
            break;
        case 4:
            currentOrientationProvider = new CalibratedGyroscopeProvider((SensorManager) getActivity()
                    .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
            break;
        case 5:
            currentOrientationProvider = new GravityCompassProvider((SensorManager) getActivity().getSystemService(
                    SensorSelectionActivity.SENSOR_SERVICE));
            break;
        case 6:
            currentOrientationProvider = new AccelerometerCompassProvider((SensorManager) getActivity()
                    .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
            break;
        default:
            break;
        }



        // Create our Preview view and set it as the content of our Activity
        mRenderer = new CubeRenderer();
        mRenderer.setOrientationProvider(currentOrientationProvider);
        mGLSurfaceView = new GLSurfaceView(getActivity());
        mGLSurfaceView.setRenderer(mRenderer);

        mGLSurfaceView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mRenderer.toggleShowCubeInsideOut();
                return true;
            }
        });


        mButtonR = (Button) getActivity().findViewById(R.id.infoButR);
        mButtonP = (Button) getActivity().findViewById(R.id.infoButP);
        mButtonY = (Button) getActivity().findViewById(R.id.infoButY);

        if(!mKeepRunning)
            ui_update.start();

        return mGLSurfaceView;
    }


    private Thread ui_update = new Thread() {
        public void run() {
            mKeepRunning = true;
            while (mKeepRunning) {
                try {
                    Thread.sleep(150);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }


                mHandler.post(new Runnable(){
                    public void run() {
                        EulerAngles ang = currentOrientationProvider.getEulerAngles();
                        mButtonR.setText("R: "+formatDouble(Math.toDegrees(ang.getRoll())));
                        mButtonP.setText("P: "+formatDouble(Math.toDegrees(ang.getPitch())));
                        mButtonY.setText("Y: "+formatDouble(Math.toDegrees(ang.getYaw())));
                    }
                });
            }
        }
    };

    /**
     * Format floats
     * @param value
     * @return ####.## formated float
     */
    private String formatDouble(double value) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(value);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }


}