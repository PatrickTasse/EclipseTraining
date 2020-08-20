/*******************************************************************************
 * Copyright (c) 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example.processing.views;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.tmf.ui.viewers.TmfViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.AbstractSelectTreeViewer2;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeColumnDataProvider;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeColumnData;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.TmfXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfFilteredXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfXYChartSettings;
import org.eclipse.tracecompass.tmf.ui.views.xychart.TmfChartView;
import org.eclipse.tracecompass.training.example.ProcessingValuesLineDataProvider;

import com.google.common.collect.ImmutableList;

public class ProcessingValuesLineView extends TmfChartView {

    public static final String ID = "org.eclipse.tracecompass.training.example.processing.values.line";

    public ProcessingValuesLineView() {
        // TODO: Call super with view ID
//        super(ID);
    }

    // TODO: Override createLeftChildViewer() to return an AbstractSelectTreeViewer2
//    @Override
//    protected @NonNull TmfViewer createLeftChildViewer(Composite parent) {
//        return new AbstractSelectTreeViewer2(parent, 1, ProcessingValuesLineDataProvider.ID) {
//            @Override
//            protected ITmfTreeColumnDataProvider getColumnDataProvider() {
//                return () -> ImmutableList.of(new TmfTreeColumnData("Challenger"), new TmfTreeColumnData("Legend"));
//            }
//        };
//    }

    // TODO: Override createChartViewer() to return a TmfFilteredXYChartViewer
//    @Override
//    protected TmfXYChartViewer createChartViewer(Composite parent) {
//        TmfXYChartSettings settings = new TmfXYChartSettings("Processing Values", "Time", "Value", 0.2);
//        return new TmfFilteredXYChartViewer(parent, settings, ProcessingValuesLineDataProvider.ID);
//    }
}
