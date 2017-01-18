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
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.vendor.model.Database;
import com.vrem.wifianalyzer.vendor.model.VendorService;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;

public enum MainContext {
    INSTANCE;

    private Settings settings;
    private MainActivity mainActivity;
    private Scanner scanner;
    private VendorService vendorService;
    private Database database;
    private Configuration configuration;

    public Settings getSettings() {
        return settings;
    }

    void setSettings(Settings settings) {
        this.settings = settings;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public Scanner getScanner() {
        return scanner;
    }

    void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public Database getDatabase() {
        return database;
    }

    void setDatabase(Database database) {
        this.database = database;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    void initialize(@NonNull MainActivity mainActivity, boolean isLargeScreenLayout) {
        WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        Handler handler = new Handler();
        Settings settings = new Settings(mainActivity);
        Configuration configuration = new Configuration(isLargeScreenLayout);

        setMainActivity(mainActivity);
        setConfiguration(configuration);
        setDatabase(new Database(mainActivity));
        setSettings(settings);
        setVendorService(new VendorService());
        setScanner(new Scanner(wifiManager, handler, settings));
    }
}
