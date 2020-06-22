/*******************************************************************************
 * Copyright (c) 2016, 2020 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.training.example;

public interface IEventConstants {
    String BALL_REQUEST = "ust_master:BALL_REQUEST_RECEIVE";
    String BALL_REPLY = "ust_master:BALL_REPLY_SEND";
    String CREATE_EVENT = "ust_master:CREATE";
    String START_EVENT = "ust_master:START";
    String STOP_EVENT = "ust_master:STOP";
    String END_EVENT = "ust_master:END";
    String PROCESS_INIT_EVENT = "ust_master:PROCESS_INIT";
    String PROCESS_START_EVENT = "ust_master:PROCESS_START";
    String PROCESS_END_EVENT = "ust_master:PROCESS_END";

    enum ProcessingStates {
        INITIALIZING,
        PROCESSING,
        WAITING;
    }
}
