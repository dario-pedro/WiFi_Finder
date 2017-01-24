package com.vrem.wifianalyzer.steps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;


import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;

import java.text.DecimalFormat;
import java.util.Calendar;


public class StepCounterActivity extends Activity {

    private Double distance = 0.0;
    private Double velocity = 0.0;

    private int step_length = 0;
    private int weight = 0;
    private int total_step = 0;

    private Thread thread;

    private long timer = 0;
    private  long startTimer = 0;
    private  long tempTime = 0;

    private TextView step_counter;
    private TextView tv_distance;
    private TextView tv_velocity;
    private TextView tv_show_step;

    private boolean mKeepRunning = false;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            countDistance();

            if (timer != 0 && distance != 0.0) {


                //calories = weight * distance * 0.001;
                velocity = distance * 1000 / timer;
            } else {
                //calories = 0.0;
                velocity = 0.0;
            }

            countStep();

            tv_show_step.setText(total_step + "");

            tv_distance.setText(formatDouble(distance));
            tv_velocity.setText(formatDouble(velocity));

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
                                timer = tempTime + System.currentTimeMillis()
                                        - startTimer;
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
        tv_show_step = (TextView) this.findViewById(R.id.show_step);

        tv_distance = (TextView) this.findViewById(R.id.distance);
        tv_velocity = (TextView) this.findViewById(R.id.velocity);


        step_counter = (TextView)findViewById(R.id.step_counter);


        Intent service = new Intent(this, StepCounterService.class);
        startService(service);
        // /stopService(service);
        StepDetector.CURRENT_STEP = 0;
        startTimer = System.currentTimeMillis();
        tempTime = timer;
        tv_show_step.setText("0");
        tv_distance.setText(formatDouble(0.0));
        tv_velocity.setText(formatDouble(0.0));

        handler.removeCallbacks(thread);

    }



    private void init() {


        step_length = MainContext.INSTANCE.getSettings().getStepLength();
        weight = MainContext.INSTANCE.getSettings().getStepWeight();

        countDistance();
        countStep();

        velocity = ((timer += tempTime) != 0 && distance != 0.0) ? velocity = distance * 1000 / timer : 0.0;

        //calories = weight * distance * 0.001; : 0

        tv_distance.setText(formatDouble(distance));
        tv_velocity.setText(formatDouble(velocity));

        tv_show_step.setText(total_step + "");

    }





    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mKeepRunning = false;
        super.onDestroy();
    }

    private void countDistance() {
        if (StepDetector.CURRENT_STEP % 2 == 0) {
            distance = (StepAccel.CURRENT_STEP / 2) * 3 * step_length * 0.01;
        } else {
            distance = ((StepAccel.CURRENT_STEP / 2) * 3 + 1) * step_length * 0.01;
        }
    }


    private void countStep() {
        total_step = StepAccel.CURRENT_STEP;
    }

    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

}
