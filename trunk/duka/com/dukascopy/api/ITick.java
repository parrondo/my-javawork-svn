/*
 * Copyright 2010 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Tick data
 * 
 * @author Denis Larka
 */
public interface ITick {
    /**
     * Time of the tick
     * 
     * @return time
     */
    long getTime();

    /**
     * Best ask price on the market
     * 
     * @return ask price
     */
    double getAsk();

    /**
     * Best bid price on the market
     * 
     * @return bid price
     */
    double getBid();

    /**
     * Volume available at the best ask price on the market
     * 
     * @return ask volume
     */
    double getAskVolume();

    /**
     * Volume available at the best bid price on the market
     * 
     * @return bid volume
     */
    double getBidVolume();

    /**
     * 
     * An array of top ask prices on the market.
     * @see IContext#setSubscribedInstruments(java.util.Set)
     * 
     * @return ask prices
     */
    double[] getAsks();

    /**
     * An array of top bid prices on the market.
     * @see IContext#setSubscribedInstruments(java.util.Set)
     * 
     * @return bid prices
     */
    double[] getBids();

    /**
     * An array of top ask volumes on the market, correspondingly to the asks returned from {@link #getAsks()} method
     * @see IContext#setSubscribedInstruments(java.util.Set)
     * 
     * @return ask volumes
     */
    double[] getAskVolumes();

    /**
     * An array of  bid volumes on the market, correspondingly to the bids returned from {@link #getBids()} method
     * @see IContext#setSubscribedInstruments(java.util.Set)
     * 
     * @return bid volumes
     */
    double[] getBidVolumes();

    /**
     * Total ask volume available on the market
     * @see IContext#setSubscribedInstruments(java.util.Set)
     *
     * @return total ask volume
     */
    double getTotalAskVolume();

    /**
     * Total bid volume available on the market
     * @see IContext#setSubscribedInstruments(java.util.Set)
     *
     * @return
     */
    double getTotalBidVolume();
}
