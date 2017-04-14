
package com.vrem.wifianalyzer.localization;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.odometry.Odom;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.HotSpotManager;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;
import com.vrem.wifianalyzer.wifi.scanner.UpdateNotifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FindApFragment extends Fragment  implements UpdateNotifier {

    private SwipeRefreshLayout swipeRefreshLayout;

    private PositionData mPositionData;
    private Odom mOdom;

    private TextView tvX;
    private TextView tvY;
    private TextView tvEX;
    private TextView tvEY;
    private ImageView arrowView;
    private volatile int degree = 0;

    private List<WiFiDetail> wiFiDetails = new ArrayList<>();



    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.findap_content, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.odomRefresh);
        swipeRefreshLayout.setOnRefreshListener(new ListViewOnRefreshListener());

        mPositionData = new PositionData();

        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.register(this);

        arrowView = (ImageView) view.findViewById(R.id.arrowView);

        tvX = (TextView) view.findViewById(R.id.textViewX_value);
        tvY = (TextView) view.findViewById(R.id.textViewY_value);
        tvEX = (TextView) view.findViewById(R.id.textView_estX_value);
        tvEY = (TextView) view.findViewById(R.id.textView_estY_value);

        mOdom = new Odom();

        mHandler = new Handler();


        final Button button_reset = (Button) view.findViewById(R.id.resetButton);
        button_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(MainContext.INSTANCE.getMainActivity().getCurrentFocus(), "Reset Success", Snackbar.LENGTH_LONG);

                snackbar.show();

                findApReset();
            }
        });

        final Button button_swap = (Button) view.findViewById(R.id.swapButton);
        button_swap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String s = "Swaping to ";
                s+= HotSpotManager.hotSpotEnable ? "WiFi" : "HotSpot";


                Snackbar snackbar = Snackbar
                        .make(MainContext.INSTANCE.getMainActivity().getCurrentFocus(), s, Snackbar.LENGTH_LONG);

                snackbar.show();

                HotSpotManager.turnOnOffHotspot(MainContext.INSTANCE.getMainActivity(),!HotSpotManager.hotSpotEnable);
            }
        });


        return view;
    }






    public void update(WiFiData wiFiData){
        Settings settings = MainContext.INSTANCE.getSettings();
        wiFiDetails = wiFiData.getWiFiDetails(settings.getWiFiBand(), settings.getSortBy(), settings.getGroupBy());

        Coordinates curr_coords = new Coordinates(mOdom.getCoords());

        PositionPoint currPoint = new PositionPoint(curr_coords,wiFiDetails);

        if(mPositionData.isPositionEstimated())
            currPoint.setAPestimation(mPositionData.getTargetPosition());

        mPositionData.addPoint(currPoint);



        //TODO CHANGE THE ARROW MOVEMENT, ACCORDING TO ESTIMATIION
        int offset_deegree = (int) - Math.toDegrees(mOdom.getAngle());


        final Coordinates current_coords = mOdom.getCoords();




        if(mPositionData.isPositionEstimated())
            degree = (int) -(getAngle(current_coords, mPositionData.getTargetPosition()) + offset_deegree);

        arrowView.animate().rotation(degree).start();



        mHandler.post(new Runnable(){
            public void run() {
                if(mPositionData.isPositionEstimated()) {
                    tvEX.setText("" + formatDouble(mPositionData.getTargetPosition().getX()));
                    tvEY.setText("" + formatDouble(mPositionData.getTargetPosition().getY()));
                }

                tvX.setText(""+formatDouble(current_coords.getX()));
                tvY.setText(""+formatDouble(current_coords.getY()));
            }
        });

    }


    public double getAngle(Coordinates current,Coordinates target) {

        double angle = Math.toDegrees(Math.atan2(target.getY() - current.getY(),target.getX() - current.getX()));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }



    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.update();
        swipeRefreshLayout.setRefreshing(false);
    }


    private String formatDouble(float val) {
        val /= 100;
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(val);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

    private void findApReset()
    {
        mOdom.resetCoords();
        mPositionData.resetCoords();
        degree = 0;
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private class ListViewOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refresh();
        }
    }

    @Override
    public void onDetach() {
       // ui_update = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        //ui_update = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //ui_update = null;
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.unregister(this);
        super.onDestroy();
    }
}
