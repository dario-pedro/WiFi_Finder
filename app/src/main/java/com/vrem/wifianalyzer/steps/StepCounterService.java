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

	public static Boolean isALL = true;

	private SensorManager mSensorManager;


	private StepAccel accell; // uses accelarometer
	private StepDetector detector; // uses type_step_detector
	private StepCounter counter; // uses type_step_counter

	private PowerManager mPowerManager;
	private WakeLock mWakeLock;




	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		FLAG = true;


		//generate sensors
		accell = new StepAccel(this);
		detector = new StepDetector(this);
		counter = new StepCounter(this);



		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		if(isALL)
		{

			mSensorManager.registerListener(accell,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);

			mSensorManager.registerListener(counter,
					mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
					SensorManager.SENSOR_DELAY_NORMAL);

		}


		mSensorManager.registerListener(detector,
				mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
				SensorManager.SENSOR_DELAY_FASTEST);






		// DONT CLOSE APP NOW
		mPowerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
		mWakeLock.acquire();
	}

	public void leaveOnlyCounter()
	{
		mSensorManager.unregisterListener(accell);
		mSensorManager.unregisterListener(detector);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		FLAG = false;
		if (accell != null) {
			mSensorManager.unregisterListener(accell);
		}
		if (detector != null) {
			mSensorManager.unregisterListener(detector);
		}
		if (counter != null) {
			mSensorManager.unregisterListener(counter);
		}

		if (mWakeLock != null) {
			mWakeLock.release();
		}
	}

}
