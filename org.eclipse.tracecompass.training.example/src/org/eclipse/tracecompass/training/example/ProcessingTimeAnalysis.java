/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.timing.core.statistics.IStatistics;
import org.eclipse.tracecompass.analysis.timing.core.statistics.IStatisticsAnalysis;
import org.eclipse.tracecompass.analysis.timing.core.statistics.Statistics;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.tmf.core.analysis.requirements.TmfAbstractAnalysisRequirement;
import org.eclipse.tracecompass.tmf.core.analysis.requirements.TmfAbstractAnalysisRequirement.PriorityLevel;
import org.eclipse.tracecompass.tmf.core.analysis.requirements.TmfAnalysisEventRequirement;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableSet;

public class ProcessingTimeAnalysis extends TmfStateSystemAnalysisModule implements IStatisticsAnalysis<ITmfStateInterval> {

    public static final String TOTAL = "*";

    public static final String SEP = "/";

    /** The analysi module ID (has to match the one in the plugin.xml) */
    public static final String ID = "org.eclipse.tracecompass.training.example.processing.module";

    /** The analysis's requirements. Only set after the trace is set. */
    private Set<TmfAbstractAnalysisRequirement> fAnalysisRequirements;


    public ProcessingTimeAnalysis() {
    }

    @Override
    protected void canceling() {
    }

    @Override
    public Iterable<TmfAbstractAnalysisRequirement> getAnalysisRequirements() {
        Set<TmfAbstractAnalysisRequirement> requirements = fAnalysisRequirements;
        if (requirements == null) {
            Set<String> requiredEvents = ImmutableSet.of(
                    IEventConstants.CREATE_EVENT,
                    IEventConstants.START_EVENT,
                    IEventConstants.STOP_EVENT,
                    IEventConstants.END_EVENT,
                    IEventConstants.PROCESS_INIT_EVENT,
                    IEventConstants.PROCESS_START_EVENT,
                    IEventConstants.PROCESS_END_EVENT
                    );
            /* Initialize the requirements for the analysis: events */
            TmfAbstractAnalysisRequirement eventsReq = new TmfAnalysisEventRequirement(requiredEvents, PriorityLevel.MANDATORY);
            requirements = ImmutableSet.of(eventsReq);
            fAnalysisRequirements = requirements;
        }
        return requirements;
    }

    @Override
    protected ITmfStateProvider createStateProvider() {
        ITmfTrace trace = getTrace();
        if (trace == null) {
            throw new IllegalStateException();
        }
        return new ProcessingTimeStateProvider(trace);
    }

    @Override
    public Map<@NonNull String, IStatistics<@NonNull ITmfStateInterval>> getStatsPerTypeForRange(long start, long end, IProgressMonitor monitor) {
        ITmfStateSystem ss = getStateSystem();
        if (ss == null || start > end) {
            return null;
        }
        ss.waitUntilBuilt();
        int requestersQuark = ss.optQuarkAbsolute("Requester");
        if (requestersQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
            return null;
        }
        // Create a map of statistics models per type
        Map<@NonNull String, IStatistics<@NonNull ITmfStateInterval>> stats = new HashMap<>();
        // Create an aggregated statistics model for total stats
        IStatistics<ITmfStateInterval> totalStats = new Statistics<>(ITmfStateInterval::getValueLong);
        // Create an aggregated statistics model for each requester
        for (int requesterQuark : ss.getSubAttributes(requestersQuark, false)) {
            String requester = ss.getAttributeName(requesterQuark);
            IStatistics<ITmfStateInterval> requesterStats = new Statistics<>(ITmfStateInterval::getValueLong);
            // Create a statistics model for each requester Id
            for (int idQuark : ss.getSubAttributes(requesterQuark, false)) {
                String id = ss.getAttributeName(idQuark);
                int numberQuark = ss.optQuarkRelative(idQuark, "number");
                if (numberQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
                    continue;
                }
                IStatistics<ITmfStateInterval> idStats = new Statistics<>(ITmfStateInterval::getValueLong);
                try {
                    // Query the state intervals of the requester Id for the time range
                    for (ITmfStateInterval interval : ss.query2D(Arrays.asList(idQuark), start, end)) {
                        // If the state is PROCESSING get the number interval at the start time
                        if (Integer.valueOf(IEventConstants.ProcessingStates.PROCESSING.ordinal()).equals(interval.getValue())
                                && interval.getStartTime() >= start && interval.getStartTime() <= end) {
                            ITmfStateInterval numberInterval = ss.querySingleState(interval.getStartTime(), numberQuark);
                            // Update the requester Id statistics model with the number value
                            idStats.update(numberInterval);
                        }
                    }
                } catch (StateSystemDisposedException e) {
                    return null;
                }
                // Add the requester Id stats to the map for type "requester/id"
                stats.put(requester + SEP + id, idStats);
                // Merge the requester Id stats to the aggregate requester statistics model
                requesterStats.merge(idStats);
            }
            // Add the requester stats to the map with for type "requester"
            stats.put(requester, requesterStats);
            // Merge the requester stats to the aggregate total statistics model
            totalStats.merge(requesterStats);
        }
        // Add the total stats to the map with for type "*"
        stats.put(TOTAL, totalStats);
        // Return the map of statistics models
        return stats;
    }

    @Override
    public @Nullable IStatistics<@NonNull ITmfStateInterval> getStatsForRange(long start, long end, IProgressMonitor monitor) {
        // Get the stats per type for range and return the model for type "*"
        return getStatsPerTypeForRange(start, end, monitor).get(TOTAL);
    }

    @Override
    public Map<String, IStatistics<@NonNull ITmfStateInterval>> getStatsPerType() {
        // Get the stats per type for the range equal to the full range of state system
        ITmfStateSystem ss = getStateSystem();
        if (ss == null) {
            return null;
        }
        return getStatsPerTypeForRange(ss.getStartTime(), ss.getCurrentEndTime(), new NullProgressMonitor());
    }

    @Override
    public @Nullable IStatistics<@NonNull ITmfStateInterval> getStatsTotal() {
        // Get the total stats per type and return the model for type "*"
        return getStatsPerType().get(TOTAL);
    }
}
