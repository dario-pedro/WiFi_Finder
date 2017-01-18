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

package com.vrem.wifianalyzer.wifi.graph.channel;

import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Button;

import com.vrem.wifianalyzer.BuildConfig;
import com.vrem.wifianalyzer.Configuration;
import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.MainContextHelper;
import com.vrem.wifianalyzer.RobolectricUtil;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.graph.channel.ChannelGraphNavigation.NavigationItem;
import com.vrem.wifianalyzer.wifi.scanner.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ChannelGraphNavigationTest {
    private Scanner scanner;
    private Settings settings;
    private Configuration configuration;
    private ChannelGraphNavigation fixture;

    @Before
    public void setUp() {
        MainActivity mainActivity = RobolectricUtil.INSTANCE.getActivity();

        scanner = MainContextHelper.INSTANCE.getScanner();
        settings = MainContextHelper.INSTANCE.getSettings();
        configuration = MainContextHelper.INSTANCE.getConfiguration();

        fixture = new ChannelGraphNavigation(mainActivity, configuration);
    }

    @After
    public void tearDown() {
        MainContextHelper.INSTANCE.restore();
    }

    @Test
    public void testChannelGraphNavigation() throws Exception {
        verify(configuration, times(3)).isLargeScreenLayout();
    }

    @Test
    public void testGetNavigationItems() throws Exception {
        // execute
        List<NavigationItem> actual = fixture.getNavigationItems();
        // validate
        assertEquals(3, actual.size());
    }

    @Test
    public void testLargeScreen() throws Exception {
        // setup
        when(configuration.isLargeScreenLayout()).thenReturn(true);
        // execute
        List<NavigationItem> actual = fixture.getNavigationItems();
        // validate
        assertEquals(3, actual.size());
        verify(configuration, times(3)).isLargeScreenLayout();
    }

    @Test
    public void testUpdateWithGHZ2() throws Exception {
        // setup
        when(settings.getCountryCode()).thenReturn(Locale.US.getCountry());
        when(settings.getWiFiBand()).thenReturn(WiFiBand.GHZ2);
        // execute
        fixture.update();
        // validate
        List<NavigationItem> navigationItems = fixture.getNavigationItems();
        for (NavigationItem navigationItem : navigationItems) {
            Button button = navigationItem.getButton();
            assertEquals(View.GONE, button.getVisibility());
            assertFalse(button.isSelected());
        }
        verify(settings).getCountryCode();
        verify(settings).getWiFiBand();
    }

    @Test
    public void testUpdateWithGHZ5AndUS() throws Exception {
        // setup
        when(configuration.getWiFiChannelPair()).thenReturn(WiFiBand.GHZ5.getWiFiChannels().getWiFiChannelPairs().get(0));
        when(settings.getCountryCode()).thenReturn(Locale.US.getCountry());
        when(settings.getWiFiBand()).thenReturn(WiFiBand.GHZ5);
        // execute
        fixture.update();
        // validate
        List<NavigationItem> navigationItems = fixture.getNavigationItems();

        Button button = navigationItems.get(0).getButton();
        assertEquals(View.VISIBLE, button.getVisibility());
        assertTrue(button.isSelected());

        button = navigationItems.get(1).getButton();
        assertEquals(View.VISIBLE, button.getVisibility());
        assertFalse(button.isSelected());

        button = navigationItems.get(2).getButton();
        assertEquals(View.VISIBLE, button.getVisibility());
        assertFalse(button.isSelected());

        verify(settings).getCountryCode();
        verify(settings).getWiFiBand();
        verify(configuration).getWiFiChannelPair();
    }

    @Test
    public void testUpdateGHZ5WithJapan() throws Exception {
        // setup
        when(settings.getCountryCode()).thenReturn(Locale.JAPAN.getCountry());
        when(settings.getWiFiBand()).thenReturn(WiFiBand.GHZ5);
        // execute
        fixture.update();
        // validate
        List<NavigationItem> navigationItems = fixture.getNavigationItems();
        assertEquals(View.VISIBLE, navigationItems.get(0).getButton().getVisibility());
        assertEquals(View.VISIBLE, navigationItems.get(1).getButton().getVisibility());
        assertEquals(View.GONE, navigationItems.get(2).getButton().getVisibility());
        verify(settings).getCountryCode();
        verify(settings).getWiFiBand();
    }

    @Test
    public void testUpdateGHZ5WithCountryThatHasOnlyOneSet() throws Exception {
        // setup
        when(settings.getCountryCode()).thenReturn("IL");
        when(settings.getWiFiBand()).thenReturn(WiFiBand.GHZ5);
        // execute
        fixture.update();
        // validate
        List<NavigationItem> navigationItems = fixture.getNavigationItems();
        assertEquals(View.GONE, navigationItems.get(0).getButton().getVisibility());
        assertEquals(View.GONE, navigationItems.get(1).getButton().getVisibility());
        assertEquals(View.GONE, navigationItems.get(2).getButton().getVisibility());
        verify(settings).getCountryCode();
        verify(settings).getWiFiBand();
    }

    @Test
    public void testSelectNavigationUpdatesConfigurationAndScans() throws Exception {
        // setup
        Pair<WiFiChannel, WiFiChannel> expected = WiFiBand.GHZ5.getWiFiChannels().getWiFiChannelPairs().get(0);
        NavigationItem navigationItem = fixture.getNavigationItems().get(0);
        // execute
        navigationItem.getButton().callOnClick();
        // validate
        verify(configuration).setWiFiChannelPair(expected);
        verify(scanner).update();
    }

}