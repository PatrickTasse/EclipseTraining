/*******************************************************************************
 * Copyright (c) 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.training.example;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderFactory;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;

public class ProcessingValuesStatisticsDataProviderFactory implements IDataProviderFactory {

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        // TODO: Get the analysis module for the trace
        // TODO: Schedule the analysis
        // TODO: Create and return a data provider instance
//        ProcessingTimeAnalysis module = TmfTraceUtils.getAnalysisModuleOfClass(trace, ProcessingTimeAnalysis.class, ProcessingTimeAnalysis.ID);
//        if (module != null) {
//            module.schedule();
//            return new ProcessingValuesStatisticsDataProvider(trace, module);
//        }
        return null;
    }

}
