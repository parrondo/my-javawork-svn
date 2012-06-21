/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.feed.IFeedDescriptor;


/**
 * Provide with system state:
 * <ul>
 * <li>The information about daily high/low prices for corresponding instruments. 
 * <li>The times of first ticks, bars, candles etc. for corresponding instruments.
 * <li>Forex Sentiment Indices.
 * <li>Offline Time Intervals
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
    
    
    
    /**
     * Returns <b>last updated</b> Forex Sentiment Index for specified {@link Instrument}.<br/>
     * Equivalent to the {@link #getFXSentimentIndex(instrument, System.currentTimeMillis) } for live data and
     * {@link #getFXSentimentIndex(instrument, pseudo_current_historical_tester_time) } for historical data.
     * @param instrument {@link Instrument} of sentiment index
     * @return the last updated sentiment index or <code>null</code> if there is no such
     */
    IFXSentimentIndex  getFXSentimentIndex(Instrument instrument);
    
    /**
     * Returns Forex Sentiment Index which was most up-to-date at a point of specified <code>time</code>
     * @param instrument {@link Instrument} of sentiment index
     * @param time the point of time in the past in milliseconds.<br/>
     * To calculate the right time point one can use either
     * <ul>
     * <li> standard java utils {@link Calendar}/{@link Date}/{@link TimeUnit}/{@link DateFormat} or
     * <li> {@link JFUtils#getTimeForNPeriodsBack(Period, long, int)} / {@link JFUtils#getTimeForNPeriodsForward(Period, long, int)} or
     * <li> custom approaches
     * </ul>
     * @return the sentiment index which was most up-to-date at a point of specified <code>time</code> or <code>null</code> if there is no such.
     * @see JFUtils#getTimeForNPeriodsBack(Period, long, int)
     * @see JFUtils#getTimeForNPeriodsForward(Period, long, int)
     */
    IFXSentimentIndex  getFXSentimentIndex(Instrument instrument, long time);
    
    /**
     * Returns <b>last updated</b> Forex Sentiment Index for specified {@link Currency}.<br/>
     * Equivalent to the {@link #getFXSentimentIndex(currency, System.currentTimeMillis)} for live data and
     * {@link #getFXSentimentIndex(currency, pseudo_current_historical_tester_time) } for historical data.
     * @param currency {@link Currency} of sentiment index.
     * @return the last updated sentiment index or <code>null</code> if there is no such
     */
    IFXSentimentIndex getFXSentimentIndex(Currency currency);
    
    /**
     * Returns Forex Sentiment Index which was most up-to-date at a point of specified <code>time</code>
     * @param currency {@link Currency} of sentiment index.
     * @param time the point of time in the past in milliseconds.<br/>
     * To calculate the right time point one can use either 
     * <ul>
     * <li> standard java utils {@link Calendar}/{@link Date}/{@link TimeUnit}/{@link DateFormat} or
     * <li> {@link JFUtils#getTimeForNPeriodsBack(Period, long, int)} / {@link JFUtils#getTimeForNPeriodsForward(Period, long, int)} or
     * <li> custom approaches
     * </ul>
     * @return the sentiment index which was most up-to-date at a point of specified <code>time</code> or <code>null</code> if there is no such.
     * @see JFUtils#getTimeForNPeriodsBack(Period, long, int)
     * @see JFUtils#getTimeForNPeriodsForward(Period, long, int)
     */
    IFXSentimentIndex getFXSentimentIndex(Currency currency, long time);
    
    
    /**
     * Returns <tt>true</tt> if specified <tt>time</tt> is within the limits of offline (weekend) period. 
     * @param time time in milliseconds
     * @return <tt>true</tt> if specified <tt>time</tt> is within the limits of offline (weekend) period, <tt>false</tt> - otherwise
     */
    boolean isOfflineTime(long time);

    
    /**
     * Returns either present, current offline (weekend) {@link ITimeDomain time interval} or the next <b>approximate</b> upcoming one.<br/>
     * The same as {@link #getOfflineTimeDomain(shift)} with shift == 0
     * @return either present, current offline (weekend) {@link ITimeDomain time interval} or the next <b>approximate</b> upcoming one. 
     * @throws JFException when some error occurs
     * @see #getOfflineTimeDomain(int)
     * 
     */
    ITimeDomain getOfflineTimeDomain() throws JFException;
    
    /**
     * Returns offline (weekend) {@link ITimeDomain time interval} that is shifted back or forward for number of offline intervals specified in <code>shift</code> parameter.
     * @param shift number of offline intervals back or forward in time staring from current one. E.g.:
     * <ul>
     * <li><b>0</b> - current or next <b>approximate</b> offline period, 
     * <li><b>-1</b> - previous offline (last finished one), 
     * <li><b>1</b> - next <b>approximate</b> after current one, etc.
     * </ul>
     * @return offline (weekend) {@link ITimeDomain time interval} that is shifted back or forward for number of offline intervals specified in <code>shift</code> parameter
     * @throws JFException when some error occurs
     */
    ITimeDomain getOfflineTimeDomain(int shift) throws JFException;
    
    /**
     * Returns the set of offline (weekend) {@link ITimeDomain time intervals} ascending ordered by time which are within the limits of <tt>from</tt> and <tt>to</tt> parameters.
     * @param from start of the time interval for which offline periods should be loaded. If <tt>start</tt> time is within the limits of offline period - this period will be returned as first element of resulting set. 
     * @param to end of the time interval for which offline periods should be loaded. If <tt>end</tt> time is within the limits of offline period - this period will be returned as last element of resulting set.
     * @return the set of offline (weekend) {@link ITimeDomain time intervals} ascending ordered by time which are within the limits of <tt>from</tt> and <tt>to</tt> parameters.
     * @throws JFException when some error occurs
     */
    Set<ITimeDomain> getOfflineTimeDomains(long from, long to) throws JFException;
    
}
