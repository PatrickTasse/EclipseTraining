/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.training.example.processing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.tracecompass.analysis.timing.core.segmentstore.AbstractSegmentStoreAnalysisEventBasedModule;
import org.eclipse.tracecompass.segmentstore.core.ISegment;
import org.eclipse.tracecompass.segmentstore.core.ISegmentStore;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.segment.ISegmentAspect;
import org.eclipse.tracecompass.tmf.core.util.Pair;
import org.eclipse.tracecompass.training.example.IEventConstants;

/**
 * A module that builds segments representing the beginning and end of processing.
 */
public class ProcessingLatencyModule extends AbstractSegmentStoreAnalysisEventBasedModule {
    /**
     * The ID of this analysis
     */
    public static final String ID = "org.eclipse.tracecompass.training.processing.module";

    private static final Collection<ISegmentAspect> PROCESSING_ASPECTS = new ArrayList<>();
    static {
        PROCESSING_ASPECTS.add(ProcessingNameAspect.INSTANCE);
        PROCESSING_ASPECTS.add(ProcessingContentAspect.INSTANCE);
    }
    private final Map<ProcessingInfoKey, ProcessingInitialInfo> fOngoingProcessingSegments = new HashMap<>();

    public static class ProcessingInfoKey extends Pair<String, String>{
        public ProcessingInfoKey(ITmfEvent event) {
            super(event.getContent().getField("requester").getFormattedValue(), event.getContent().getField("id").getFormattedValue());
        }
    }

    private static class ProcessingInitialInfo {
        public ProcessingInitialInfo(ITmfEvent event) {
            super();
            this.fStart = event.getTimestamp().getValue();
        }
        private long fStart;
    }

    @Override
    protected AbstractSegmentStoreAnalysisRequest createAnalysisRequest(ISegmentStore<ISegment> segmentStore) {
        return new AbstractSegmentStoreAnalysisRequest(segmentStore) {
            @Override
            public void handleData(ITmfEvent event) {
                super.handleData(event);
                processEvent(event, segmentStore);
            }
        };
    }

    private void processEvent(ITmfEvent event, ISegmentStore<ISegment> segmentStore) {
        if (event.getName().equals(IEventConstants.PROCESS_START_EVENT)) {
            fOngoingProcessingSegments.put(new ProcessingInfoKey(event), new ProcessingInitialInfo(event));
        } else if (event.getName().equals(IEventConstants.PROCESS_END_EVENT)) {
            ProcessingInfoKey key = new ProcessingInfoKey(event);
            ProcessingInitialInfo processingInfo = fOngoingProcessingSegments.get(key);
            if (processingInfo != null) {
                long endTime = event.getTimestamp().getValue();
                segmentStore.add(new ProcessingSegment(processingInfo.fStart, endTime, "PROCESSING", key.getSecond(), key.getFirst()));
            }
        }
    }

    @Override
    protected Object[] readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        return (Object[]) ois.readObject();
    }

    @Override
    public Iterable<ISegmentAspect> getSegmentAspects() {
        return PROCESSING_ASPECTS;
    }
}
