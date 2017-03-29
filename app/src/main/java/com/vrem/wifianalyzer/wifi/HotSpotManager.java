package com.vrem.wifianalyzer.wifi;

/**
 * Created by Dário on 27/03/2017.
 */

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;


/**
 * This class is use to handle all Hotspot related information.
 */
public class HotSpotManager {
    private static Method getWifiApState;
    private static Method isWifiApEnabled;
    private static Method setWifiApEnabled;
    private static Method getWifiApConfiguration;

    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    public static final int WIFI_AP_STATE_DISABLED = WifiManager.WIFI_STATE_DISABLED;
    public static final int WIFI_AP_STATE_DISABLING = WifiManager.WIFI_STATE_DISABLING;
    public static final int WIFI_AP_STATE_ENABLED = WifiManager.WIFI_STATE_ENABLED;
    public static final int WIFI_AP_STATE_ENABLING = WifiManager.WIFI_STATE_ENABLING;
    public static final int WIFI_AP_STATE_FAILED = WifiManager.WIFI_STATE_UNKNOWN;

    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = WifiManager.EXTRA_PREVIOUS_WIFI_STATE;
    public static final String EXTRA_WIFI_AP_STATE = WifiManager.EXTRA_WIFI_STATE;

    public static Boolean hotSpotEnable = false;

    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getWifiApState")) {
                getWifiApState = method;
            } else if (methodName.equals("isWifiApEnabled")) {
                isWifiApEnabled = method;
            } else if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
            } else if (methodName.equals("getWifiApConfiguration")) {
                getWifiApConfiguration = method;
            }
        }
    }

    public static boolean isApSupported() {
        return (getWifiApState != null && isWifiApEnabled != null
                && setWifiApEnabled != null && getWifiApConfiguration != null);
    }

    private WifiManager mgr;

    private HotSpotManager(WifiManager mgr) {
        this.mgr = mgr;
    }

    public static HotSpotManager getApControl(WifiManager mgr) {
        if (!isApSupported())
            return null;
        return new HotSpotManager(mgr);
    }

    public boolean isWifiApEnabled() {
        try {
            return (Boolean) isWifiApEnabled.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return false;
        }
    }

    public int getWifiApState() {
        try {
            return (Integer) getWifiApState.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return -1;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            return (WifiConfiguration) getWifiApConfiguration.invoke(mgr);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return null;
        }
    }

    public boolean setWifiApEnabled(WifiConfiguration config, boolean enabled) {
        try {
            return (Boolean) setWifiApEnabled.invoke(mgr, config, enabled);
        } catch (Exception e) {
            Log.v("BatPhone", e.toString(), e); // shouldn't happen
            return false;
        }
    }

    /**
     * Turn on or off Hotspot.
     *
     * @param context
     * @param isTurnToOn
     */
    public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {

        hotSpotEnable = isTurnToOn;

        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        HotSpotManager apControl = HotSpotManager.getApControl(wifiManager);
        if (apControl != null) {

            // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
            //if (isWifiOn(context) && isTurnToOn) {
            //  turnOnOffWifi(context, false);
            //}

            apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                    isTurnToOn);
        }
    }

} // end of class
