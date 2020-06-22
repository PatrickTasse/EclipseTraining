/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

public class ProcessingTimeStateProvider extends AbstractTmfStateProvider {

    public ProcessingTimeStateProvider(ITmfTrace trace) {
        super(trace, "org.eclipse.tracecompass.processing.time.state.provider");
    }

    @Override
    public int getVersion() {
        return 6;
    }

    @Override
    public ITmfStateProvider getNewInstance() {
        return new ProcessingTimeStateProvider(this.getTrace());
    }

    @Override
    protected void eventHandle(ITmfEvent event) {
        final ITmfStateSystemBuilder stateSystem = getStateSystemBuilder();
        if (stateSystem == null){
            return;
        }
        /**
         * Attribute tree:
         * --------------
         * Receiver
         *     |
         *     |-<receiver> -> Requester Name
         * Requester
         *     |
         *     |-<requester> -> State Value
         *            |---<id> -> State Value
         *                 |---number -> Number Value
         *
         * Requester Name:
         * -----------
         * Store name of <requester>
         *
         * State Value:
         * -----------
         * Use enum Processing state in {@link IEventConstants}
         *
         * Number Value:
         * ------------
         * Store optional event field "value"
         *
         */
        switch (event.getName()) {
        // TODO add case for ball request and reply and change state of attribute Receiver/<receiver>
        case IEventConstants.CREATE_EVENT: {
            Integer stateValue = IEventConstants.ProcessingStates.INITIALIZING.ordinal();
            updateRequesterState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.START_EVENT: {
            Integer stateValue = IEventConstants.ProcessingStates.PROCESSING.ordinal();
            updateRequesterState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.STOP_EVENT: {
            Integer stateValue = IEventConstants.ProcessingStates.WAITING.ordinal();
            updateRequesterState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.END_EVENT: {
            Object stateValue = null;
            updateRequesterState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.PROCESS_INIT_EVENT: {
            Object stateValue = IEventConstants.ProcessingStates.INITIALIZING.ordinal();
            updateProcessingState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.PROCESS_START_EVENT: {
            Object stateValue = IEventConstants.ProcessingStates.PROCESSING.ordinal();
            updateProcessingState(stateSystem, event, stateValue);
            return;
        }

        case IEventConstants.PROCESS_END_EVENT: {
            Object stateValue = null;
            updateProcessingState(stateSystem, event, stateValue);
            break;
        }

        default:
            return;
        }
    }

    private static void updateRequesterState(ITmfStateSystemBuilder stateSystem, ITmfEvent event, Object stateValue) {
        // get event field with name
        String requester = event.getContent().getFieldValue(String.class, "requester");
        if (requester == null) {
            return;
        }

        // get quark of attribute for path Requester/requester
        int quark = stateSystem.getQuarkAbsoluteAndAdd("Requester", requester);

        // get time of event
        long t = event.getTimestamp().getValue();

        // apply state change
        stateSystem.modifyAttribute(t, stateValue, quark);
        return;
    }

    private static void updateProcessingState(ITmfStateSystemBuilder stateSystem, ITmfEvent event, Object stateValue) {
        // get event field with name
        String requester = event.getContent().getFieldValue(String.class, "requester");
        if (requester == null) {
            return;
        }

        Long id = event.getContent().getFieldValue(Long.class, "id");
        if (id == null) {
            return;
        }

        // get quark of attribute for path Requester/requester
        int quark = stateSystem.getQuarkAbsoluteAndAdd("Requester", requester, String.valueOf(id));

        // get time of event
        long t = event.getTimestamp().getValue();

        // apply state change
        stateSystem.modifyAttribute(t, stateValue, quark);

        // Add optional attribute to store the number (using optional event field "value")
        Long number = event.getContent().getFieldValue(Long.class, "value");
        if (number == null) {
            return;
        }
        quark = stateSystem.getQuarkAbsoluteAndAdd("Requester", requester, String.valueOf(id), "number");
        stateSystem.modifyAttribute(t, number, quark);

        return;
    }
}
