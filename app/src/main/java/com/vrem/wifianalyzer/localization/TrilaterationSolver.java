package com.vrem.wifianalyzer.localization;

import com.vrem.wifianalyzer.odometry.Coordinates;

/**
 * Created by Dário on 28/01/2017.
 */

public class TrilaterationSolver {


    /**
     * REQUIRE THAT THE COORDINATES ARE IN CM!
     * @param position1
     * @param position2
     * @param position3
     * @param d1
     * @param d2
     * @param d3
     * @return estimated target Coordinates
     */
    public static Coordinates solve(Coordinates position1, Coordinates position2, Coordinates position3, double d1, double d2, double d3) {

        double xa = position1.getX();
        double ya = position1.getY();
        double xb = position2.getX();
        double yb = position2.getY();
        double xc = position3.getX();
        double yc = position3.getY();
        double ra = d1;
        double rb = d2;
        double rc = d3;

        double S = (Math.pow(xc, 2.) - Math.pow(xb, 2.) + Math.pow(yc, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(rc, 2.)) / 2.0;
        double T = (Math.pow(xa, 2.) - Math.pow(xb, 2.) + Math.pow(ya, 2.) - Math.pow(yb, 2.) + Math.pow(rb, 2.) - Math.pow(ra, 2.)) / 2.0;
        double y = ((T * (xb - xc)) - (S * (xb - xa))) / (((ya - yb) * (xb - xc)) - ((yc - yb) * (xb - xa)));
        double x = ((y * (ya - yb)) - T) / (xb - xa);

        return new Coordinates((float)x,(float)y);
    }

}
