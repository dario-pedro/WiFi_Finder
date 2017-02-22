package com.vrem.wifianalyzer.odometry;

/**
 * Created by Dário on 26/01/2017.
 */

public class Coordinates {



        public Coordinates() {
            this.x = 0;
            this.y = 0;
        }

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Coordinates(Coordinates c) {
            this.x = c.getX();
            this.y = c.getY();
        }

        public void increment(float _x,float _y)
        {
            x+=_x;
            y+=_y;
        }

        public boolean equals(Coordinates test)
        {
            return test.getX()==this.x && test.getY()==this.y;
        }

        public static boolean equals(Coordinates c1,Coordinates c2)
        {
            return c1.getX()==c2.getX() && c1.getY()==c2.getX();
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }


        public static float distance_betweent(Coordinates a, Coordinates b)
        {
            float x_diff = a.getX() - b.getX();
            float y_diff = a.getY() - b.getY();

            return (float) Math.sqrt(x_diff*x_diff + y_diff*y_diff);
        }

        public float distance_betweent(Coordinates b)
        {
            float x_diff = x - b.getX();
            float y_diff = y - b.getY();

            return (float) Math.sqrt(x_diff*x_diff + y_diff*y_diff);
        }

        public static float distance_betweent_simp(Coordinates a, Coordinates b)
        {
            float x_diff = a.getX() - b.getX();
            float y_diff = a.getY() - b.getY();

            return (float) x_diff + y_diff;
        }

        public float distance_betweent_simp(Coordinates b)
        {
            float x_diff = x - b.getX();
            float y_diff = y - b.getY();

            return (float) x_diff + y_diff;
        }

        private float x;
        private float y;
    }
