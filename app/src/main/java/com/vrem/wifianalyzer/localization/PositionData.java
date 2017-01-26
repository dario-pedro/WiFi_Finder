package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.odometry.Odom;
import com.vrem.wifianalyzer.wifi.model.WiFiData;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionData {


    private boolean positionEstimated;

    private Coordinates estimatedTargetPosition;
    private Deque<PositionPoint> points;


    /**
     * MIN VALUE IS 3
     */
    private int MAX_POINTS_STORAGE = 10;


    public PositionData() {
        this.estimatedTargetPosition = new Coordinates();
        this.positionEstimated = false;
        this.points = new ArrayDeque<>();
    }

    public void addPoint(PositionPoint p){
        points.add(p);

        if(points.size() > 2)
            positionEstimated = true;

        while (points.size() >= MAX_POINTS_STORAGE) {
            points.pollLast();
        }

        if (p != null) {
            points.addFirst(p);
        }
    }

    public Coordinates getTargetPosition()
    {
        return this.estimatedTargetPosition;
    }


}
