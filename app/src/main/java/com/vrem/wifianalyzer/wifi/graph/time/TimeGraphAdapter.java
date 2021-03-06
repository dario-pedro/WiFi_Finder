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

package com.vrem.wifianalyzer.wifi.graph.time;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphAdapter;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphViewNotifier;

import java.util.ArrayList;
import java.util.List;

class TimeGraphAdapter extends GraphAdapter {
    TimeGraphAdapter() {
        super(makeGraphViewNotifiers());
    }

    private static List<GraphViewNotifier> makeGraphViewNotifiers() {
        List<GraphViewNotifier> graphViewNotifiers = new ArrayList<>();

        for (WiFiBand wiFiBand : WiFiBand.values()) {
            graphViewNotifiers.add(new TimeGraphView(wiFiBand));
        }
        return graphViewNotifiers;
    }
}
