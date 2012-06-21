/*
 * Copyright 2011 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.File;
import java.util.Map;

/**
 * @author Dmitry Shohov
 */
public interface IStrategies {

    public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException;

    public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException;

    public void stopStrategy(long strategyId) throws JFException;
    
    /**
     * Stop all nested strategies started by either {@link #startStrategy(IStrategy, IStrategyListener, boolean)} or {@link #startStrategy(File, IStrategyListener, Map, boolean)}.<br/>
     * <b>NOTE:</b> The strategy that uses this method has not been stopped and must have <b>full access</b> rights granted. 
     * @throws JFException if main strategy has no <b>full access</b>, if error has been thrown on stopping of any executed strategy.
     */
    public void stopAll() throws JFException;
}
