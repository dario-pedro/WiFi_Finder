/*
 * WiFi Analyzer
 * Copyright (C) 2016  VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.vrem.wifianalyzer.navigation;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.localization.PositionPoint;
import com.vrem.wifianalyzer.odometry.Coordinates;
import com.vrem.wifianalyzer.odometry.MutipleDistanceMeasurements;
import com.vrem.wifianalyzer.odometry.Odom;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.model.WiFiSignal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;

class ExportItem implements NavigationMenuItem {

    @Override
    public void activate(@NonNull MainActivity mainActivity, @NonNull MenuItem menuItem, @NonNull NavigationMenu navigationMenu) {

        String title = getTitle(mainActivity);
        List<WiFiDetail> wiFiDetails = getWiFiDetails();
        List<PositionPoint> estimatives = getCoordsDetails();
        List<List<PositionPoint>> allPoints = getPositionPointsDetails();
        List<Double> dTests = getDoubleTests();
        List<MutipleDistanceMeasurements> gpsAndOdom = getGpsAndOdom();


        if (!dataAvailable(wiFiDetails)) {
            Toast.makeText(mainActivity, R.string.no_data, Toast.LENGTH_LONG).show();
            return;
        }
        String data = getWifiData(wiFiDetails);


        if(!estimatives.isEmpty())
        {
            data += "\n";
            data += getCoordsData(estimatives);
        }

        if(!allPoints.isEmpty())
        {
            data += "\n";
            data += getPositionPointsData(allPoints);
        }

        if(!dTests.isEmpty())
        {
            data += "\n";
            data += getDoublesData(dTests);
        }

        if(!gpsAndOdom.isEmpty())
        {
            data += "\n";
            data += getGpsAndOdomData(gpsAndOdom);
        }

        Intent intent = createIntent(title, data);
        Intent chooser = createChooserIntent(intent, title);
        if (!exportAvailable(mainActivity, chooser)) {
            Toast.makeText(mainActivity, R.string.export_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        mainActivity.startActivity(chooser);
    }

    private boolean exportAvailable(@NonNull MainActivity mainActivity, @NonNull Intent chooser) {
        return chooser.resolveActivity(mainActivity.getPackageManager()) != null;
    }

    private boolean dataAvailable(@NonNull List<WiFiDetail> wiFiDetails) {
        return !wiFiDetails.isEmpty();
    }

    String getWifiData(@NonNull List<WiFiDetail> wiFiDetails) {
        StringBuilder result = new StringBuilder();
        result.append("WiFi Information:\n");
        result.append("SSID|BSSID|Strength|Primary Channel|Primary Frequency|Center Channel|Center Frequency|Width (Range)|Distance|Security\n");
        for (WiFiDetail wiFiDetail : wiFiDetails) {
            WiFiSignal wiFiSignal = wiFiDetail.getWiFiSignal();
            result.append(String.format(Locale.ENGLISH, "%s|%s|%ddBm|%d|%d%s|%d|%d%s|%d%s (%d - %d)|%.1fm|%s\n\n",
                    wiFiDetail.getSSID(),
                    wiFiDetail.getBSSID(),
                    wiFiSignal.getLevel(),
                    wiFiSignal.getPrimaryWiFiChannel().getChannel(),
                    wiFiSignal.getPrimaryFrequency(),
                    WiFiSignal.FREQUENCY_UNITS,
                    wiFiSignal.getCenterWiFiChannel().getChannel(),
                    wiFiSignal.getCenterFrequency(),
                    WiFiSignal.FREQUENCY_UNITS,
                    wiFiSignal.getWiFiWidth().getFrequencyWidth(),
                    WiFiSignal.FREQUENCY_UNITS,
                    wiFiSignal.getFrequencyStart(),
                    wiFiSignal.getFrequencyEnd(),
                    wiFiSignal.getDistance(),
                    wiFiDetail.getCapabilities()));
        }
        return result.toString();
    }



    String getDoublesData(@NonNull List<Double> doubles) {
        StringBuilder result = new StringBuilder();
        result.append("Double Information:\n");
        //result.append("(x,y,z) \n\n");

        int i = 1;

        //double[] events = {0d,0d,0d};

        for (Double d : doubles) {

            result.append(String.format(Locale.ENGLISH, "%d ,%.0f\n",
                    i, d));


              /*int index = i%3;

              if(index == 0) {
                  events[index+2] = d;
                  result.append(String.format(Locale.ENGLISH, "%d. ,%.5f,%.5f,%.5f\n",
                          i/3, events[0], events[1],events[2]));
              }else {
                  events[index - 1] = d;
              }*/


            /*else
                result.append(String.format(Locale.ENGLISH, "estimative. (%.2f,%.2f)\n",
                        positionPoint.getPosition().getX()/100,positionPoint.getPosition().getY()/100));*/

            ++i;
        }
        return result.toString();
    }

    String getGpsAndOdomData(@NonNull List<MutipleDistanceMeasurements> listMDM) {
        StringBuilder result = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.#######", DecimalFormatSymbols.getInstance());

        result.append("Gps And Odom Info Information:\n");
        result.append("X,Y,GPS_LAT,GPS,LONG:\n");

        String r = result.toString();

        for (MutipleDistanceMeasurements mdm : listMDM) {


            //odometry location
            String currX = df.format(mdm.mOdometryCoords.getX()/100);
            String currY = df.format(mdm.mOdometryCoords.getY()/100);
            r+= currX.replace(",",".")+","+currY.replace(",",".")+",";

            //Android Location estimation
            LatLng latLng = mdm.mAndroidLocationCoords;
            if(latLng != null) {
                String lat = df.format(latLng.latitude);
                String lon = df.format(latLng.longitude);
                r+= lat.replace(",",".")+","+lon.replace(",",".")+"\n";
            }
            else {
                r+= "\n";
            }

        }

        return r;
    }


    String getCoordsData(@NonNull List<PositionPoint> coordinates) {
        StringBuilder result = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.#######", DecimalFormatSymbols.getInstance());

        result.append("Coordinates Information:\n");
        result.append("Date,x,y,distance,PrimaryFrequency,power,ESTx,ESTy,GPS_LATR,GPS_LONG\n");

        String r = result.toString();

        for (PositionPoint positionPoint : coordinates) {

            //adds the Date
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(positionPoint.getStore_time());
            r+= date+",";

            //odometry location
            String currX = df.format(positionPoint.getPosition().getX()/100);
            String currY = df.format(positionPoint.getPosition().getY()/100);
            r+= currX.replace(",",".")+","+currY.replace(",",".")+",";

            //distance to AP
            String distance = df.format(positionPoint.getDistance()/100);
            r+= distance.replace(",",".")+",";

            //ws info
            WiFiSignal ws = positionPoint.getInfo().get(0).getWiFiSignal();
            r+=""+ws.getPrimaryFrequency()+","+ws.getLevel()+",";

            //estimation location
            String estX = df.format(positionPoint.getAPestimation().getX()/100);
            String estY = df.format(positionPoint.getAPestimation().getY()/100);
            r+= estX.replace(",",".")+","+estY.replace(",",".")+",";

            //Android Location estimation
            LatLng latLng = positionPoint.getmLL();
            if(latLng != null) {
                String lat = df.format(latLng.latitude);
                String lon = df.format(latLng.longitude);
                r+= lat.replace(",",".")+","+lon.replace(",",".")+"\n";
            }
            else {
                r+= "\n";
            }


        }
        return r;
    }

    String getPositionPointsData(@NonNull List<List<PositionPoint>> list_coordinates) {
        StringBuilder result = new StringBuilder();



        int list_type = 0;


        for(List<PositionPoint> coordinates : list_coordinates) {

            if(coordinates.size()>0) {
                result.append("\nAll Points using:" + ListType_IntToString(list_type) + "\n");
                result.append("(x,y) in meters\n\n");
                result.append("angle in degrees\n\n");
            }

            list_type++;
            int i = 1;

            for (PositionPoint positionPoint : coordinates) {

                if (positionPoint.getDistance() < 10000000)

                    result.append(String.format(Locale.ENGLISH, "%d. ,%.2f,%.2f, %.2f\n",
                            i++, positionPoint.getPosition().getX() / 100, positionPoint.getPosition().getY() / 100,
                            positionPoint.getDistance() /*/ 100*/));
                else

                    result.append(String.format(Locale.ENGLISH, "%d. ,%.2f,%.2f\n",
                            i++, positionPoint.getPosition().getX() / 100, positionPoint.getPosition().getY() / 100));


            }
        }
        return result.toString();
    }

    private String ListType_IntToString(int list_type) {
        switch (list_type){
            case Odom.ACCELEROMETERCOMPASSPROVIDER : return "ACCELEROMETER and COMPASS PROVIDER";
            case Odom.CALIBRATEDGYROSCOPEPROVIDER : return "CALIBRATED GYROSCOPE PROVIDER";
            case Odom.GRAVITYCOMPASSPROVIDER : return "GRAVITY and COMPASS PROVIDER";
            case Odom.IMPROVEDORIENTATIONSENSOR1PROVIDER : return "IMPROVED ORIENTATION SENSOR 1 PROVIDER";
            case Odom.IMPROVEDORIENTATIONSENSOR2PROVIDER : return "IMPROVED ORIENTATION SENSOR 2 PROVIDER";
            case Odom.ROTATIONVECTORPROVIDER : return "ROTATION VECTOR PROVIDER";
            default: return "";
    }
    }




    private List<WiFiDetail> getWiFiDetails() {
        return MainContext.INSTANCE.getScanner().getWiFiData().getWiFiDetails();
    }

    private List<PositionPoint> getCoordsDetails() {
        return MainContext.INSTANCE.getmEstimativesList();
    }

    private List<Double> getDoubleTests() {
        return MainContext.INSTANCE.getmTests();
    }

    private List<MutipleDistanceMeasurements> getGpsAndOdom() {
        return MainContext.INSTANCE.getGPSandOdom();
    }

    private List<List<PositionPoint>> getPositionPointsDetails() {
        return MainContext.INSTANCE.getmAllPointsList();
    }

    @NonNull
    private String getTitle(@NonNull MainActivity mainActivity) {
        Resources resources = mainActivity.getResources();
        return resources.getString(R.string.action_access_points);
    }

    private Intent createIntent(String title, String data) {
        Intent intent = createSendIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        return intent;
    }

    Intent createSendIntent() {
        return new Intent(Intent.ACTION_SEND);
    }

    Intent createChooserIntent(@NonNull Intent intent, @NonNull String title) {
        return Intent.createChooser(intent, title);
    }

}
