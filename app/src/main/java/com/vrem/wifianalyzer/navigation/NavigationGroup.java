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

import android.support.annotation.NonNull;

public enum NavigationGroup {
    GROUP_TODO(NavigationMenu.LOGIN),
    GROUP_WIFI(NavigationMenu.ACCESS_POINTS, NavigationMenu.CHANNEL_RATING, NavigationMenu.CHANNEL_GRAPH, NavigationMenu.TIME_GRAPH),
    GROUP_ODOM(NavigationMenu.GOOGLE_MAPS,NavigationMenu.FIND_AP,NavigationMenu.ODOMETRY,
            NavigationMenu.SENSOR_FUSION,NavigationMenu.STEP_COUNTER),
    GROUP_COOP(NavigationMenu.CHAT),
    GROUP_OTHER(NavigationMenu.CHANNEL_AVAILABLE, NavigationMenu.VENDOR_LIST),
    GROUP_SETTINGS(NavigationMenu.EXPORT,NavigationMenu.SENDDB,NavigationMenu.SETTINGS,NavigationMenu.ACCESS_POINTS.CONTACTS);

    private final NavigationMenu[] navigationMenu;

    NavigationGroup(@NonNull NavigationMenu... navigationMenu) {
        this.navigationMenu = navigationMenu;
    }

    public NavigationMenu[] navigationMenu() {
        return navigationMenu;
    }
}
