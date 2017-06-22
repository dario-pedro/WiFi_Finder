package com.vrem.wifianalyzer.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.vrem.wifianalyzer.odometry.OdomInterface;

public class StepDetector implements SensorEventListener {

	public static int CURRENT_STEP = 0;

	private OdomInterface stepEvent;

	public StepDetector() {

		super();

	}


	public StepDetector(OdomInterface stepEvent) {
		super();
		this.stepEvent = stepEvent;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		int type = event.sensor.getType();

		synchronized (this) {

			if (type == Sensor.TYPE_STEP_DETECTOR) {
				CURRENT_STEP++;
				if(stepEvent!=null)
					stepEvent.update(CURRENT_STEP);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
