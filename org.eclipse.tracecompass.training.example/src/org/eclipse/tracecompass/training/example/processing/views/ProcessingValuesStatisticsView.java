/*******************************************************************************
 * Copyright (c) 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example.processing.views;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderParameterUtils;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfWindowRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.AbstractSelectTreeViewer2;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeColumnDataProvider;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeColumnData;
import org.eclipse.tracecompass.tmf.ui.views.TmfView;
import org.eclipse.tracecompass.training.example.ProcessingValuesStatisticsDataProvider;

import com.google.common.collect.ImmutableList;

public class ProcessingValuesStatisticsView extends TmfView {

    public static final String ID = "org.eclipse.tracecompass.training.example.processing.values.statistics";

    private AbstractSelectTreeViewer2 fTreeViewer;

    public ProcessingValuesStatisticsView() {
        // TODO: Call super with view ID
//        super(ID);
    }

    // TODO: Override createPartControl() to create an AbstractSelectTreeViewer2
//    @Override
//    public void createPartControl(Composite parent) {
//        fTreeViewer = new AbstractSelectTreeViewer2(parent, -1, ProcessingValuesStatisticsDataProvider.ID) {
            // TODO: Override getColumnDataProvider() to provide tree column data
//            @Override
//            protected ITmfTreeColumnDataProvider getColumnDataProvider() {
//                return () -> ImmutableList.of(
//                        new TmfTreeColumnData("Challenger"),
//                        new TmfTreeColumnData("Count"),
//                        new TmfTreeColumnData("Minimum"),
//                        new TmfTreeColumnData("Maximum"),
//                        new TmfTreeColumnData("Average"));
//            }
//
            // TODO: Override setSelectionRange() to update tree
//            @Override
//            protected void setSelectionRange(long selectionBeginTime, long selectionEndTime) {
//                super.setSelectionRange(selectionBeginTime, selectionEndTime);
//                updateContent(selectionBeginTime, selectionEndTime, true);
//            }
//
            // TODO: Override windowRangeUpdated() to ignore window change
//            @Override
//            public void windowRangeUpdated(TmfWindowRangeUpdatedSignal signal) {
//                // Do not update tree viewer
//            }
//
            // TODO: Override getParameters() to provide selection boolean
//            @Override
//            protected @NonNull Map<String, Object> getParameters(long start, long end, boolean isSelection) {
//                Map<String, Object> parameters = super.getParameters(start, end, isSelection);
//                parameters.put(DataProviderParameterUtils.FILTERED_KEY, isSelection);
//                return parameters;
//            }
//        };
        // TODO: Call traceSelected() at creation with active trace
//        ITmfTrace trace = TmfTraceManager.getInstance().getActiveTrace();
//        if (trace != null) {
//            TmfTraceSelectedSignal signal = new TmfTraceSelectedSignal(this, trace);
//            fTreeViewer.traceSelected(signal);
//        }
//    }

    @Override
    public void dispose() {
        super.dispose();
        if (fTreeViewer != null) {
            fTreeViewer.dispose();
        }
    }

    @Override
    public void setFocus() {
        fTreeViewer.getControl().setFocus();
    }
}
