package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.wifi.model.WiFiData;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionPoint {

    private Coordinates position;
    private WiFiData info;

    private int level;

    public PositionPoint(Coordinates position, WiFiData info) {
        this.position = position;
        this.info = info;

        this.level = info.getWiFiDetails().get(0).getWiFiSignal().getLevel();
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public WiFiData getInfo() {
        return info;
    }

    public void setInfo(WiFiData info) {
        this.info = info;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
