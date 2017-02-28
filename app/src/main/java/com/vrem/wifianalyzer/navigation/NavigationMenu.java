
package com.vrem.wifianalyzer.navigation;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.vrem.wifianalyzer.localization.FindApFragment;
import com.vrem.wifianalyzer.login.LoginActivity;
import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.contacts.ContactsFragment;
//import com.vrem.wifianalyzer.maps.MapsActivity;
import com.vrem.wifianalyzer.maps.MapsActivity;
import com.vrem.wifianalyzer.odometry.OdometryFragment;
import com.vrem.wifianalyzer.sensor_fusion.SensorSelectionActivity;
import com.vrem.wifianalyzer.settings.SettingActivity;
import com.vrem.wifianalyzer.steps.StepCounterActivity;
import com.vrem.wifianalyzer.vendor.VendorFragment;
import com.vrem.wifianalyzer.wifi.AccessPointsFragment;
import com.vrem.wifianalyzer.wifi.ChannelAvailableFragment;
import com.vrem.wifianalyzer.wifi.ChannelRatingFragment;
import com.vrem.wifianalyzer.wifi.graph.channel.ChannelGraphFragment;
import com.vrem.wifianalyzer.wifi.graph.time.TimeGraphFragment;

//import static com.vrem.wifianalyzer.R.drawable.cast_ic_expanded_controller_play;

public enum NavigationMenu {
    LOGIN(R.drawable.ic_settings_grey_500_48dp, R.string.action_login, new ActivityItem(LoginActivity.class)),


    ACCESS_POINTS(R.drawable.ic_network_wifi_grey_500_48dp, R.string.action_access_points, true, new FragmentItem(new AccessPointsFragment())),
    CHANNEL_RATING(R.drawable.ic_wifi_tethering_grey_500_48dp, R.string.action_channel_rating, true, new FragmentItem(new ChannelRatingFragment())),
    CHANNEL_GRAPH(R.drawable.ic_insert_chart_grey_500_48dp, R.string.action_channel_graph, true, new FragmentItem(new ChannelGraphFragment())),
    TIME_GRAPH(R.drawable.ic_show_chart_grey_500_48dp, R.string.action_time_graph, true, new FragmentItem(new TimeGraphFragment())),


    GOOGLE_MAPS(R.drawable.ic_media_play, R.string.action_gmaps, new ActivityItem(MapsActivity.class)),
    FIND_AP(R.drawable.ic_leak_add_black_24dp, R.string.action_findap, new FragmentItem(new FindApFragment())),
    ODOMETRY(R.drawable.ic_transfer_within_a_station_black_24dp, R.string.action_odometry, new FragmentItem(new OdometryFragment())),
    SENSOR_FUSION(R.drawable.ic_developer_mode_black_24dp, R.string.action_sensorf, new ActivityItem(SensorSelectionActivity.class)),
    STEP_COUNTER(R.drawable.ic_directions_run_black_24dp, R.string.action_step_counter, new ActivityItem(StepCounterActivity.class)),


    CHANNEL_AVAILABLE(R.drawable.ic_location_on_grey_500_48dp, R.string.action_channel_available, new FragmentItem(new ChannelAvailableFragment())),
    VENDOR_LIST(R.drawable.ic_list_grey_500_48dp, R.string.action_vendors, new FragmentItem(new VendorFragment())),


    EXPORT(R.drawable.ic_import_export_grey_500_48dp, R.string.action_export, new ExportItem()),
    SENDDB(R.drawable.ic_dns_black_24dp, R.string.action_senddb, new SendDBItem()),
    SETTINGS(R.drawable.ic_settings_grey_500_48dp, R.string.action_settings, new ActivityItem(SettingActivity.class)),
    CONTACTS(R.drawable.ic_info_outline_grey_500_48dp, R.string.action_contacts, new FragmentItem(new ContactsFragment()));

    private final int icon;
    private final int title;
    private final boolean wiFiBandSwitchable;
    private final NavigationMenuItem item;

    NavigationMenu(int icon, int title, boolean wiFiBandSwitchable, @NonNull NavigationMenuItem item) {
        this.icon = icon;
        this.title = title;
        this.wiFiBandSwitchable = wiFiBandSwitchable;
        this.item = item;
    }

    NavigationMenu(int icon, int title, @NonNull NavigationMenuItem item) {
        this.icon = icon;
        this.title = title;
        this.wiFiBandSwitchable = false;
        this.item = item;
    }

    public static NavigationMenu find(int index) {
        if (index < 0 || index >= values().length) {
            return ACCESS_POINTS;
        }
        return values()[index];
    }

    public int getTitle() {
        return title;
    }

    public boolean isWiFiBandSwitchable() {
        return wiFiBandSwitchable;
    }

    int getIcon() {
        return icon;
    }

    public void activateNavigationMenu(@NonNull MainActivity mainActivity, @NonNull MenuItem menuItem) {
        item.activate(mainActivity, menuItem, this);
    }

    NavigationMenuItem getItem() {
        return item;
    }
}
