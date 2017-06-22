package com.vrem.wifianalyzer.maps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.vrem.wifianalyzer.MainContext;

/**
 * Created by Dário on 20/06/2017.
 */

public class LatLonProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {


    private final GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LatLng mCurrLL;

    private final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY; // LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    private final int LOCATION_REQUEST_INTERVAL;
    private final int LOCATION_REQUEST_FASTINTERVAL;

    public LatLng getmCurrLL() {
        return mCurrLL;
    }

    public void setmCurrLL(LatLng mCurrLL) {
        this.mCurrLL = mCurrLL;
    }

    public LatLonProvider() {
        LOCATION_REQUEST_INTERVAL = 1000;
        LOCATION_REQUEST_FASTINTERVAL = 500;

        mGoogleApiClient = new GoogleApiClient.Builder(MainContext.INSTANCE.getMainActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }


    public LatLonProvider(int interval) {
        LOCATION_REQUEST_INTERVAL = interval;
        LOCATION_REQUEST_FASTINTERVAL = 500;

        mGoogleApiClient = new GoogleApiClient.Builder(MainContext.INSTANCE.getMainActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    public LatLonProvider(int interval , int fast_interval) {
        LOCATION_REQUEST_INTERVAL = interval;
        LOCATION_REQUEST_FASTINTERVAL = fast_interval;

        mGoogleApiClient = new GoogleApiClient.Builder(MainContext.INSTANCE.getMainActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLastLocation != null)
        {
            mCurrLL = new LatLng(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LOCATION_REQUEST_PRIORITY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL); // Update location every second
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTINTERVAL);
        //mLocationRequest.setPriority();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrLL = new LatLng(location.getLatitude(),location.getLongitude());
    }



    public void disconnect(){
        mGoogleApiClient.disconnect();


    }
}