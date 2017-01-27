package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionData {

    private static final int  NUMBER_OF_STORED_MAX = 3;

    public boolean positionEstimated;

    private Coordinates estimatedTargetPosition;
    private Deque<PositionPoint> points;

    private PositionPoint[] highestValues;



    /**
     * MIN VALUE IS 3
     */
    private int MAX_POINTS_STORAGE = 25;


    public PositionData() {
        this.estimatedTargetPosition = new Coordinates();
        this.positionEstimated = false;
        this.points = new ArrayDeque<>();

        highestValues = new PositionPoint[NUMBER_OF_STORED_MAX];

        for (int i = NUMBER_OF_STORED_MAX-1 ; i >= 0  ; i--)
        {
            highestValues[i] = new PositionPoint(new Coordinates(),null);
        }

    }

    public void addPoint(PositionPoint p){

        while (points.size() >= MAX_POINTS_STORAGE) {
            points.pollLast();
        }

        if (p != null) {
            points.addFirst(p);
        }

        if(points.size() > 2) {
            positionEstimated = true;
        }


        for(int i = highestValues.length -1 ; i >= 0 ; i--)
        {
            /**
             * CHECK IF THE NEW VALUE IS BIGGER THEN ANY IN THE STORE MAX VALUES
             */
            if(highestValues[i].getLevel() < p.getLevel())
            {
                PositionPoint min_value = highestValues[i];
                int min_value_index = i;

                /**
                 * GET THE MINIMUM VALUE IN THE MAX VALUES STORED
                 */
                int j = i;
                for(; j >= 0 ; j--)
                {
                    if(highestValues[j].getLevel() < min_value.getLevel()) {
                        min_value = highestValues[j];
                        min_value_index = j;
                    }
                }
                /**
                 * REPLACE THE VALUE
                 */
                highestValues[min_value_index] = p;
                break;
            }
        }


    }

    public Coordinates getTargetPosition()
    {

        return estimatedTargetPosition;
    }





    private void calculateEstimative()
    {
        if(!positionEstimated)
            return;


    }





}
