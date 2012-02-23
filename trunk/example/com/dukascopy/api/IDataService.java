/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Collection;
import java.util.Map;

import com.dukascopy.api.feed.IFeedDescriptor;


/**
 * Provide with system state:
 * <ul>
 * <li>The information about daily high/low prices for corresponding instruments. 
 * <li>The times of first ticks, bars, candles etc. for corresponding instruments.
 * <li>...
 * </ul>
 * @author aburenin
 *
 */
public interface IDataService {
    
    
    /**
     * The method adds daily high/low listener
     * After addition listener will be notified with the current best daily high/low (If in progress daily candle exists in the system)
     * If in progress candle doesn't exist in the system listener will be notified as soon as candle arrives
     * 
     * @param instrument
     * @param listener
     */
    void addDailyHighLowListener(
            Instrument instrument,
            IDailyHighLowListener listener
    );
    
    /**
     * Removes daily high/low listener
     * @param listener
     */
    void removeDailyHighLowListener(IDailyHighLowListener listener);
    
    /**
     * Returns all high/low listeners subscribed on the passed instrument
     * 
     * @param instrument
     * @return
     */
    Collection<IDailyHighLowListener> getDailyHighLowListeners(Instrument instrument);
    
    /**
     * Returns all high/low listeners
     * 
     * @return
     */
    Map<Instrument, Collection<IDailyHighLowListener>> getDailyHighLowListeners();

    /**
     * Removes all high/low listeners
     */
    void removeAllDailyHighLowListeners();
    
    
    
    
    /**
     * Returns the time of first feed data specified in {@link IFeedDescriptor}.
     * @param feedDescriptor the {@link IFeedDescriptor} specifies the feed data the first time must be returned.<br/>
     * {@link IFeedDescriptor#getDataType()}  determines the required properties of {@link IFeedDescriptor} must be set and<br/>
     * the specifies the method will be invoked:
     * <ul>
     *  <li> DataType#TICKS - requires {@link Instrument}, equals to {@link #getTimeOfFirstTick(Instrument)} 
     *  <li> DataType#TICK_BAR - requires {@link Instrument}, equals to {@link #getTimeOfFirstTickBar(Instrument)} 
     *  <li> DataType#TIME_PERIOD_AGGREGATION - requires {@link Instrument} and {@link Period}, equals to {@link #getTimeOfFirstCandle(Instrument, Period)} 
     *  <li> DataType#PRICE_RANGE_AGGREGATION - requires {@link Instrument} and {@link PriceRange}, equals to {@link #getTimeOfFirstRangeBar(Instrument, PriceRange)} 
     *  <li> DataType#POINT_AND_FIGURE - requires {@link Instrument}, {@link PriceRange} and and {@link ReversalAmount}, equals to {@link #getTimeOfFirstPointAndFigure(Instrument, PriceRange, ReversalAmount)}
     *  <li> DataType#RENKO - requires {@link Instrument} and {@link PriceRange}, equals to {@link #getTimeOfFirstRenko(Instrument, PriceRange)} 
     * </ul>
     * @return Returns the time of first feed data specified in {@link IFeedDescriptor} or {@link Long#MAX_VALUE} if there is no one.
     */
    long getTimeOfFirstCandle(IFeedDescriptor feedDescriptor);
    
    
    /**
     * Returns the time of first {@link DataType#TICKS ticks} for specified {@link Instrument}
     * @param instrument the {@link Instrument} the first tick's time must be returned.
     * @return the time of first {@link DataType#TICKS ticks} for specified {@link Instrument} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TICKS
     */
    long getTimeOfFirstTick(Instrument instrument);                                                                 
    
    /**
     * Returns the time of first {@link DataType#TICK_BAR tick bars} for specified {@link Instrument}
     * @param instrument the {@link Instrument} the first Tick Bars' time must be returned.
     * @return the time of first {@link DataType#TICK_BAR tick bars} for specified {@link Instrument} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TICK_BAR
     */
    long getTimeOfFirstTickBar(Instrument instrument);                                                              
    
    /**
     * Returns the time of first {@link DataType#TIME_PERIOD_AGGREGATION candles} for specified {@link Instrument} and {@link Period}
     * @param instrument the {@link Instrument} the first {@link DataType#TIME_PERIOD_AGGREGATION candles}' time must be returned.
     * @param period the {@link Period} the first {@link DataType#TIME_PERIOD_AGGREGATION candles}' time must be returned.
     * @return the time of first {@link DataType#TIME_PERIOD_AGGREGATION candles} for specified {@link Instrument} and {@link Period}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TIME_PERIOD_AGGREGATION
     */
    long getTimeOfFirstCandle(Instrument instrument, Period period);                                                
    
    /**
     * Returns the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument} and {@link PriceRange}
     * @param instrument the {@link Instrument} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @return the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument} and {@link PriceRange}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#PRICE_RANGE_AGGREGATION
     */
    long getTimeOfFirstRangeBar(Instrument instrument, PriceRange priceRange);                                      
    
    /**
     * Returns the time of first {@link DataType#POINT_AND_FIGURE point & figures} for specified {@link Instrument}, {@link PriceRange} and {@link ReversalAmount}
     * @param instrument the {@link Instrument} the first {@link DataType#POINT_AND_FIGURE point & figures}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#POINT_AND_FIGURE point & figures}' time must be returned.
     * @param reversalAmount the {@link ReversalAmount} the first {@link DataType#POINT_AND_FIGURE point & figures}' time must be returned.
     * @return the time of first {@link DataType#POINT_AND_FIGURE point & figures} for specified {@link Instrument}, {@link PriceRange} and {@link ReversalAmount}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#POINT_AND_FIGURE
     */
    long getTimeOfFirstPointAndFigure(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount); 
    
    /**
     * Returns the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link PriceRange}
     * @param instrument the {@link Instrument} the first {@link DataType#RENKO renko}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#RENKO renko}' time must be returned.
     * @return the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link PriceRange} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#RENKO
     */
    long getTimeOfFirstRenko(Instrument instrument, PriceRange priceRange);                                         
    
}
