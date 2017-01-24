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

package com.vrem.wifianalyzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.vrem.wifianalyzer.navigation.NavigationMenu;
import com.vrem.wifianalyzer.navigation.NavigationMenuView;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.settings.ThemeStyle;
import com.vrem.wifianalyzer.wifi.AccessPointView;
import com.vrem.wifianalyzer.wifi.ConnectionView;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener, OnNavigationItemSelectedListener {
    private ThemeStyle currentThemeStyle;
    private AccessPointView currentAccessPointView;
    private NavigationMenuView navigationMenuView;
    private NavigationMenu startNavigationMenu;
    private String currentCountryCode;
    private ConnectionView connectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainContext mainContext = MainContext.INSTANCE;
        mainContext.initialize(this, isLargeScreenLayout());

        Settings settings = mainContext.getSettings();
        settings.initializeDefaultValues();
        setCurrentThemeStyle(settings.getThemeStyle());
        setCurrentAccessPointView(settings.getAccessPointView());
        setTheme(getCurrentThemeStyle().themeAppCompatStyle());
        setWiFiChannelPairs();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        settings.registerOnSharedPreferenceChangeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new WiFiBandToggle());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        startNavigationMenu = settings.getStartMenu();
        navigationMenuView = new NavigationMenuView(this, startNavigationMenu);
        onNavigationItemSelected(navigationMenuView.getCurrentMenuItem());

        connectionView = new ConnectionView(this);
        Scanner scanner = mainContext.getScanner();
        scanner.register(connectionView);

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);




    }



    ConnectionView getConnectionView() {
        return connectionView;
    }

    private void setWiFiChannelPairs() {
        Settings settings = MainContext.INSTANCE.getSettings();
        String countryCode = settings.getCountryCode();
        if (!countryCode.equals(currentCountryCode)) {
            Pair<WiFiChannel, WiFiChannel> pair = WiFiBand.GHZ5.getWiFiChannels().getWiFiChannelPairFirst(countryCode);
            Configuration configuration = MainContext.INSTANCE.getConfiguration();
            configuration.setWiFiChannelPair(pair);
            currentCountryCode = countryCode;
        }
    }

    private boolean isLargeScreenLayout() {
        Resources resources = getResources();
        android.content.res.Configuration configuration = resources.getConfiguration();
        int screenLayoutSize = configuration.screenLayout & android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayoutSize == android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE ||
            screenLayoutSize == android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (shouldReload()) {
            reloadActivity();
        } else {
            setWiFiChannelPairs();
            Scanner scanner = MainContext.INSTANCE.getScanner();
            scanner.update();
            updateSubTitle();
        }
    }

    boolean shouldReload() {
        Settings settings = MainContext.INSTANCE.getSettings();
        return isThemeChanged(settings) || isAccessPointViewChanged(settings);
    }

    private boolean isAccessPointViewChanged(Settings settings) {
        AccessPointView settingAccessPointView = settings.getAccessPointView();
        boolean accessPointViewChanged = !getCurrentAccessPointView().equals(settingAccessPointView);
        if (accessPointViewChanged) {
            setCurrentAccessPointView(settingAccessPointView);
        }
        return accessPointViewChanged;
    }

    private boolean isThemeChanged(Settings settings) {
        ThemeStyle settingThemeStyle = settings.getThemeStyle();
        boolean themeChanged = !getCurrentThemeStyle().equals(settingThemeStyle);
        if (themeChanged) {
            setCurrentThemeStyle(settingThemeStyle);
        }
        return themeChanged;
    }

    private void reloadActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP |
            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            if (startNavigationMenu.equals(navigationMenuView.getCurrentNavigationMenu())) {
                super.onBackPressed();
            } else {
                navigationMenuView.setCurrentNavigationMenu(startNavigationMenu);
                onNavigationItemSelected(navigationMenuView.getCurrentMenuItem());
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        NavigationMenu.find(menuItem.getItemId()).activateNavigationMenu(this, menuItem);
        return true;
    }

    private boolean closeDrawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.resume();
    }

    @Override
    protected void onDestroy() {
        Scanner scanner = MainContext.INSTANCE.getScanner();
        scanner.unregister(connectionView);
        super.onDestroy();
    }

    public void updateSubTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(makeSubtitle());
        }
    }

    private CharSequence makeSubtitle() {
        NavigationMenu navigationMenu = navigationMenuView.getCurrentNavigationMenu();
        Settings settings = MainContext.INSTANCE.getSettings();
        CharSequence subtitle = StringUtils.EMPTY;
        if (navigationMenu.isWiFiBandSwitchable()) {
            int color = ContextCompat.getColor(this, R.color.connected);
            WiFiBand currentWiFiBand = settings.getWiFiBand();
            String subtitleText = makeSubtitleText("<font color='" + color + "'><strong>", "</strong></font>", "<small>", "</small>");
            if (WiFiBand.GHZ5.equals(currentWiFiBand)) {
                subtitleText = makeSubtitleText("<small>", "</small>", "<font color='" + color + "'><strong>", "</strong></font>");
            }
            subtitle = fromHtml(subtitleText);
        }
        return subtitle;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    Spanned fromHtml(@NonNull String subtitleText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(subtitleText, Html.FROM_HTML_MODE_LEGACY);
        }
        return Html.fromHtml(subtitleText);
    }

    @NonNull
    private String makeSubtitleText(@NonNull String tag1, @NonNull String tag2, @NonNull String tag3, @NonNull String tag4) {
        return tag1 + WiFiBand.GHZ2.getBand() + tag2 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + tag3 + WiFiBand.GHZ5.getBand() + tag4;
    }

    public NavigationMenuView getNavigationMenuView() {
        return navigationMenuView;
    }

    ThemeStyle getCurrentThemeStyle() {
        return currentThemeStyle;
    }

    void setCurrentThemeStyle(@NonNull ThemeStyle currentThemeStyle) {
        this.currentThemeStyle = currentThemeStyle;
    }

    public AccessPointView getCurrentAccessPointView() {
        return currentAccessPointView;
    }

    public void setCurrentAccessPointView(@NonNull AccessPointView currentAccessPointView) {
        this.currentAccessPointView = currentAccessPointView;
    }

    private class WiFiBandToggle implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (navigationMenuView.getCurrentNavigationMenu().isWiFiBandSwitchable()) {
                Settings settings = MainContext.INSTANCE.getSettings();
                settings.toggleWiFiBand();
            }
        }
    }
}
