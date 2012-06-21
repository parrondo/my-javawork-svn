/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * Provides access to system services
 * 
 * @author Dmitry Shohov
 */
public interface IIndicatorContext {
    
    /**
     * Returns interface that allows to write messages into the Messages table
     * 
     * @return interface for messages sending
     */
    IConsole getConsole();
    
    /**
     * Returns interface that can be used to get indicators registered in the system
     * 
     * @return interface to work with indicators
     */
    IIndicatorsProvider getIndicatorsProvider();

    /**
     * Returns instrument of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return instrument of the primary input
     * 
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getInstrument() instead
     */
    public Instrument getInstrument();

    /**
     * Returns period of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return period of the primary input
     * 
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getPeriod() instead
     */
    public Period getPeriod();

    /**
     * Returns offer side of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return offer side of the pimary input
     * 
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getOfferSide() instead
     */
    public OfferSide getOfferSide();
    
    /**
     * Provides access to history from indicators
     * 
     * @return history interface
     */
    public IHistory getHistory();
    
    public IAccount getAccount();
    
    /**
     * @return chart state described by bean {@link IFeedDescriptor}
     */
    public IFeedDescriptor getFeedDescriptor();
    
}
