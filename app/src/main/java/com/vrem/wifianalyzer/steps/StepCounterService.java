package com.vrem.wifianalyzer.steps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;


public class StepCounterService extends Service {

	public static Boolean FLAG = false;

	private SensorManager mSensorManager;
	private StepAccel accell;//

	private PowerManager mPowerManager;//
	private WakeLock mWakeLock;//

/*
	Sensor mSensor_counter ;
	Sensor mSensor_detect;

	mSensor_counter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
	mSensor_detect = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

	mSensorManager.registerListener(mSensorEventListenerCounter,
	mSensor_counter, SensorManager.SENSOR_DELAY_NORMAL);


	private SensorEventListener mSensorEventListenerCounter = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float value = event.values[0];
		}
	};

*/






	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		FLAG = true;//

		//
		accell = new StepAccel(this);


		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

		mSensorManager.registerListener(accell,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		mPowerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
		mWakeLock.acquire();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FLAG = false;
		if (accell != null) {
			mSensorManager.unregisterListener(accell);
		}

		if (mWakeLock != null) {
			mWakeLock.release();
		}
	}

}
