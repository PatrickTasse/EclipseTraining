/*******************************************************************************
 * Copyright (c) 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.timing.core.statistics.IStatistics;
import org.eclipse.tracecompass.internal.tmf.core.model.tree.AbstractTreeDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.interval.TmfStateInterval;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderParameterUtils;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class ProcessingValuesStatisticsDataProvider extends AbstractTreeDataProvider<ProcessingTimeAnalysis, TmfTreeDataModel> {

    public ProcessingValuesStatisticsDataProvider(ITmfTrace trace, ProcessingTimeAnalysis analysisModule) {
        super(trace, analysisModule);
    }

    public static final String ID = "org.eclipse.tracecompass.training.example.dataprovider.processing.values.statistics"; //$NON-NLS-1$

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected boolean isCacheable() {
        return false;
    }

    @Override
    protected TmfTreeModel<TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        long startTime = ss.getStartTime();
        long endTime = ss.getCurrentEndTime();
        List<@NonNull TmfTreeDataModel> entryList = new ArrayList<>();

        // TODO: Create a root element for the trace
//        long traceId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
//        TmfTreeDataModel traceEntry = new TmfTreeDataModel(traceId, -1, Collections.singletonList(getTrace().getName()), false, null);
//        entryList.add(traceEntry);
        // TODO: Add the Total sub-tree
        // TODO: Get the total statistics per type
//        Map<String, IStatistics<@NonNull ITmfStateInterval>> statsMap = getAnalysisModule().getStatsPerType();
        // TODO: Add the Total aggregated element
//        long totalId = getEntryId("Total");
//        TmfTreeDataModel totalEntry = new TmfTreeDataModel(totalId, traceId, getLabels("Total", ProcessingTimeAnalysis.TOTAL, statsMap), false, null);
//        entryList.add(totalEntry);
        // TODO: Get all the <requester> attributes from the state system
        // TODO: Get an id and create an aggregated element for each <requester>
//        for (@NonNull Integer requesterQuark : ss.getQuarks("Requester", "*")) {
//            long requesterId = getId(requesterQuark);
//            String requesterName = ss.getAttributeName(requesterQuark);
//            TmfTreeDataModel requesterEntry = new TmfTreeDataModel(requesterId, totalId, getLabels(requesterName, requesterName, statsMap), false, null);
//            entryList.add(requesterEntry);
            // TODO: Get all the <id> child attributes of each <requester>
            // TODO: Get an id and create an element for each <id>
//            for (@NonNull Integer idQuark : ss.getSubAttributes(requesterQuark, false)) {
//                long idId = getId(idQuark);
//                String idName = ss.getAttributeName(idQuark);
//                String key = requesterName + ProcessingTimeAnalysis.SEP + idName;
//                TmfTreeDataModel idEntry = new TmfTreeDataModel(idId, requesterId, getLabels(idName, key, statsMap), false, null);
//                entryList.add(idEntry);
//            }
//        }
        // TODO: Add the Selection sub-tree if isFiltered is true
//        Boolean isFiltered = DataProviderParameterUtils.extractIsFiltered(fetchParameters);
//        if (Boolean.TRUE.equals(isFiltered)) {
            // TODO: Get the range statistics per type
//            List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);
//            long start = Math.max(ss.getStartTime(), timeRequested.get(0));
//            long end = Math.min(ss.getCurrentEndTime(), Iterables.getLast(timeRequested));
//            Map<String, IStatistics<@NonNull ITmfStateInterval>> rangeStatsMap = getAnalysisModule().getStatsPerTypeForRange(start, end, monitor);
            // TODO: Add the Selection aggregated element
//            long selectionId = getEntryId("Selection");
//            TmfTreeDataModel selectionEntry = new TmfTreeDataModel(selectionId, traceId, getLabels("Selection", ProcessingTimeAnalysis.TOTAL, rangeStatsMap), false, null);
//            entryList.add(selectionEntry);
            // TODO: Get all the <requester> attributes from the state system
            // TODO: Get an id and create an aggregated element for each <requester>
//            for (@NonNull Integer requesterQuark : ss.getQuarks("Requester", "*")) {
//                long requesterId = getId(requesterQuark);
//                String requesterName = ss.getAttributeName(requesterQuark);
//                TmfTreeDataModel requesterEntry = new TmfTreeDataModel(requesterId, selectionId, getLabels(requesterName, requesterName, rangeStatsMap), false, null);
//                entryList.add(requesterEntry);
                // TODO: Get all the <id> child attributes of each <requester>
                // TODO: Get an id and create an element for each <id>
//                for (@NonNull Integer idQuark : ss.getSubAttributes(requesterQuark, false)) {
//                    long idId = getId(idQuark);
//                    String idName = ss.getAttributeName(idQuark);
//                    String key = requesterName + ProcessingTimeAnalysis.SEP + idName;
//                    TmfTreeDataModel idEntry = new TmfTreeDataModel(idId, requesterId, getLabels(idName, key, rangeStatsMap), false, null);
//                    entryList.add(idEntry);
//                }
//            }
//        }

        // TODO: Return the list of all created elements
//        return new TmfTreeModel<>(ImmutableList.of("Challenger", "Count", "Minimum", "Maximum", "Average"), ImmutableList.copyOf(entryList));
    }

    private static List<String> getLabels(String name, String key, Map<String, IStatistics<@NonNull ITmfStateInterval>> statsMap) {
        if (statsMap == null) {
            return Arrays.asList(name);
        }
        IStatistics<@NonNull ITmfStateInterval> stats = statsMap.get(key);
        if (stats == null) {
            return Arrays.asList(name);
        }
        return Arrays.asList(
                name,
                String.valueOf(stats.getNbElements()),
                stats.getNbElements() == 0 ? "" : String.valueOf(stats.getMin()),
                stats.getNbElements() == 0 ? "" : String.valueOf(stats.getMax()),
                stats.getNbElements() == 0 ? "" : String.format("%.3f", stats.getMean()));
    }

    private static @Nullable ITmfStateInterval getIntervalAt(NavigableSet<ITmfStateInterval> intervals, long time) {
        ITmfStateInterval interval = intervals.floor(new TmfStateInterval(time, time, -1, null));
        if (interval != null && interval.intersects(time)) {
            return interval;
        }
        return null;
    }
}
