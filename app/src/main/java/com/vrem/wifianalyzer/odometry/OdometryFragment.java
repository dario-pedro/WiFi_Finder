
package com.vrem.wifianalyzer.odometry;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import java.text.DecimalFormat;


public class OdometryFragment extends Fragment {


    private TextView tvX;
    private TextView tvY;

    private Odom mOdom;

    private Handler mHandler;

    private boolean mKeepRunningUI;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //FragmentActivity activity = getActivity();

        View view = inflater.inflate(R.layout.odometry_content, container, false);


        tvX = (TextView) view.findViewById(R.id.textViewX_value);
        tvY = (TextView) view.findViewById(R.id.textViewY_value);

        final Button button = (Button) view.findViewById(R.id.resetButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(MainContext.INSTANCE.getMainActivity().getCurrentFocus(), "Reset Success", Snackbar.LENGTH_LONG);

                snackbar.show();

                mOdom.resetCoords();
            }
        });

        // setting the reset text wasnt working on layout
        button.setText(MainContext.INSTANCE.getMainActivity().getString(R.string.reset));

        mOdom = new Odom();

        mHandler = new Handler();

        ui_update  = new Thread() {
            public void run() {
                mKeepRunningUI = true;
                while (mKeepRunningUI) {
                    try {
                        Thread.sleep(150);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.post(new Runnable(){
                        public void run() {
                            Coordinates c = mOdom.getCoords();

                            tvX.setText(""+formatDouble(c.getX()));
                            tvY.setText(""+formatDouble(c.getY()));
                        }
                    });
                }
            }
        };
        ui_update.start();



        return view;
    }

    private Thread ui_update ;


    /**
     * Converts cm to m
     * @param value
     * @return the value in m with the format "xxxx.xx m"
     */
    private String formatDouble(float value) {

        value/=100;

        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(value);

        String result = ""+value;

        if(mKeepRunningUI)
            result = distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                    : distanceStr;

        result+=" m";

        return result;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!mKeepRunningUI)
            ui_update.start();
    }

    @Override
    public void onDetach() {
        mKeepRunningUI = false;
        ui_update.interrupt();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mKeepRunningUI = false;
        ui_update.interrupt();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mKeepRunningUI = false;
        ui_update.interrupt();
        super.onDestroy();
    }
}
