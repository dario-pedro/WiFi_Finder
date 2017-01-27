package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;

import java.util.List;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionPoint {

    private Coordinates position;
    private List<WiFiDetail> info;

    private int level;

    public PositionPoint(Coordinates position, List<WiFiDetail> info) {
        this.position = position;
        this.info = info;

        this.level = (info != null && info.size() > -1) ?
            info.get(0).getWiFiSignal().getLevel() : Integer.MIN_VALUE;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
