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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.annotations.Annotation;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.annotations.AnnotationCategoriesModel;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.annotations.AnnotationModel;
import org.eclipse.tracecompass.internal.provisional.tmf.core.model.annotations.IOutputAnnotationProvider;
import org.eclipse.tracecompass.internal.tmf.core.model.timegraph.AbstractTimeGraphDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.interval.TmfStateInterval;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderParameterUtils;
import org.eclipse.tracecompass.tmf.core.model.CommonStatusMessage;
import org.eclipse.tracecompass.tmf.core.model.IOutputStyleProvider;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.OutputStyleModel;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphArrow;
import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphRowModel;
import org.eclipse.tracecompass.tmf.core.model.timegraph.ITimeGraphState;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphArrow;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphEntryModel;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphModel;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphRowModel;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphState;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
import org.eclipse.tracecompass.tmf.core.response.ITmfResponse;
import org.eclipse.tracecompass.tmf.core.response.TmfModelResponse;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.training.example.IEventConstants.ProcessingStates;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class ProcessingStatesDataProvider extends AbstractTimeGraphDataProvider<ProcessingTimeAnalysis, TimeGraphEntryModel> implements IOutputStyleProvider, IOutputAnnotationProvider {

    public static final @NonNull String ID = "org.eclipse.tracecompass.training.example.dataprovider.processing.states"; //$NON-NLS-1$

    private static final String INITIALIZING_STATE_KEY = ProcessingStates.INITIALIZING.name();
    private static final String PROCESSING_STATE_KEY = ProcessingStates.PROCESSING.name();
    private static final String WAITING_STATE_KEY = ProcessingStates.WAITING.name();
    private static final String BALL_REQUEST_ARROW_KEY = "BallRequest";
    private static final String BALL_REPLY_ARROW_KEY = "BallReply";
    private static final String BALL_ANNOTATION_KEY = "BallAnnotation";

    private static final String BALL_CATEGORY = "Ball";

    private static final String VALUE_LABEL = "Value";

    private Map<String, OutputElementStyle> fStyles = new HashMap<>();

    public ProcessingStatesDataProvider(ITmfTrace trace, ProcessingTimeAnalysis analysisModule) {
        super(trace, analysisModule);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected TmfTreeModel<@NonNull TimeGraphEntryModel> getTree(@NonNull ITmfStateSystem ss,
            Map<String, Object> parameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        long startTime = ss.getStartTime();
        long endTime = ss.getCurrentEndTime();
        List<@NonNull TimeGraphEntryModel> entryList = new ArrayList<>();

        // Create a root entry for the trace
        long traceId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        TimeGraphEntryModel traceEntry = new TimeGraphEntryModel(traceId, -1, getTrace().getName(), startTime, endTime, false);
        entryList.add(traceEntry);

        // Get all the <receiver> attributes from the state system
        // Get an id and create an entry for each <receiver>
        for (@NonNull Integer receiverQuark : ss.getQuarks("Receiver", "*")) {
            long receiverId = getId(receiverQuark);
            String receiverName = ss.getAttributeName(receiverQuark);
            TimeGraphEntryModel receiverEntry = new TimeGraphEntryModel(receiverId, traceId, receiverName, startTime, endTime, true);
            entryList.add(receiverEntry);
        }

        // Get all the <requester> attributes from the state system
        // Get an id and create an entry for each <requester>
        for (@NonNull Integer requesterQuark : ss.getQuarks("Requester", "*")) {
            long requesterId = getId(requesterQuark);
            String requesterName = ss.getAttributeName(requesterQuark);
            TimeGraphEntryModel requesterEntry = new TimeGraphEntryModel(requesterId, traceId, requesterName, startTime, endTime, true);
            entryList.add(requesterEntry);
            // Get all the <id> child attributes of each <requester>
            // Get an id and create an entry for each <id>
            for (@NonNull Integer idQuark : ss.getSubAttributes(requesterQuark, false)) {
                long idId = getId(idQuark);
                String idName = ss.getAttributeName(idQuark);
                TimeGraphEntryModel idEntry = new TimeGraphEntryModel(idId, requesterId, idName, startTime, endTime, true);
                entryList.add(idEntry);
            }
        }

        // Return the list of all created entries
        return new TmfTreeModel<>(Collections.emptyList(), ImmutableList.copyOf(entryList));
    }

    @Override
    protected @Nullable TimeGraphModel getRowModel(@NonNull ITmfStateSystem ss,
            @NonNull Map<@NonNull String, @NonNull Object> parameters, @Nullable IProgressMonitor monitor)
            throws StateSystemDisposedException {
        // Extract the requested timestamps and ids from the parameters
        List<@NonNull Long> selectedItems = DataProviderParameterUtils.extractSelectedItems(parameters);
        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(parameters);

        // Get the ids to quarks map for the requested ids
        Map<@NonNull Long, @NonNull Integer> idsToQuark = getSelectedEntries(selectedItems);

        // Compile the list of quarks needed to get state data from state system
        Collection<@NonNull Integer> quarks = new ArrayList<>();
        quarks.addAll(ss.getQuarks("Requester", "*"));
        quarks.addAll(ss.getQuarks("Requester", "*", "*"));
        quarks.retainAll(idsToQuark.values());

        // Do a 2D query of the state system and collect the returned intervals in a tree multimap per quark
        TreeMultimap<Integer, ITmfStateInterval> intervals = TreeMultimap.create(Comparator.naturalOrder(),
                Comparator.comparing(ITmfStateInterval::getStartTime));
        for (ITmfStateInterval interval : ss.query2D(quarks, timeRequested)) {
            intervals.put(interval.getAttribute(), interval);
        }

        List<@NonNull ITimeGraphRowModel> rows = new ArrayList<>();
        // For each id/quark pair, get the list of intervals
        for (Entry<@NonNull Long, @NonNull Integer> entry : idsToQuark.entrySet()) {
            Long entryId = entry.getKey();
            Integer quark = entry.getValue();
            List<ITimeGraphState> states = new ArrayList<>();
            // For each interval, create a time graph state model and add to a list
            for (ITmfStateInterval interval : intervals.get(quark)) {
                long time = interval.getStartTime();
                long duration = interval.getEndTime() - time + 1;
                if (interval.getValue() instanceof Integer) {
                    String label = "";
                    String styleKey = ProcessingStates.values()[interval.getValueInt()].name();
                    OutputElementStyle style = new OutputElementStyle(styleKey);
                    states.add(new TimeGraphState(time, duration, label, style));
                } else if (interval.getValue() == null) {
                    states.add(new TimeGraphState(time, duration, Integer.MIN_VALUE));
                }
            }
            // Create a time graph row model with this list
            rows.add(new TimeGraphRowModel(entryId, states));
        }
        // Return the list of all created time graph row models
        return new TimeGraphModel(rows);
    }

    @Override
    public @NonNull TmfModelResponse<@NonNull List<@NonNull ITimeGraphArrow>> fetchArrows(
            @NonNull Map<@NonNull String, @NonNull Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        ITmfStateSystem ss = getAnalysisModule().getStateSystem();
        // Extract the requested timestamps from the parameters
        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);

        // Compile the list of quarks needed to get arrow data from state system
        Collection<@NonNull Integer> quarks = new ArrayList<>();
        List<@NonNull Integer> receiverQuarks = ss.getQuarks("Receiver", "*");
        quarks.addAll(receiverQuarks);
        List<@NonNull Integer> requesterQuarks = ss.getQuarks("Requester", "*");
        quarks.addAll(requesterQuarks);

        // Do a 2D query of the state system and collect the returned intervals in a tree multimap per quark
        TreeMultimap<Integer, ITmfStateInterval> intervals = TreeMultimap.create(Comparator.naturalOrder(),
                Comparator.comparing(ITmfStateInterval::getStartTime));
        try {
            for (ITmfStateInterval interval : ss.query2D(quarks, timeRequested)) {
                intervals.put(interval.getAttribute(), interval);
            }
        } catch (StateSystemDisposedException e) {
            return new TmfModelResponse<>(null, ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
        }

        List<@NonNull ITimeGraphArrow> arrowList = new ArrayList<>();
        // For each quark, get the list of intervals
        for (Integer receiverQuark : receiverQuarks) {
            // For each interval, get source id, destination id, start and duration
            for (ITmfStateInterval interval : intervals.get(receiverQuark)) {
                if (interval.getValue() instanceof String) {
                    int requesterQuark = ss.optQuarkAbsolute("Requester", interval.getValueString());
                    long receiverId = getId(receiverQuark);
                    long requesterId = getId(requesterQuark);
                    long requestTime = interval.getStartTime();
                    ITmfStateInterval requesterStartInterval = getIntervalAt(intervals.get(requesterQuark), requestTime);
                    if (requesterStartInterval != null) {
                        long requestDuration = requesterStartInterval.getEndTime() - requestTime;
                        // Create a time graph arrow model and add to a list
                        arrowList.add(new TimeGraphArrow(receiverId, requesterId, requestTime, requestDuration, getStyle(BALL_REQUEST_ARROW_KEY)));
                    }
                    ITmfStateInterval requesterEndInterval = getIntervalAt(intervals.get(requesterQuark), interval.getEndTime());
                    if (requesterEndInterval != null) {
                        long replyTime = requesterEndInterval.getStartTime();
                        long replyDuration = interval.getEndTime() + 1 - replyTime;
                        arrowList.add(new TimeGraphArrow(requesterId, receiverId, replyTime, replyDuration, getStyle(BALL_REPLY_ARROW_KEY)));
                    }
                }
            }
        }
        // Return the list of all created time graph arrow models
        return new TmfModelResponse<>(arrowList, ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
    }

    @Override
    public @NonNull TmfModelResponse<@NonNull Map<@NonNull String, @NonNull String>> fetchTooltip(
            @NonNull Map<@NonNull String, @NonNull Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        // Extract the requested timestamp, id and element from the parameters
        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);
        List<@NonNull Long> selectedItem = DataProviderParameterUtils.extractSelectedItems(fetchParameters);
        Object element = fetchParameters.get(DataProviderParameterUtils.REQUESTED_ELEMENT_KEY);

        Map<String, String> tooltips = new HashMap<>();
        if (element instanceof ITimeGraphState) {
            // Get the quark associated to that id
            long entryId = selectedItem.get(0);
            Integer quark = getSelectedEntries(Collections.singleton(entryId)).get(entryId);
            // Query the state system for the interval value
            ITmfStateSystem ss = getAnalysisModule().getStateSystem();
            int numberQuark = ss.optQuarkRelative(quark, "number");
            if (numberQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                try {
                    ITmfStateInterval idInterval = ss.querySingleState(timeRequested.get(0), quark);
                    ITmfStateInterval numberInterval = ss.querySingleState(timeRequested.get(0), numberQuark);
                    // Add a name/value pair to a tooltip map
                    if (Integer.valueOf(IEventConstants.ProcessingStates.PROCESSING.ordinal()).equals(idInterval.getValue())
                            && numberInterval.getValue() instanceof Long) {
                        tooltips.put(VALUE_LABEL, Long.toString(numberInterval.getValueLong()));
                    }
                } catch (StateSystemDisposedException e) {
                    // Ignored
                }
            }
        }
        // Return the map of tooltips
        return new TmfModelResponse<>(tooltips, ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
    }

    @Override
    public @NonNull TmfModelResponse<@NonNull AnnotationCategoriesModel> fetchAnnotationCategories(
            @NonNull Map<@NonNull String, @NonNull Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        // Return the list of annotation categories
        List<String> categories = Arrays.asList(BALL_CATEGORY);
        return new TmfModelResponse<>(new AnnotationCategoriesModel(categories), ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
    }

    @Override
    public TmfModelResponse<AnnotationModel> fetchAnnotations(
            @NonNull Map<@NonNull String, @NonNull Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        ITmfStateSystem ss = getAnalysisModule().getStateSystem();
        // Extract the requested timestamps and ids from the parameters
        List<@NonNull Long> selectedItems = DataProviderParameterUtils.extractSelectedItems(fetchParameters);
        List<Long> timeRequested = DataProviderParameterUtils.extractTimeRequested(fetchParameters);

        // Get the ids to quarks map for the requested ids
        Map<@NonNull Long, @NonNull Integer> idsToQuark = getSelectedEntries(selectedItems);

        // Compile the list of quarks needed to get annotation data from state system
        Collection<@NonNull Integer> quarks = new ArrayList<>();
        quarks.addAll(ss.getQuarks("Receiver", "*"));
        quarks.retainAll(idsToQuark.values());

        // Do a 2D query of the state system and collect the returned intervals in a tree multimap per quark
        TreeMultimap<Integer, ITmfStateInterval> intervals = TreeMultimap.create(Comparator.naturalOrder(),
                Comparator.comparing(ITmfStateInterval::getStartTime));
        try {
            for (ITmfStateInterval interval : ss.query2D(quarks, timeRequested)) {
                intervals.put(interval.getAttribute(), interval);
            }
        } catch (StateSystemDisposedException e) {
            return new TmfModelResponse<>(null, ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
        }

        Multimap<String, Annotation> annotations = HashMultimap.create();
        // For each id/quark pair, get the list of intervals
        for (Entry<@NonNull Long, @NonNull Integer> entry : idsToQuark.entrySet()) {
            Long entryId = entry.getKey();
            Integer quark = entry.getValue();
            // For each interval, create an annotation model and add to a map of annotations per category
            for (ITmfStateInterval interval : intervals.get(quark)) {
                if (interval.getValue() instanceof String) {
                    long requestTime = interval.getStartTime();
                    long replyTime = interval.getEndTime() + 1;
                    long duration = 0;
                    annotations.put(BALL_CATEGORY, new Annotation(requestTime, duration, entryId, null, getStyle(BALL_ANNOTATION_KEY)));
                    annotations.put(BALL_CATEGORY, new Annotation(replyTime, duration, entryId, null, getStyle(BALL_ANNOTATION_KEY)));
                }
            }
        }
        // Return the map of annotations
        return new TmfModelResponse<>(new AnnotationModel(annotations.asMap()), ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
    }

    @Override
    public @NonNull TmfModelResponse<@NonNull OutputStyleModel> fetchStyle(
            @NonNull Map<@NonNull String, @NonNull Object> fetchParameters, @Nullable IProgressMonitor monitor) {
        Map<String, OutputElementStyle> styleMap = new LinkedHashMap<>();
        // Assign a unique style key to each style and add to a map of style per key
        // Create and add state styles, set BACKGROUND_COLOR, STYLE_NAME, STYLE_GROUP
        styleMap.put(INITIALIZING_STATE_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.BACKGROUND_COLOR, "#888888",
                StyleProperties.STYLE_GROUP, "States",
                StyleProperties.STYLE_NAME, "Initializing")));
        styleMap.put(PROCESSING_STATE_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.BACKGROUND_COLOR, "#BCDD68",
                StyleProperties.STYLE_GROUP, "States",
                StyleProperties.STYLE_NAME, "Processing")));
        styleMap.put(WAITING_STATE_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.BACKGROUND_COLOR, "#CCCCCC",
                StyleProperties.STYLE_GROUP, "States",
                StyleProperties.STYLE_NAME, "Waiting")));
        // Create and add arrow styles, set COLOR, WIDTH, STYLE_NAME, STYLE_GROUP
        styleMap.put(BALL_REQUEST_ARROW_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.COLOR, "#00FF00",
                StyleProperties.WIDTH, 2,
                StyleProperties.STYLE_GROUP, "Arrows",
                StyleProperties.STYLE_NAME, "Ball Request")));
        styleMap.put(BALL_REPLY_ARROW_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.COLOR, "#CC0000",
                StyleProperties.WIDTH, 2,
                StyleProperties.STYLE_GROUP, "Arrows",
                StyleProperties.STYLE_NAME, "Ball Reply")));
        // Create and add annotations styles, set SYMBOL_TYPE, COLOR, HEIGHT, STYLE_NAME, STYLE_GROUP
        styleMap.put(BALL_ANNOTATION_KEY, new OutputElementStyle(null, ImmutableMap.of(
                StyleProperties.SYMBOL_TYPE, StyleProperties.SymbolType.CIRCLE,
                StyleProperties.COLOR, "#0000CC",
                StyleProperties.HEIGHT, 0.25f,
                StyleProperties.STYLE_GROUP, "Markers",
                StyleProperties.STYLE_NAME, "Ball")));
        // Make sure the state, arrow and annotation model elements use one of these style keys for their style
        // Return the map of styles
        return new TmfModelResponse<>(new OutputStyleModel(styleMap), ITmfResponse.Status.COMPLETED, CommonStatusMessage.COMPLETED);
    }

    private static @Nullable ITmfStateInterval getIntervalAt(NavigableSet<ITmfStateInterval> intervals, long time) {
        ITmfStateInterval interval = intervals.floor(new TmfStateInterval(time, time, -1, null));
        if (interval != null && interval.intersects(time)) {
            return interval;
        }
        return null;
    }

    private OutputElementStyle getStyle(String styleKey) {
        return fStyles.computeIfAbsent(styleKey, key -> new OutputElementStyle(key));
    }

    @Override
    protected boolean isCacheable() {
        return false;
    }
}
