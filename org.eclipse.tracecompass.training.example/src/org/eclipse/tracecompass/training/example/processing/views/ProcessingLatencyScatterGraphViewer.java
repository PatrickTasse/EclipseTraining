/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.training.example.processing.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.scatter.AbstractSegmentStoreScatterChartViewer2;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfXYChartSettings;
import org.eclipse.tracecompass.training.example.processing.ProcessingLatencyModule;

/**
 * A scatter viewer (X/Y chart) showing processing latencies.
 */
public class ProcessingLatencyScatterGraphViewer extends AbstractSegmentStoreScatterChartViewer2 {

    public ProcessingLatencyScatterGraphViewer(Composite parent, String title, String xLabel, String yLabel) {
        super(parent, new TmfXYChartSettings(title, xLabel, yLabel, 1), ProcessingLatencyModule.ID);
    }

}
