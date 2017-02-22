
package com.vrem.wifianalyzer.localization;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.odometry.Odom;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;
import com.vrem.wifianalyzer.wifi.scanner.UpdateNotifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FindApFragment extends Fragment  implements UpdateNotifier {

    private SwipeRefreshLayout swipeRefreshLayout;
    private PositionData positionData;

    private TextView tvX;
    private TextView tvY;
    private TextView tvEX;
    private TextView tvEY;
    private ImageView arrowView;

    private List<WiFiDetail> wiFiDetails = new ArrayList<>();

    private Odom mOdom;

    private Handler mHandler;

    private boolean mKeepRunningUI;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();

        View view = inflater.inflate(R.layout.findap_content, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.odomRefresh);
        swipeRefreshLayout.setOnRefreshListener(new ListViewOnRefreshListener());

        positionData = new PositionData();

        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.register(this);

        arrowView = (ImageView) view.findViewById(R.id.arrowView);

        tvX = (TextView) view.findViewById(R.id.textViewX_value);
        tvY = (TextView) view.findViewById(R.id.textViewY_value);
        tvEX = (TextView) view.findViewById(R.id.textView_estX_value);
        tvEY = (TextView) view.findViewById(R.id.textView_estY_value);

        mOdom = new Odom();

        mHandler = new Handler();

/*
        Coordinates a = new Coordinates(0.5f,0.5f);
        Coordinates b = new Coordinates(0.5f,-0.2f);
        Coordinates c = new Coordinates(-0.3f,-0.2f);
        Coordinates d = new Coordinates(-0.3f,0.5f);

        double test_angle = getAngle(a,b);
        test_angle=0;
        test_angle = getAngle(a,b);
        test_angle=0;
        test_angle = getAngle(a,c);
        test_angle=0;
        test_angle = getAngle(a,d);
        test_angle=0;
        test_angle = getAngle(c,a);
        test_angle=0;
        test_angle = getAngle(c,b);
        test_angle=0;
        test_angle = getAngle(a,d);
        test_angle=0;
        test_angle = getAngle(b,d);
        test_angle=0;

        test_angle = getAngle(d,b);
        test_angle=0;*/

        //refresh();

        return view;
    }




    private int degree = 0;

    public void update(WiFiData wiFiData){
        Settings settings = MainContext.INSTANCE.getSettings();
        wiFiDetails = wiFiData.getWiFiDetails(settings.getWiFiBand(), settings.getSortBy(), settings.getGroupBy());

        Coordinates curr_coords = new Coordinates(mOdom.getCoords());

        positionData.addPoint(new PositionPoint(curr_coords,wiFiDetails));

        //TODO CHANGE THE ARROW MOVEMENT, ACCORDING TO ESTIMATIION
        int offset_deegree = (int) - Math.toDegrees(mOdom.getAngle());
        arrowView.animate().rotation(offset_deegree).start();

        final Coordinates current_coords = mOdom.getCoords();




        if(positionData.positionEstimated)
            degree = (int) -(getAngle(current_coords,positionData.getTargetPosition()) + offset_deegree);

        //arrowView.animate().rotation(degree).start();

        mHandler.post(new Runnable(){
            public void run() {
                if(positionData.positionEstimated) {
                    tvEX.setText("" + formatDouble(positionData.getTargetPosition().getX()));
                    tvEY.setText("" + formatDouble(positionData.getTargetPosition().getY()));
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
        super.onDestroy();
    }
}
