/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.training.example.processing.views;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.scatter.AbstractSegmentStoreScatterChartTreeViewer2;
import org.eclipse.tracecompass.tmf.ui.viewers.TmfViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.TmfXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.views.xychart.TmfChartView;
import org.eclipse.tracecompass.training.example.processing.ProcessingLatencyModule;

/**
 * A scatter view (X/Y chart) showing processing latencies.
 */
public class ProcessingLatencyScatterView extends TmfChartView {

    private static final String VIEW_ID = "org.eclipse.tracecompass.training.processing.scatter";

    public ProcessingLatencyScatterView() {
        super(VIEW_ID);
    }

    @Override
    protected TmfXYChartViewer createChartViewer(Composite parent) {
        final String title = "Duration vs Time";
        final String xLabel = "Time axis";
        final String yLabel = "Duration";
        return new ProcessingLatencyScatterGraphViewer(parent, title, xLabel, yLabel);
    }

    @Override
    protected @NonNull TmfViewer createLeftChildViewer(@Nullable Composite parent) {
        return new AbstractSegmentStoreScatterChartTreeViewer2(Objects.requireNonNull(parent), ProcessingLatencyModule.ID);
    }
}
