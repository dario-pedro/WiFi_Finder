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

        private float x;
        private float y;
    }
