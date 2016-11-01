/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import org.eclipse.tracecompass.internal.provisional.tmf.ui.widgets.timegraph.BaseDataProviderTimeGraphPresentationProvider;
import org.eclipse.tracecompass.tmf.ui.views.timegraph.BaseDataProviderTimeGraphView;

public class ProcessingStatesView extends BaseDataProviderTimeGraphView {

    public static final String ID = "org.eclipse.tracecompass.training.example.processing.states";

    private static String[] FILTER_COLUMNS = { "Entry" };

    private static class FilterLabelProvider extends TreeLabelProvider {}

    public ProcessingStatesView() {
        // TODO: Call super with view ID, presentation provider and data provider ID
//        super(ID, new BaseDataProviderTimeGraphPresentationProvider(), ProcessingStatesDataProvider.ID);

        // TODO: Enable entry filtering by setting filter columns and label provider
//        setFilterColumns(FILTER_COLUMNS);
//        setFilterLabelProvider(new FilterLabelProvider());
    }

}
