/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.IBar;

/**
 * @author Mark Vilkel
 */
public interface IPriceAggregationBar extends IBar {
    /**
     * Returns end time of the bar
     * 
     * @return 
     */
    long getEndTime();
    
    /**
     * 
     * Returns ticks/candles count which was used to form current bar
     * 
     * @return
     */
    long getFormedElementsCount();

}
