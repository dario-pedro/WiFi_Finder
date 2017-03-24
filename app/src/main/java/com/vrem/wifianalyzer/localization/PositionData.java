package com.vrem.wifianalyzer.localization;

import android.util.Log;
import android.util.Pair;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.odometry.Coordinates;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import static com.vrem.wifianalyzer.localization.TrilaterationSolver.solve;

/**
 * Created by Dário on 26/01/2017.
 */

public class PositionData {


    public static final char TRILATERATION = 'c';
    public static final char MULTILATERATION = 't';



    /**
     * Number of Points used for estimation
     */
    private static final int  NUMBER_OF_STORED_MAX = 3;

    /**
     * Minimal distance in cm between 2 points used
     * for estimation
     */
    private static final float  MIN_DIST = 50.0f;

    /**
     * By default use MULTILATERATION
     */
    private char mMode = MULTILATERATION;

    private boolean positionEstimated;

    private Coordinates estimatedTargetPosition;
    private Deque<PositionPoint> points;

    private PositionPoint[] highestValues;



    /**
     * MIN VALUE IS 3
     */
    private int MAX_POINTS_STORAGE = 25;


    public PositionData() {
        init();
    }

    public PositionData(char mode) {
        init();
        this.mMode = mode;
    }


    private void init()
    {
        this.estimatedTargetPosition = new Coordinates();
        this.positionEstimated = false;
        this.points = new ArrayDeque<>();
        this.highestValues = new PositionPoint[NUMBER_OF_STORED_MAX];

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
            MainContext.INSTANCE.addEstimative(p);
        }

        if(points.size() > 2) {
            positionEstimated = true;
        }


        estimate(p);

    }

    private void estimate(PositionPoint p)
    {
        switch (mMode)
        {
            case TRILATERATION:
                estimateTrilateration(p);
                break;
            case MULTILATERATION:
                estimateMultilateration();
                break;
            default:
                estimateMultilateration();
                break;
        }



    }

    private Pair<double[][],double[]> getDoublePoints(){



        int number_points = points.size();

        double[][] positions = new double[number_points][2];
        double[] distances = new double[number_points];

        Iterator it = points.iterator();

        for (int i = 0; i< number_points ; i++)
        {
            PositionPoint p = (PositionPoint) it.next();
            positions[i] =  p.getPosition().getDoubles();
            distances[i] = p.getDistance();
        }




        return new Pair<>(positions,distances);
    }

    private double[] estimateMultilateration()
    {
        Pair<double[][],double[]> points = getDoublePoints();
        double[][] positions = points.first;
        double[] distances = points.second;


        double[] centroid = new double[0];
        try {
            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new MultilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            centroid = optimum.getPoint().toArray();

            estimatedTargetPosition.setX((float) centroid[0]);
            estimatedTargetPosition.setY((float) centroid[1]);

            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
            RealVector standardDeviation = optimum.getSigma(0);
            RealMatrix covarianceMatrix = optimum.getCovariances(0);
        } catch (Exception e) {
            Log.d("Multilateration",""+e);
        }

        return centroid;
    }


    private void estimateTrilateration(PositionPoint p) {
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



    public boolean isPositionEstimated() {
        return positionEstimated;
    }

    public void setPositionEstimated(boolean positionEstimated) {
        this.positionEstimated = positionEstimated;
    }

    public void addPoint(PositionPoint p, short index){

        while (points.size() >= MAX_POINTS_STORAGE) {
            points.pollLast();
        }

        if (p != null) {
            points.addFirst(p);
            MainContext.INSTANCE.addPositionPoint(p,index);
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


    public void resetCoords() {
        init();
    }

}
