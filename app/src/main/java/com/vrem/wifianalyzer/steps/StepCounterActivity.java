package com.vrem.wifianalyzer.steps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;

import java.text.DecimalFormat;

public class StepCounterActivity extends Activity {

    private Double distance_accel = 0.0;
    private Double velocity_accel = 0.0;
    private int total_step_accel = 0;
    private TextView step_counter_accel;
    private TextView tv_distance_accel;
    private TextView tv_velocity_accel;
    private TextView tv_show_step_accel;

    private Double distance_detector = 0.0;
    private Double velocity_detector = 0.0;
    private int total_step_detector = 0;
    private TextView step_counter_detector;
    private TextView tv_distance_detector;
    private TextView tv_velocity_detector;
    private TextView tv_show_step_detector;

    private Double distance_counter = 0.0;
    private Double velocity_counter = 0.0;
    private int total_step_counter = 0;
    private TextView step_counter_counter;
    private TextView tv_distance_counter;
    private TextView tv_velocity_counter;
    private TextView tv_show_step_counter;

    private int step_length = 0;
    private int weight = 0;

    private Thread thread;

    private long timer = 0;
    private  long startTimer = 0;
    private  long tempTime = 0;

    private boolean mKeepRunning = false;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            countDistance();

            if (timer != 0 && distance_accel != 0.0) {

                velocity_accel = distance_accel * 1000 / timer;
            } else {
                velocity_accel = 0.0;
            }

            countStep();

            tv_show_step_accel.setText(total_step_accel + "");

            tv_distance_accel.setText(formatDouble(distance_accel));
            tv_velocity_accel.setText(formatDouble(velocity_accel));

        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.step_counter_activity);

        mKeepRunning = true;

        if (thread == null) {

            thread = new Thread() {// ���߳����ڼ�����ǰ�����ı仯

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    int temp = 0;
                    while (mKeepRunning) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepAccel.CURRENT_STEP) {
                                temp = StepAccel.CURRENT_STEP;
                            }



                            if (startTimer != System.currentTimeMillis()) {
                                timer = tempTime + System.currentTimeMillis()- startTimer;
                            }



                            handler.sendMessage(msg);
                        }
                    }
                }
            };
            thread.start();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addView();
        init();

    }



    private void addView() {
        tv_show_step_accel = (TextView) this.findViewById(R.id.show_step);

        tv_distance_accel = (TextView) this.findViewById(R.id.distance);
        tv_velocity_accel = (TextView) this.findViewById(R.id.velocity);


        step_counter_accel = (TextView)findViewById(R.id.step_counter);


        Intent service = new Intent(this, StepCounterService.class);
        startService(service);
        // /stopService(service);
        StepDetector.CURRENT_STEP = 0;
        startTimer = System.currentTimeMillis();
        tempTime = timer;
        tv_show_step_accel.setText("0");
        tv_distance_accel.setText(formatDouble(0.0));
        tv_velocity_accel.setText(formatDouble(0.0));

        handler.removeCallbacks(thread);

    }



    private void init() {


        step_length = MainContext.INSTANCE.getSettings().getStepLength();
        weight = MainContext.INSTANCE.getSettings().getStepWeight();

        distance_accel = 0.0;
        total_step_accel = 0;

        velocity_accel = ((timer += tempTime) != 0 && distance_accel != 0.0) ? velocity_accel = distance_accel * 1000 / timer : 0.0;

        //calories = weight * distance_accel * 0.001; : 0

        tv_distance_accel.setText(formatDouble(distance_accel));
        tv_velocity_accel.setText(formatDouble(velocity_accel));

        tv_show_step_accel.setText(total_step_accel + "");

    }





    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mKeepRunning = false;
        super.onDestroy();
    }

    private void countDistance(){
        countDistance(R.string.sensor_accelarometer);
        countDistance(R.string.sensor_step_detector);
        countDistance(R.string.sensor_step_counter);
    }

    private void countStep() {
        countStep(R.string.sensor_accelarometer);
        countStep(R.string.sensor_step_detector);
        countStep(R.string.sensor_step_counter);
    }

    private void countDistance(int sensor) {

        switch (sensor)
        {
            case R.string.sensor_accelarometer:
                if (StepDetector.CURRENT_STEP % 2 == 0) {
                    distance_accel = (StepAccel.CURRENT_STEP / 2) * 3 * step_length * 0.01;
                } else {
                    distance_accel = ((StepAccel.CURRENT_STEP / 2) * 3 + 1) * step_length * 0.01;
                }
                break;
            case R.string.sensor_step_detector:
                if (StepDetector.CURRENT_STEP % 2 == 0) {
                    distance_accel = (StepAccel.CURRENT_STEP / 2) * 3 * step_length * 0.01;
                } else {
                    distance_accel = ((StepAccel.CURRENT_STEP / 2) * 3 + 1) * step_length * 0.01;
                }
                break;
            case R.string.sensor_step_counter:
                if (StepDetector.CURRENT_STEP % 2 == 0) {
                    distance_accel = (StepAccel.CURRENT_STEP / 2) * 3 * step_length * 0.01;
                } else {
                    distance_accel = ((StepAccel.CURRENT_STEP / 2) * 3 + 1) * step_length * 0.01;
                }
                break;
        }
    }


    private void countStep(int sensor) {

        switch (sensor)
        {
            case R.string.sensor_accelarometer:
                total_step_accel = StepAccel.CURRENT_STEP;
                break;
            case R.string.sensor_step_detector:
                total_step_detector = StepAccel.CURRENT_STEP;
                break;
            case R.string.sensor_step_counter:
                total_step_counter = StepAccel.CURRENT_STEP;
                break;
        }
    }

    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

}
