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

package com.vrem.wifianalyzer.wifi.graph.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphViewBuilderTest {
    private static final String HORIZONTAL_TITLE = "horizontalTitle";
    private static final String VERTICAL_TITLE = "verticalTitle";
    private static final int NUM_HORIZONTAL_LABELS = 5;

    @Mock
    private GridLabelRenderer gridLabelRenderer;
    @Mock
    private Context content;
    @Mock
    private GraphView graphView;
    @Mock
    private Viewport viewport;
    @Mock
    private LabelFormatter labelFormatter;

    private GraphViewBuilder fixture;

    @Before
    public void setUp() {
        fixture = new GraphViewBuilder(content, NUM_HORIZONTAL_LABELS);
    }

    @Test
    public void testSetGraphView() throws Exception {
        // setup
        ViewGroup.LayoutParams layoutParams = fixture.getLayoutParams();
        // execute
        fixture.setGraphView(graphView);
        // validate
        verify(graphView).setLayoutParams(layoutParams);
        verify(graphView).setVisibility(View.GONE);
    }

    @Test
    public void testSetViewPortY() throws Exception {
        // setup
        when(graphView.getViewport()).thenReturn(viewport);
        // execute
        fixture.setViewPortY(graphView);
        // validate
        verify(graphView).getViewport();
        verify(viewport).setScrollable(true);
        verify(viewport).setYAxisBoundsManual(true);
        verify(viewport).setMinY(GraphViewBuilder.MIN_Y);
        verify(viewport).setMaxY(GraphViewBuilder.MAX_Y);
        verify(viewport).setXAxisBoundsManual(true);
    }

    @Test
    public void testSetGridLabelRenderer() throws Exception {
        // setup
        fixture.setLabelFormatter(labelFormatter);
        fixture.setHorizontalTitle(HORIZONTAL_TITLE);
        fixture.setVerticalTitle(VERTICAL_TITLE);
        when(graphView.getGridLabelRenderer()).thenReturn(gridLabelRenderer);
        // execute
        fixture.setGridLabelRenderer(graphView);
        // validate
        verify(graphView).getGridLabelRenderer();
        verify(gridLabelRenderer).setHighlightZeroLines(false);
        verify(gridLabelRenderer).setNumVerticalLabels(GraphViewBuilder.NUM_Y);
        verify(gridLabelRenderer).setNumHorizontalLabels(NUM_HORIZONTAL_LABELS);
        verify(gridLabelRenderer).setLabelFormatter(labelFormatter);
        verify(gridLabelRenderer).setVerticalAxisTitle(VERTICAL_TITLE);
        verify(gridLabelRenderer).setVerticalLabelsVisible(true);
        verify(gridLabelRenderer).setHorizontalAxisTitle(HORIZONTAL_TITLE);
        verify(gridLabelRenderer).setHorizontalLabelsVisible(true);
    }

    @Test
    public void testSetGridLabelRendererWithoutTitles() throws Exception {
        // setup
        when(graphView.getGridLabelRenderer()).thenReturn(gridLabelRenderer);
        // execute
        fixture.setGridLabelRenderer(graphView);
        // validate
        verify(graphView).getGridLabelRenderer();
        verify(gridLabelRenderer).setHighlightZeroLines(false);
        verify(gridLabelRenderer).setNumVerticalLabels(GraphViewBuilder.NUM_Y);
        verify(gridLabelRenderer).setNumHorizontalLabels(NUM_HORIZONTAL_LABELS);
        verify(gridLabelRenderer, never()).setLabelFormatter(labelFormatter);
        verify(gridLabelRenderer, never()).setVerticalAxisTitle(VERTICAL_TITLE);
        verify(gridLabelRenderer).setVerticalLabelsVisible(false);
        verify(gridLabelRenderer, never()).setHorizontalAxisTitle(HORIZONTAL_TITLE);
        verify(gridLabelRenderer).setHorizontalLabelsVisible(false);
    }
}