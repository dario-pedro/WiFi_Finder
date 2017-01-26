
package com.vrem.wifianalyzer.odometry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;

import java.util.ArrayList;
import java.util.List;

public class OdometryFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private OdometryUpdates odometryUpdates;

    private TextView tvX;
    private TextView tvY;

    private List<WiFiDetail> wiFiDetails = new ArrayList<>();

    private Odom mOdom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();

        View view = inflater.inflate(R.layout.odometry_content, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.odomRefresh);
       // swipeRefreshLayout.setOnRefreshListener(new AccessPointsFragment.ListViewOnRefreshListener());

        odometryUpdates = new OdometryUpdates();

        Scanner scanner = MainContext.INSTANCE.getScanner();
        //scanner.register(odometryUpdates);

        tvX = (TextView) view.findViewById(R.id.textViewX_value);
        tvY = (TextView) view.findViewById(R.id.textViewY_value);


        if(mOdom==null) {
            mOdom = new Odom();
            mOdom.start();
        }


        refresh();

        update(scanner.getWiFiData());


        return view;
    }

    private void update(WiFiData wiFiData){
        Settings settings = MainContext.INSTANCE.getSettings();
        wiFiDetails = wiFiData.getWiFiDetails(settings.getWiFiBand(), settings.getSortBy(), settings.getGroupBy());

    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.update();
        Odom.Coordinates coords = mOdom.getmCoords();
        coords.setX(coords.getY());
        swipeRefreshLayout.setRefreshing(false);
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

}
