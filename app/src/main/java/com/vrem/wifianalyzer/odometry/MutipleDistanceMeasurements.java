package com.vrem.wifianalyzer.odometry;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dário on 01/07/2017.
 */

public class MutipleDistanceMeasurements {

    public Coordinates mOdometryCoords;
    public LatLng mAndroidLocationCoords;

    public MutipleDistanceMeasurements(Coordinates mOdometryCoords, LatLng mAndroidLocationCoords) {
        this.mOdometryCoords = mOdometryCoords;
        this.mAndroidLocationCoords = mAndroidLocationCoords;
    }

}
