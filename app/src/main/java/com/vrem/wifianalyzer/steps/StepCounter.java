package com.vrem.wifianalyzer.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class StepCounter implements SensorEventListener {

	public static int CURRENT_SETP = 0;

	public static float SENSITIVITY = 3;

	private static long end = 0;
	private static long start = 0;



	public StepCounter(Context context) {
		// TODO Auto-generated constructor stub
		super();

	}



	// public void onSensorChanged(int sensor, float[] values) {
	@Override
	public void onSensorChanged(SensorEvent event) {

		int type = event.sensor.getType();

		synchronized (this) {

			if (type == Sensor.TYPE_STEP_COUNTER) {
				Log.i("SD","TODO handle TYPE_STEP_COUNTER ");
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
