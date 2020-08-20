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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.xy.AbstractTreeCommonXDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.interval.TmfStateInterval;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderParameterUtils;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.core.model.YModel;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
import org.eclipse.tracecompass.tmf.core.model.xy.IYModel;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.tracecompass.tmf.core.presentation.RotatingPaletteProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;

public class ProcessingValuesLineDataProvider extends AbstractTreeCommonXDataProvider<ProcessingTimeAnalysis, TmfTreeDataModel> {

    private static final List<RGBAColor> COLORS = new RotatingPaletteProvider.Builder().setNbColors(4).build().get();

    public ProcessingValuesLineDataProvider(ITmfTrace trace, ProcessingTimeAnalysis analysisModule) {
        super(trace, analysisModule);
    }

    public static final String ID = "org.eclipse.tracecompass.training.example.dataprovider.processing.values.line"; //$NON-NLS-1$

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTitle() {
        return "Processing Values View";
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

        // Create a root element for the trace
        long traceId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        TmfTreeDataModel traceEntry = new TmfTreeDataModel(traceId, -1, Collections.singletonList(getTrace().getName()), false, null);
        entryList.add(traceEntry);

        // Get all the <requester> attributes from the state system
        // Get an id and create an element for each <requester>
        for (@NonNull Integer requesterQuark : ss.getQuarks("Requester", "*")) {
            long requesterId = getId(requesterQuark);
            String requesterName = ss.getAttributeName(requesterQuark);
            TmfTreeDataModel requesterEntry = new TmfTreeDataModel(requesterId, traceId, Collections.singletonList(requesterName), false, null);
            entryList.add(requesterEntry);
            // Get all the <id> child attributes of each <requester>
            // Get an id and create an element for each <id>
            for (@NonNull Integer idQuark : ss.getSubAttributes(requesterQuark, false)) {
                long idId = getId(idQuark);
                String idName = ss.getAttributeName(idQuark);
                String color = COLORS.get(Integer.parseInt(idName) % 4).toString().substring(0, 7);
                OutputElementStyle style = new OutputElementStyle(null, ImmutableMap.of(
                        StyleProperties.COLOR, color,
                        StyleProperties.SERIES_STYLE, StyleProperties.SeriesStyle.DOT,
                        StyleProperties.WIDTH, 3));
                TmfTreeDataModel idEntry = new TmfTreeDataModel(idId, requesterId, Collections.singletonList(idName), true, style);
                entryList.add(idEntry);
            }
        }

        // Return the list of all created elements
        return new TmfTreeModel<>(Collections.singletonList("Requester"), ImmutableList.copyOf(entryList));
    }

    @Deprecated
    @Override
    protected @Nullable Map<String, IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
    }

    @Override
    protected @Nullable Collection<IYModel> getYSeriesModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        // Extract the requested timestamps and ids from the parameters
        List<@NonNull Long> selectedItems = DataProviderParameterUtils.extractSelectedItems(fetchParameters);
        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);
        long start = Math.max(ss.getStartTime(), timeRequested.get(0));
        long end = Math.min(ss.getCurrentEndTime(), Iterables.getLast(timeRequested));
        if (end < start) {
            return Collections.emptyList();
        }
        // Get the ids to quarks map for the requested ids
        Map<@NonNull Long, @NonNull Integer> idsToQuark = getSelectedEntries(selectedItems);

        // Compile the list of quarks needed to get y-value data from state system
        Collection<@NonNull Integer> quarks = new ArrayList<>();
        List<@NonNull Integer> seriesQuarks = ss.getQuarks("Requester", "*", "*");
        // Keep only the selected quarks that belong to a series
        quarks.addAll(idsToQuark.values());
        quarks.retainAll(seriesQuarks);
        for (Integer quark : Lists.newArrayList(quarks)) {
            int numberQuark = ss.optQuarkRelative(quark, "number");
            if (numberQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                quarks.add(numberQuark);
            } else {
                quarks.remove(quark);
            }
        }

        // Do a 2D query of the state system and collect the returned intervals in a tree multimap per quark
        TreeMultimap<Integer, ITmfStateInterval> intervals = TreeMultimap.create(Comparator.naturalOrder(),
                Comparator.comparing(ITmfStateInterval::getStartTime));
        for (ITmfStateInterval interval : ss.query2D(quarks, start, end)) {
            intervals.put(interval.getAttribute(), interval);
        }

        Collection<IYModel> yModels = new ArrayList<>();
        // For each id/quark pair, get the list of intervals
        for (Entry<@NonNull Long, @NonNull Integer> entry : idsToQuark.entrySet()) {
            Long entryId = entry.getKey();
            Integer quark = entry.getValue();
            if (!seriesQuarks.contains(quark)) {
                // Ignore id/quark pairs that do not have a series
                continue;
            }
            Iterator<ITmfStateInterval> iterator = intervals.get(quark).iterator();
            double[] yValues = new double[timeRequested.size()];
            ITmfStateInterval interval = null;
            // For each requested timestamp find the corresponding state interval
            for (int i = 0; i < timeRequested.size(); i++) {
                long time0 = timeRequested.get(i);
                long time1 = i + 1 < timeRequested.size() ? timeRequested.get(i + 1) : Long.MAX_VALUE;
                while ((interval == null || interval.getEndTime() < time0) ||
                        !Integer.valueOf(IEventConstants.ProcessingStates.PROCESSING.ordinal()).equals(interval.getValue())) {
                    if (iterator.hasNext()) {
                        interval = iterator.next();
                    } else {
                        interval = null;
                        break;
                    }
                }
                // If interval is the correct state, find the corresponding value and add to an array
                // Otherwise add zero value to the array
                if (interval != null && interval.getStartTime() < time1 && interval.getEndTime() >= time0) {
                    int numberQuark = ss.optQuarkRelative(quark, "number");
                    ITmfStateInterval numberInterval = getIntervalAt(intervals.get(numberQuark), interval.getStartTime());
                    yValues[i] = numberInterval.getValueLong();
                } else {
                    yValues[i] = 0.0;
                }
            }
            // Create a y-series model and add to a list
            yModels.add(new YModel(entryId, ss.getAttributeName(quark), yValues));
        }
        // Return the list of all created y-series models
        return yModels;
    }

    private static @Nullable ITmfStateInterval getIntervalAt(NavigableSet<ITmfStateInterval> intervals, long time) {
        ITmfStateInterval interval = intervals.floor(new TmfStateInterval(time, time, -1, null));
        if (interval != null && interval.intersects(time)) {
            return interval;
        }
        return null;
    }
}
