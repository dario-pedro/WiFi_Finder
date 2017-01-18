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

import com.vrem.wifianalyzer.MainContextHelper;
import com.vrem.wifianalyzer.settings.Settings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheTest {
    @Mock
    private ScanResult scanResult1;
    @Mock
    private ScanResult scanResult2;
    @Mock
    private ScanResult scanResult3;
    @Mock
    private ScanResult scanResult4;
    @Mock
    private ScanResult scanResult5;
    @Mock
    private ScanResult scanResult6;

    private Settings settings;
    private Cache fixture;

    @Before
    public void setUp() {
        fixture = new Cache();

        settings = MainContextHelper.INSTANCE.getSettings();
    }

    @After
    public void tearDown() {
        MainContextHelper.INSTANCE.restore();
    }

    @Test
    public void testAddWithNulls() throws Exception {
        // setup
        when(settings.getScanInterval()).thenReturn(5);
        // execute
        fixture.add(null);
        // validate
        assertTrue(fixture.getCache().isEmpty());
    }

    @Test
    public void testAdd() throws Exception {
        // setup
        when(settings.getScanInterval()).thenReturn(5);
        List<ScanResult> scanResults = new ArrayList<>();
        // execute
        fixture.add(scanResults);
        // validate
        assertEquals(scanResults, fixture.getCache().getFirst());
    }

    @Test
    public void testAddCompliesToMaxCacheSize() throws Exception {
        // setup
        int scanInterval = 5;
        int cacheSize = 2;
        when(settings.getScanInterval()).thenReturn(scanInterval);
        List<List<ScanResult>> expected = new ArrayList<>();
        // execute
        for (int i = 0; i < cacheSize; i++) {
            List<ScanResult> scanResults = new ArrayList<>();
            expected.add(scanResults);
            fixture.add(scanResults);
        }
        // validate
        assertEquals(cacheSize, expected.size());
        assertEquals(cacheSize, fixture.getCache().size());
        assertEquals(expected.get(cacheSize - 1), fixture.getCache().getFirst());
        assertEquals(expected.get(cacheSize - 2), fixture.getCache().getLast());
    }

    @Test
    public void testGetWiFiData() throws Exception {
        // setup
        withScanResults();
        // execute
        List<CacheResult> actuals = fixture.getScanResults();
        // validate
        assertEquals(3, actuals.size());
        validate(scanResult3, 20, actuals.get(0));
        validate(scanResult4, 50, actuals.get(1));
        validate(scanResult6, 10, actuals.get(2));
    }

    private void validate(ScanResult expectedScanResult, int expectedLevel, CacheResult actual) {
        assertEquals(expectedScanResult, actual.getScanResult());
        assertEquals(expectedLevel, actual.getLevelAverage());
    }

    private void withScanResults() {
        scanResult1.BSSID = scanResult2.BSSID = scanResult3.BSSID = "BBSID1";
        scanResult1.level = 10;
        scanResult2.level = 20;
        scanResult3.level = 30;

        scanResult4.BSSID = scanResult5.BSSID = "BBSID2";
        scanResult4.level = 60;
        scanResult5.level = 40;

        scanResult6.BSSID = "BBSID3";
        scanResult6.level = 10;

        fixture.add(Arrays.asList(scanResult1, scanResult4));
        fixture.add(Arrays.asList(scanResult2, scanResult5));
        fixture.add(Arrays.asList(scanResult3, scanResult6));
    }

    @Test
    public void testGetCacheSize() throws Exception {
        int values[] = new int[]{
            1, 4,
            4, 4,
            5, 3,
            9, 3,
            10, 2,
            19, 2,
            20, 1
        };

        for (int i = 0; i < values.length; i += 2) {
            when(settings.getScanInterval()).thenReturn(values[i]);
            assertEquals("Scan Interval:" + values[i], values[i + 1], fixture.getCacheSize());
        }
        verify(settings, times(values.length / 2)).getScanInterval();
    }

}