package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;

import java.util.Date;
import java.util.List;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionPoint implements Cloneable {



    private Coordinates position;
    private List<WiFiDetail> info;

    private static final double m_to_cm = 100.0;

    private double distance;

    Date store_time;

    public PositionPoint(Coordinates position, List<WiFiDetail> info) {
        this.position = position;
        this.info = info;

        this.store_time = new Date();

        this.distance = (info != null && info.size() > -1) ?
            info.get(0).getWiFiSignal().getDistance()*m_to_cm : Double.MAX_VALUE;


    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public List<WiFiDetail> getInfo() {
        return info;
    }

    public void setInfo(List<WiFiDetail> info) {
        this.info = info;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
