/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.training.example.processing.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.ISegmentStoreProvider;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.scatter.AbstractSegmentStoreScatterGraphViewer;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;
import org.eclipse.tracecompass.training.example.processing.ProcessingLatencyModule;

/**
 * A scatter viewer (X/Y chart) showing processing latencies.
 */
public class ProcessingLatencyScatterGraphViewer extends AbstractSegmentStoreScatterGraphViewer {

    public ProcessingLatencyScatterGraphViewer(Composite parent, String title, String xLabel, String yLabel) {
        super(parent, title, xLabel, yLabel);
    }

    @Override
    protected ISegmentStoreProvider getSegmentStoreProvider(ITmfTrace trace) {
        return TmfTraceUtils.getAnalysisModuleOfClass(trace, ProcessingLatencyModule.class, ProcessingLatencyModule.ID);
    }
}