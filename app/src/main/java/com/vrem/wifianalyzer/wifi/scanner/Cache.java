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

package com.vrem.wifianalyzer.wifi.scanner;

import android.net.wifi.ScanResult;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.settings.Settings;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

class Cache {
    private final Deque<List<ScanResult>> cache = new ArrayDeque<>();

    List<CacheResult> getScanResults() {
        ScanResult current = null;
        int levelTotal = 0;
        int count = 0;
        List<CacheResult> results = new ArrayList<>();
        for (ScanResult scanResult : combineCache()) {
            if (current != null && !scanResult.BSSID.equals(current.BSSID)) {
                results.add(new CacheResult(current, levelTotal / count));
                count = 0;
                levelTotal = 0;
            }
            current = scanResult;
            count++;
            levelTotal += scanResult.level;
        }
        if (current != null) {
            results.add(new CacheResult(current, levelTotal / count));
        }
        return results;
    }

    private List<ScanResult> combineCache() {
        List<ScanResult> scanResults = new ArrayList<>();
        for (List<ScanResult> cachedScanResults : cache) {
            scanResults.addAll(cachedScanResults);
        }
        Collections.sort(scanResults, new ScanResultComparator());
        return scanResults;
    }

    void add(List<ScanResult> scanResults) {
        int cacheSize = getCacheSize();
        while (cache.size() >= cacheSize) {
            cache.pollLast();
        }
        if (scanResults != null) {
            cache.addFirst(scanResults);
        }
    }

    Deque<List<ScanResult>> getCache() {
        return cache;
    }

    int getCacheSize() {
        Settings settings = MainContext.INSTANCE.getSettings();
        int scanInterval = settings.getScanInterval();
        if (scanInterval < 4) {
            return 4;
        }
        if (scanInterval < 8) {
            return 3;
        }
        if (scanInterval < 12) {
            return 2;
        }
        if (scanInterval < 20) {
            return 2;
        }
        return 1;
    }

    private static class ScanResultComparator implements Comparator<ScanResult> {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            return new CompareToBuilder()
                .append(lhs.BSSID, rhs.BSSID)
                .append(lhs.level, rhs.level)
                .toComparison();
        }
    }
}
