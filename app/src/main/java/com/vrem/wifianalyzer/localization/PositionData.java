package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.odometry.Coordinates;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.vrem.wifianalyzer.localization.TrilaterationSolver.solve;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionData {


    /**
     * Number of Points used for estimation
     */
    private static final int  NUMBER_OF_STORED_MAX = 3;

    /**
     * Minimal distance in cm between 2 points used
     * for estimation
     */
    private static final float  MIN_DIST = 50.0f;

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
            MainContext.INSTANCE.addPositionPoint(p);
        }

        if(points.size() > 2) {
            positionEstimated = true;
        }

        boolean require_recalculate_estimation = false;


        if(replace_coordinates(p))
        {
            require_recalculate_estimation = positionEstimated;
        }
        else{
            require_recalculate_estimation = addNewPoint(positionEstimated,p);
        }



        if(require_recalculate_estimation)
        {
           estimatedTargetPosition = solve(highestValues[0].getPosition(),
                                            highestValues[1].getPosition(),
                                            highestValues[2].getPosition(),
                                            highestValues[0].getDistance(),
                                            highestValues[1].getDistance(),
                                            highestValues[2].getDistance());

            MainContext.INSTANCE.addEstimative(highestValues[0]);
            MainContext.INSTANCE.addEstimative(highestValues[1]);
            MainContext.INSTANCE.addEstimative(highestValues[2]);
            MainContext.INSTANCE.addEstimative(new PositionPoint(estimatedTargetPosition,null));
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


    private boolean  addNewPoint(boolean positionEstimated, PositionPoint p){


        if(!isInMinDistance(p))
            return false;

        boolean require_recalculate_estimation = false;

        for(int i = highestValues.length -1 ; i >= 0 ; i--)
        {
            /**
             * CHECK IF THE NEW VALUE IS BIGGER THEN ANY IN THE STORE MAX VALUES
             */
            if(highestValues[i].getDistance() > p.getDistance())
            {
                PositionPoint min_value = highestValues[i];
                int min_value_index = i;

                /**
                 * GET THE MINIMUM VALUE IN THE MAX VALUES STORED
                 */
                int j = i;
                for(; j >= 0 ; j--)
                {
                    if(highestValues[j].getDistance() > min_value.getDistance()) {
                        min_value = highestValues[j];
                        min_value_index = j;
                    }
                }
                /**
                 * REPLACE THE VALUE
                 */
                highestValues[min_value_index] = (PositionPoint) p.clone();
                require_recalculate_estimation = positionEstimated;
                break;
            }
        }

        return require_recalculate_estimation;
    }

    private boolean isInMinDistance(PositionPoint p)
    {
        for(int i = highestValues.length -1 ; i >= 0 ; i--)
        {
            /**
             * Distance between the point in highest value i and the point p
             */
            float dbp = p.getPosition().distance_betweent(highestValues[i].getPosition());

            if(dbp<MIN_DIST)
                return false;
        }
        return true;
    }

    private boolean containes_coordinates(PositionPoint[] points, PositionPoint p)
    {
        if(points==null || points.length==0)
            return false;

        for(int i = points.length -1; i > 0 ;i-- )
        {
            if(Coordinates.equals(points[i].getPosition(),p.getPosition()))
                return true;
        }

        return false;

    }


    /**
     *
     * Replaces the point with the same coordinates if the given point is closer
     * @param p - PositionPoint to be tested
     * @return true if sucessfly replaced
     */
    private boolean replace_coordinates(PositionPoint p)
    {
        if(points==null || highestValues.length==0)
            return false;

        for(int i = highestValues.length -1; i > 0 ;i-- )
        {
            if(Coordinates.equals(highestValues[i].getPosition(),p.getPosition()))
            {
                if(highestValues[i].getDistance()>= p.getDistance()) {
                    highestValues[i] = p; // replace by the point with shorter distance
                    return true;
                }
                return false;

            }

        }

        return false;


    }




}
