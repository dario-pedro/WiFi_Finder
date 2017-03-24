
package com.vrem.wifianalyzer.multicast;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.localization.PositionData;
import com.vrem.wifianalyzer.localization.PositionPoint;
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

public class MulticastFragment extends Fragment  {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.findap_content, container, false);


        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //ui_update = null;
        super.onDestroy();
    }
}
