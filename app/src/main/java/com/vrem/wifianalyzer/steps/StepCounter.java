package com.vrem.wifianalyzer.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.vrem.wifianalyzer.odometry.OdomInterface;


public class StepCounter implements SensorEventListener {

	private OdomInterface stepEvent;

	public static int CURRENT_STEP = 0;

	public static boolean first_time = true;

	private static long end = 0;

	public static void reset_counter(){
		end = CURRENT_STEP;
		CURRENT_STEP =0;
	}

	public StepCounter() {
		// TODO Auto-generated constructor stub
		super();
		first_time = true;
	}

	public StepCounter(OdomInterface stepEvent) {
		super();
		first_time = true;
		this.stepEvent = stepEvent;
	}

	// public void onSensorChanged(int sensor, float[] values) {
	@Override
	public void onSensorChanged(SensorEvent event) {

		int type = event.sensor.getType();

		synchronized (this) {

			if (type == Sensor.TYPE_STEP_COUNTER) {
				if(first_time) {
					end = (int) event.values[0];
					first_time = false;
				}

				CURRENT_STEP = (int) (event.values[0] - end);
				if(stepEvent!=null)
					stepEvent.update(CURRENT_STEP);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
