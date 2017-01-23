package com.vrem.wifianalyzer.sensor_fusion;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Class that tests availability of hardware sensors.
 *
 *
 */
public class HardwareChecker implements SensorChecker {

	boolean gyroscopeIsAvailable = false;
	
	public HardwareChecker (SensorManager sensorManager) {
		if(sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
			gyroscopeIsAvailable = true;
		}
		List<Sensor> a = sensorManager.getSensorList(Sensor.TYPE_ALL);
	}

	public List<Sensor> getAllSensors(SensorManager sensorManager) {
		return  sensorManager.getSensorList(Sensor.TYPE_ALL);
	}
	
	@Override
	public boolean IsGyroscopeAvailable() {
		return gyroscopeIsAvailable;
	}

}
