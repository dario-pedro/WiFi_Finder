package com.vrem.wifianalyzer.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.odometry.OdomInterface;
import com.vrem.wifianalyzer.odometry.OdometryFragment;

public class StepAccel implements SensorEventListener {

	private OdomInterface stepEvent;

	public static int CURRENT_STEP = 0;

	public static float SENSITIVITY = 0;

	private float mLastValues[] = new float[3 * 2];
	private float mScale[] = new float[2];
	private float mYOffset;
	private static long end = 0;
	private static long start = 0;


	private float mLastDirections[] = new float[3 * 2];
	private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
	private float mLastDiff[] = new float[3 * 2];
	private int mLastMatch = -1;


	public StepAccel() {
		// TODO Auto-generated constructor stub
		super();
		int h = 480;
		mYOffset = h * 0.5f;
		mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
		mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
		SENSITIVITY = 3;

	}


    public StepAccel(OdomInterface inter) {
        // TODO Auto-generated constructor stub
        super();
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        SENSITIVITY = 3;

       stepEvent = inter;

    }



	@Override
	public void onSensorChanged(SensorEvent event) {
		// Log.i(Constant.STEP_SERVER, "StepDetector");
		Sensor sensor = event.sensor;
		// Log.i(Constant.STEP_DETECTOR, "onSensorChanged");

		/*MainContext.INSTANCE.addDoubleTest((double) event.values[0]);
		MainContext.INSTANCE.addDoubleTest((double) event.values[1]);
		MainContext.INSTANCE.addDoubleTest((double) event.values[2]);*/

		synchronized (this) {
			 if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				float vSum = 0;
				for (int i = 0; i < 3; i++) {
					final float v = mYOffset + event.values[i] * mScale[1];
					vSum += v;
				}
				int k = 0;
				float v = vSum / 3;

				float direction = (v > mLastValues[k] ? 1: (v < mLastValues[k] ? -1 : 0));
				if (direction == -mLastDirections[k]) {
					// Direction changed
					int extType = (direction > 0 ? 0 : 1); // minumum or
					// maximum?
					mLastExtremes[extType][k] = mLastValues[k];
					float diff = Math.abs(mLastExtremes[extType][k]- mLastExtremes[1 - extType][k]);

					if (diff > SENSITIVITY) {
						boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
						boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
						boolean isNotContra = (mLastMatch != 1 - extType);

						if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
							end = System.currentTimeMillis();
							if (end - start > 500) {
								Log.i("StepAccel", "CURRENT_STEP:"
										+ CURRENT_STEP);
								CURRENT_STEP++;

								if(stepEvent!=null)
									stepEvent.update(CURRENT_STEP);

								mLastMatch = extType;
								start = end;
							}
						} else {
							mLastMatch = -1;
						}
					}
					mLastDiff[k] = diff;
				}
				mLastDirections[k] = direction;
				mLastValues[k] = v;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
