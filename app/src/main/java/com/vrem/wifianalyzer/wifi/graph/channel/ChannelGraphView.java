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

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.TitleLineGraphSeries;
import com.vrem.wifianalyzer.Configuration;
import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.band.WiFiChannels;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphColor;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphLegend;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphViewBuilder;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphViewNotifier;
import com.vrem.wifianalyzer.wifi.graph.tools.GraphViewWrapper;
import com.vrem.wifianalyzer.wifi.model.SortBy;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.model.WiFiSignal;

import java.util.Set;
import java.util.TreeSet;

class ChannelGraphView implements GraphViewNotifier {
    private static final int CNT_X_SMALL_2 = 16;
    private static final int CNT_X_SMALL_5 = 18;
    private static final int CNT_X_LARGE = 24;
    private static final int THICKNESS_INVISIBLE = 0;

    private final WiFiBand wiFiBand;
    private final Pair<WiFiChannel, WiFiChannel> wiFiChannelPair;
    private GraphViewWrapper graphViewWrapper;

    ChannelGraphView(@NonNull WiFiBand wiFiBand, @NonNull Pair<WiFiChannel, WiFiChannel> wiFiChannelPair) {
        this.wiFiBand = wiFiBand;
        this.wiFiChannelPair = wiFiChannelPair;
        this.graphViewWrapper = makeGraphViewWrapper();
    }

    @Override
    public void update(@NonNull WiFiData wiFiData) {
        Settings settings = MainContext.INSTANCE.getSettings();
        GraphLegend channelGraphLegend = settings.getChannelGraphLegend();
        SortBy sortBy = settings.getSortBy();
        Set<WiFiDetail> newSeries = new TreeSet<>();
        for (WiFiDetail wiFiDetail : wiFiData.getWiFiDetails(wiFiBand, sortBy)) {
            if (isInRange(wiFiDetail.getWiFiSignal().getCenterFrequency(), wiFiChannelPair)) {
                newSeries.add(wiFiDetail);
                addData(wiFiDetail);
            }
        }
        graphViewWrapper.removeSeries(newSeries);
        graphViewWrapper.updateLegend(channelGraphLegend);
        graphViewWrapper.setVisibility(isSelected() ? View.VISIBLE : View.GONE);
    }

    private boolean isInRange(int frequency, Pair<WiFiChannel, WiFiChannel> wiFiChannelPair) {
        return frequency >= wiFiChannelPair.first.getFrequency() && frequency <= wiFiChannelPair.second.getFrequency();
    }

    private boolean isSelected() {
        Settings settings = MainContext.INSTANCE.getSettings();
        WiFiBand wiFiBand = settings.getWiFiBand();
        Configuration configuration = MainContext.INSTANCE.getConfiguration();
        Pair<WiFiChannel, WiFiChannel> wiFiChannelPair = configuration.getWiFiChannelPair();
        return this.wiFiBand.equals(wiFiBand) && (WiFiBand.GHZ2.equals(this.wiFiBand) || this.wiFiChannelPair.equals(wiFiChannelPair));
    }

    private void addData(@NonNull WiFiDetail wiFiDetail) {
        DataPoint[] dataPoints = createDataPoints(wiFiDetail);
        TitleLineGraphSeries<DataPoint> series = new TitleLineGraphSeries<>(dataPoints);
        if (graphViewWrapper.addSeries(wiFiDetail, series, dataPoints)) {
            GraphColor graphColor = graphViewWrapper.getColor();
            series.setColor((int) graphColor.getPrimary());
            series.setBackgroundColor((int) graphColor.getBackground());
        }
    }

    private DataPoint[] createDataPoints(@NonNull WiFiDetail wiFiDetail) {
        WiFiSignal wiFiSignal = wiFiDetail.getWiFiSignal();
        int frequency = frequencyAdjustment(wiFiSignal.getCenterFrequency());
        int frequencyStart = frequencyAdjustment(wiFiSignal.getFrequencyStart());
        int frequencyEnd = frequencyAdjustment(wiFiSignal.getFrequencyEnd());
        int level = wiFiSignal.getLevel();
        return new DataPoint[]{
            new DataPoint(frequencyStart, GraphViewBuilder.MIN_Y),
            new DataPoint(frequencyStart + WiFiChannels.FREQUENCY_SPREAD, level),
            new DataPoint(frequency, level),
            new DataPoint(frequencyEnd - WiFiChannels.FREQUENCY_SPREAD, level),
            new DataPoint(frequencyEnd, GraphViewBuilder.MIN_Y)
        };
    }

    @Override
    public GraphView getGraphView() {
        return graphViewWrapper.getGraphView();
    }

    private int getNumX() {
        int numX = CNT_X_LARGE;
        Configuration configuration = MainContext.INSTANCE.getConfiguration();
        if (!configuration.isLargeScreenLayout()) {
            numX = WiFiBand.GHZ2.equals(wiFiBand) ? CNT_X_SMALL_2 : CNT_X_SMALL_5;
        }
        int channelFirst = wiFiChannelPair.first.getChannel() - WiFiChannels.CHANNEL_OFFSET;
        int channelLast = wiFiChannelPair.second.getChannel() + WiFiChannels.CHANNEL_OFFSET;
        return Math.min(numX, channelLast - channelFirst + 1);
    }

    private GraphView makeGraphView() {
        MainActivity mainActivity = MainContext.INSTANCE.getMainActivity();
        Resources resources = mainActivity.getResources();
        return new GraphViewBuilder(mainActivity, getNumX())
            .setLabelFormatter(new ChannelAxisLabel(wiFiBand, wiFiChannelPair))
            .setVerticalTitle(resources.getString(R.string.graph_axis_y))
            .setHorizontalTitle(resources.getString(R.string.graph_channel_axis_x))
            .build();
    }

    private GraphViewWrapper makeGraphViewWrapper() {
        Settings settings = MainContext.INSTANCE.getSettings();
        graphViewWrapper = new GraphViewWrapper(makeGraphView(), settings.getChannelGraphLegend());

        int frequencyStart = frequencyAdjustment(wiFiChannelPair.first.getFrequency());
        int frequencyEnd = frequencyAdjustment(wiFiChannelPair.second.getFrequency());
        int minX = frequencyStart - WiFiChannels.FREQUENCY_OFFSET;
        int maxX = minX + (graphViewWrapper.getViewportCntX() * WiFiChannels.FREQUENCY_SPREAD);
        graphViewWrapper.setViewport(minX, maxX);

        DataPoint[] dataPoints = new DataPoint[]{
            new DataPoint(minX, GraphViewBuilder.MIN_Y),
            new DataPoint(frequencyEnd + WiFiChannels.FREQUENCY_OFFSET, GraphViewBuilder.MIN_Y)
        };

        TitleLineGraphSeries<DataPoint> series = new TitleLineGraphSeries<>(dataPoints);
        series.setColor((int) GraphColor.TRANSPARENT.getPrimary());
        series.setThickness(THICKNESS_INVISIBLE);
        graphViewWrapper.addSeries(series);
        return graphViewWrapper;
    }

    void setGraphViewWrapper(@NonNull GraphViewWrapper graphViewWrapper) {
        this.graphViewWrapper = graphViewWrapper;
    }

    private int frequencyAdjustment(int frequency) {
        return frequency - (frequency % 5);
    }
}
