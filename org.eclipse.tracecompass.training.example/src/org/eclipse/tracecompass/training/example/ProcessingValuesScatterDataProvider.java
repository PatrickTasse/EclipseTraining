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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.TmfXyResponseFactory;
import org.eclipse.tracecompass.internal.tmf.core.model.tree.AbstractTreeDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.interval.TmfStateInterval;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderParameterUtils;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.SeriesModel;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
import org.eclipse.tracecompass.tmf.core.model.xy.ISeriesModel;
import org.eclipse.tracecompass.tmf.core.model.xy.ITmfTreeXYDataProvider;
import org.eclipse.tracecompass.tmf.core.model.xy.ITmfXyModel;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.tracecompass.tmf.core.presentation.RotatingPaletteProvider;
import org.eclipse.tracecompass.tmf.core.response.TmfModelResponse;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;

public class ProcessingValuesScatterDataProvider extends AbstractTreeDataProvider<ProcessingTimeAnalysis, TmfTreeDataModel> implements ITmfTreeXYDataProvider<TmfTreeDataModel> {

    private static final List<RGBAColor> COLORS = new RotatingPaletteProvider.Builder().setNbColors(4).build().get();

    public ProcessingValuesScatterDataProvider(ITmfTrace trace, ProcessingTimeAnalysis analysisModule) {
        super(trace, analysisModule);
    }

    public static final String ID = "org.eclipse.tracecompass.training.example.dataprovider.processing.values.scatter"; //$NON-NLS-1$

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected boolean isCacheable() {
        return true;
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

        // TODO: Get all the <requester> attributes from the state system
        // TODO: Get an id and create an element for each <requester>
//        for (@NonNull Integer requesterQuark : ss.getQuarks("Requester", "*")) {
//            long requesterId = getId(requesterQuark);
//            String requesterName = ss.getAttributeName(requesterQuark);
//            TmfTreeDataModel requesterEntry = new TmfTreeDataModel(requesterId, traceId, Collections.singletonList(requesterName), false, null);
//            entryList.add(requesterEntry);
            // TODO: Get all the <id> child attributes of each <requester>
            // TODO: Get an id and create an element for each <id>
//            for (@NonNull Integer idQuark : ss.getSubAttributes(requesterQuark, false)) {
//                long idId = getId(idQuark);
//                String idName = ss.getAttributeName(idQuark);
//                String color = COLORS.get(Integer.parseInt(idName) % 4).toString().substring(0, 7);
//                OutputElementStyle style = new OutputElementStyle(null, ImmutableMap.of(
//                        StyleProperties.COLOR, color,
//                        StyleProperties.SERIES_TYPE, StyleProperties.SeriesType.SCATTER,
//                        StyleProperties.SERIES_STYLE, StyleProperties.SeriesStyle.NONE,
//                        StyleProperties.SYMBOL_TYPE, StyleProperties.SymbolType.DIAMOND,
//                        StyleProperties.HEIGHT, 2.0f));
//                TmfTreeDataModel idEntry = new TmfTreeDataModel(idId, requesterId, Collections.singletonList(idName), true, style);
//                entryList.add(idEntry);
//            }
//        }

        // TODO: Return the list of all created elements
//        return new TmfTreeModel<>(Collections.singletonList("Requester"), ImmutableList.copyOf(entryList));
    }

    @Override
    public TmfModelResponse<ITmfXyModel> fetchXY(Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        ITmfStateSystem ss = getAnalysisModule().getStateSystem();
        boolean complete = ss.waitUntilBuilt(0);
        // TODO: Extract the requested timestamps and ids from the parameters
//        List<@NonNull Long> selectedItems = DataProviderParameterUtils.extractSelectedItems(fetchParameters);
//        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);
//        long start = Math.max(ss.getStartTime(), timeRequested.get(0));
//        long end = Math.min(ss.getCurrentEndTime(), Iterables.getLast(timeRequested));
//        if (end < start) {
//            return TmfXyResponseFactory.createEmptyResponse("Out of range");
//        }
        // TODO: Get the ids to quarks map for the requested ids
//        Map<@NonNull Long, @NonNull Integer> idsToQuark = getSelectedEntries(selectedItems);

        // TODO: Compile the list of quarks needed to get value data from state system
//        Collection<@NonNull Integer> quarks = new ArrayList<>();
//        List<@NonNull Integer> seriesQuarks = ss.getQuarks("Requester", "*", "*");
        // TODO: Keep only the selected quarks that belong to a series
//        quarks.addAll(idsToQuark.values());
//        quarks.retainAll(seriesQuarks);
//        for (Integer quark : Lists.newArrayList(quarks)) {
//            int numberQuark = ss.optQuarkRelative(quark, "number");
//            if (numberQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
//                quarks.add(numberQuark);
//            } else {
//                quarks.remove(quark);
//            }
//        }

        // TODO: Do a 2D query of the state system and collect the returned intervals in a tree multimap per quark
//        TreeMultimap<Integer, ITmfStateInterval> intervals = TreeMultimap.create(Comparator.naturalOrder(),
//                Comparator.comparing(ITmfStateInterval::getStartTime));
//        try {
//            for (ITmfStateInterval interval : ss.query2D(quarks, start, end)) {
//                intervals.put(interval.getAttribute(), interval);
//            }
//        } catch (StateSystemDisposedException e) {
//            return TmfXyResponseFactory.createEmptyResponse("State System Disposed");
//        }

//        List<ISeriesModel> yModels = new ArrayList<>();
        // TODO: For each id/quark pair, get the list of intervals
//        for (Entry<@NonNull Long, @NonNull Integer> entry : idsToQuark.entrySet()) {
//            Long entryId = entry.getKey();
//            Integer quark = entry.getValue();
            // TODO: Ignore id/quark pairs that do not have a series
//            if (!seriesQuarks.contains(quark)) {
//                continue;
//            }
//            List<Long> xList = new ArrayList<>();
//            List<Double> yList = new ArrayList<>();
            // TODO: For each interval, create a data point and add to a list
//            for (ITmfStateInterval interval : intervals.get(quark)) {
//                if (Integer.valueOf(IEventConstants.ProcessingStates.PROCESSING.ordinal()).equals(interval.getValue())) {
//                    int numberQuark = ss.optQuarkRelative(quark, "number");
//                    ITmfStateInterval numberInterval = getIntervalAt(intervals.get(numberQuark), interval.getStartTime());
//                    xList.add(interval.getStartTime());
//                    yList.add((double) numberInterval.getValueLong());
//                }
//            }
            // TODO: Convert the list to array and create a series model
//            long[] xValues = xList.stream().mapToLong(Long::longValue).toArray();
//            double[] yValues = yList.stream().mapToDouble(Double::doubleValue).toArray();
//            yModels.add(new SeriesModel(entryId, ss.getAttributeName(quark), xValues, yValues));
//        }
        // TODO: Return the list of all created series models
//        return TmfXyResponseFactory.create("Processing Values", yModels, complete);
    }

    private static @Nullable ITmfStateInterval getIntervalAt(NavigableSet<ITmfStateInterval> intervals, long time) {
        ITmfStateInterval interval = intervals.floor(new TmfStateInterval(time, time, -1, null));
        if (interval != null && interval.intersects(time)) {
            return interval;
        }
        return null;
    }
}
