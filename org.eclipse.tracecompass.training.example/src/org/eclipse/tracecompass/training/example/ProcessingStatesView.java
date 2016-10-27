/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.StateSystemUtils;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.tmf.core.signal.TmfSelectionRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.eclipse.tracecompass.tmf.ui.views.TmfView;

public class ProcessingStatesView extends TmfView {
    private Text fTextArea;

    /** The selected trace */
    private ITmfTrace fTrace;

    public ProcessingStatesView() {
        super("ProvessingStates");
    }

    @Override
    public void createPartControl(Composite parent) {
        // Create an inner composite
        Composite viewComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        viewComposite.setLayout(layout);

        // Create a text area
        fTextArea = new Text(viewComposite, SWT.BORDER | SWT.MULTI  | SWT.WRAP | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        fTextArea.setLayoutData(gridData);
        fTextArea.setEditable(false);

        // Select trace if it is already open when opening the view
        ITmfTrace trace = TmfTraceManager.getInstance().getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
    }

    @Override
    public void setFocus() {
        fTextArea.setFocus();
    }

    @TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        if (signal.getTrace() == fTrace) {
            return;
        }
        fTrace = signal.getTrace();
    }

    @TmfSignalHandler
    public void selectionRangeUpdated(final TmfSelectionRangeUpdatedSignal signal) {
        if (signal.getSource() == this || fTrace == null) {
            return;
        }

        final long beginTime = signal.getBeginTime().toNanos();
        final long endTime = signal.getEndTime().toNanos();

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                /*
                 * Signal might be sent from non-UI thread
                 */
                printStates(beginTime, endTime);
            }
        });
    }

    private void printStates(long startTime, long endTime) {
        ITmfTrace trace = fTrace;
        if (trace == null) {
            return;
        }

        // get relevant state system
        final ITmfStateSystem ssq = TmfStateSystemAnalysisModule.getStateSystem(trace, ProcessingTimeAnalysis.ID);
        if (ssq == null) {
            return;
        }

        // make sure that it is fully build
        ssq.waitUntilBuilt();
        outputText("startTime=" + startTime + ", endTime=" + endTime);

        /*
         * 1) get the quarks of all children of attribute "Requester".
         *    Print the quark and attribute name to screen
         */
        List<Integer> requesterQuarks = ssq.getQuarks("Requester", "*");
        for (Integer requesterQuark : requesterQuarks) {
            String requesterName = ssq.getAttributeName(requesterQuark);
            outputText("Exercise 1");
            outputText("attribute=" + requesterName + ", quark=" + requesterQuark);
            outputText("--------------------------------------");
            try {
                /*
                 * 2) Get ITmfStateInterval of attribute Requester/<requester> at startTime (single query)
                 *    Print value of ITmfStateValue
                 *    Question: what data type is the state value?
                 */
                outputText("Exercise 2");
                ITmfStateInterval interval = ssq.querySingleState(startTime, requesterQuark);
                ITmfStateValue state = interval.getStateValue();
                outputText("state at start=" + state.toString());
                outputText("--------------------------------------");
                /*
                 *  3) Print ITmfStateValue of attribute Requester/<requester> at endTime
                 */
                outputText("Exercise 3");
                interval = ssq.querySingleState(endTime, requesterQuark);
                state = interval.getStateValue();
                outputText("state at end=" + state.toString());
                outputText("--------------------------------------");
                /*
                 * 4) Query history range using utility class StateSystemUtils and
                 *     print each ITmfStateInterval (state value, start time, end time and duration)
                 */
                outputText("Exercise 4");
                List<ITmfStateInterval> intervals = StateSystemUtils.queryHistoryRange(ssq, requesterQuark, startTime, endTime);
                for (ITmfStateInterval inter : intervals) {
                    outputText("interval" + inter.toString());
                }
                outputText("--------------------------------------");
                /*
                 * 5) Do a full query at end time and print each attribute, path and state interval
                 */
                outputText("Exercise 5");

                intervals = ssq.queryFullState(endTime);
                for (ITmfStateInterval inter : intervals) {
                    outputText("interval" + inter.toString() +
                            ", \tattributeName=" + ssq.getAttributeName(inter.getAttribute()) +
                            ", \tattributePath=" + ssq.getFullAttributePath(inter.getAttribute()));

                }
                outputText("--------------------------------------");
                /*
                 * 6) Bonus: do step 4 with all attribute with path Requester/<requester>/<id>
                 */
                outputText("Exercise 6");
                List<Integer> idQuarks = ssq.getQuarks(requesterQuark, "*");
                for (Integer idQuark : idQuarks) {
                    intervals = StateSystemUtils.queryHistoryRange(ssq, idQuark, startTime, endTime);
                    for (ITmfStateInterval inter : intervals) {
                        outputText("interval" + inter.toString());
                    }
                }

            } catch (StateSystemDisposedException | AttributeNotFoundException e) {
                e.printStackTrace();
            }
        }

        outputText("=========================================");
    }
    private void outputText(String text) {
        fTextArea.append(text);
        fTextArea.append(System.getProperty("line.separator"));
    }
}
