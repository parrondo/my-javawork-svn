/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.List;

import com.dukascopy.api.feed.IPointAndFigure;
import com.dukascopy.api.feed.IPointAndFigureFeedListener;
import com.dukascopy.api.feed.IRangeBar;
import com.dukascopy.api.feed.IRangeBarFeedListener;
import com.dukascopy.api.feed.IRenkoBar;
import com.dukascopy.api.feed.IRenkoBarFeedListener;
import com.dukascopy.api.feed.ITickBar;
import com.dukascopy.api.feed.ITickBarFeedListener;

/**
 * The <code>IHistory</code> interface represents API for historical data access.
 * 
 * @author Dmitry Shohov, Denis Larka
 */
public interface IHistory {
    /**
     * Returns time of last tick received for specified instrument. Returns -1 if no tick was received yet.
     * 
     * @param instrument instrument of the tick
     * @return time of last tick or -1 if no tick was received
     * @throws JFException when instrument is not active (not opened in platform)
     */
    public long getTimeOfLastTick(Instrument instrument) throws JFException;
    
    /**
     * Returns last tick for specified instrument
     * 
     * @param instrument instrument of the tick
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform)
     */
    public ITick getLastTick(Instrument instrument) throws JFException;

    /**
     * Returns starting time of the current bar (bar currently generated from ticks) for specified instrument and period.
     * If no tick was received for this instrument, then returns -1.
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @return starting time of the current bar or -1 if no tick was received
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    public long getStartTimeOfCurrentBar(Instrument instrument, Period period) throws JFException;
    
    /**
     * Returns bar for specified instrument, period and side, that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar (currently generated from ticks), 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @param side bid or ask side of the bar
     * @param shift number of candle back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
     * @return bar or null if no bar is loaded
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    public IBar getBar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException;

    /**
     * Reads ticks from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, ticks will be returned by calling methods in <code>tickListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error.
     * This method has two main purposes: one is to load a lot of ticks without keeping them all in memory, and second is asynchronous processing
     * 
     * @param instrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @param tickListener receives data about requested ticks
     * @param loadingProgress used to control loading process
     * @throws JFException when some error occurs while creating internal request for data
     */
    public void readTicks(Instrument instrument, long from, long to, LoadingDataListener tickListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    public void readBars(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background.
     * Method returns fast after creating request for data not waiting for any data to be read from local cache.
     * After internal request is sent, bars will be returned by calling method in <code>barListener</code>.
     * LoadingProgressListener is used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    public void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;
    
    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candles in period specified in <code>numberOfCandlesBefore</code> parameter or/and
     *        time of the candle prior first candle in period specified with <code>numberOfCandlesAfter</code> parameter
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    public void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns ticks for specified instrument and time interval. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * @param instrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @return loaded ticks
     * @throws JFException when some error occurs when loading data
     */
    public List<ITick> getTicks(Instrument instrument, long from, long to) throws JFException;
    
    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException;
    
    /**
     * Returns bars for specified instrument, period, side and filter.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException;

    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>. If the requested period includes the bar that is not fully
     * formed yet (in-progress bar), then it is included even if it's flat
     *
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candle in period specified in <code>numberOfCandlesBefore</code> parameter, or
     *        time of the first candle in period specified with <code>numberOfCandlesAfter</code> parameter if <code>numberOfCandlesBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfCandlesAfter</code> if <code>numberOfCandlesBefore</code> is > 0
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException;

    /**
     * Returns starting time of the bar that includes time specified in <code>time</code> parameter
     * 
     * @param period period of the bar
     * @param time time that is included by the bar
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    public long getBarStart(Period period, long time) throws JFException;
    
    /**
     * Returns starting time of the bar next to the bar that includes time specified in <code>barTime</code> parameter
     * 
     * @param period period of the bar
     * @param barTime time that is included by the bar previous to the returned
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    public long getNextBarStart(Period period, long barTime) throws JFException;
    
    /**
     * Returns starting time of the bar previous to the bar that includes time specified in <code>barTime</code> parameter
     * 
     * @param period period to the bar
     * @param barTime time that is included by the bar next to the returned
     * @return staring time of the bar
     * @throws JFException when period is not supported
     */
    public long getPreviousBarStart(Period period, long barTime) throws JFException;
    
    /**
     * Returns starting time of the bar that is <code>numberOfBars - 1</code> back in time to the bar that includes time specified in
     * <code>to</code> parameter. Method can be used to get time for the <code>from</code> parameter for {@link #getBars}
     * method when is known time of the last bar and number of candles that needs to be loaded
     * 
     * @param period period of the bars
     * @param to time of the last bar
     * @param numberOfBars number of bars that could be loaded when passing returned time and time specified in <code>to</code> parameter in
     *        {@link #getBars} method
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    public long getTimeForNBarsBack(Period period, long to, int numberOfBars) throws JFException;
    
    /**
     * Returns starting time of the bar that is + <code>numberOfBars - 1</code> in the future to the bar that includes time specified in
     * <code>from</code> parameter. Method can be used to get time for the <code>to</code> parameter for {@link #getBars}
     * method when is known time of the first bar and number of candles that needs to be loaded
     * 
     * @param period period of the bars
     * @param from time of the first bar
     * @param numberOfBars number of bars that could be loaded when passing returned time and time specified in <code>from</code> parameter in
     *        {@link #getBars} method
     * @return starting time of the last bar
     * @throws JFException when period is not supported
     */
    public long getTimeForNBarsForward(Period period, long from, int numberOfBars) throws JFException;

    /**
     * Loads orders from the server in the background. Method returns fast after creating request for data not waiting for any data to be loaded
     * After internal request is sent, orders will be returned by calling method in <code>ordersListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method can be used for orders loading without blocking strategy execution
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param instrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @param ordersListener receives data about requested orders
     * @param loadingProgress used to control loading progress
     * @throws JFException in case of any system error
     * @see #getOrdersHistory(Instrument instrument, long from, long to)
     */
    public void readOrdersHistory(Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns orders for specified instrument and time interval. Method blocks until all data will be loaded from the server.
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param instrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @return loaded orders
     * @throws JFException in case of any system error
     */
    public List<IOrder> getOrdersHistory(Instrument instrument, long from, long to) throws JFException;

    /**
     * Returns current equity calculated for every tick
     * 
     * @return actual equity
     */
    public double getEquity();
    
    
    
    /**
     * Returns Point and Figures for specified instrument, offer side, box size and reversal amount.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&Fs
     * @param offerSide offer side of P&Fs
     * @param boxSize box size of P&Fs
     * @param reversalAmount reversal amount of P&Fs
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Point And Figures
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     */
	List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, long from, long to) throws JFException;
	
	/**
     * Returns Point and Figures for specified instrument, offer side, box size and reversal amount.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     *
	 * @param instrument instrument of the P&Fs
	 * @param offerSide offer side of the P&Fs
	 * @param priceRange price range of the P&Fs
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 */
	List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Point and Figures from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&Fs
     * @param offerSide offer side of P&Fs
     * @param boxSize box size of P&Fs
     * @param reversalAmount reversal amount of P&Fs
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, long from, long to, IPointAndFigureFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Reads Point and Figures from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&Fs
     * @param offerSide offer side of P&Fs
     * @param boxSize box size of P&Fs
     * @param reversalAmount reversal amount of P&Fs
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter, IPointAndFigureFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Returns Point and Figure for specified instrument, offer side, box size and reversal amount,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
	 * 
	 * @param instrument instrument of P&F
	 * @param offerSide bid or ask side of P&F
	 * @param boxSize box size of the P&F
	 * @param reversalAmount reversal amount of the P&F
	 * @param shift number of P&F back in time staring from current P&F. 1 - previous P&F, 2 - current P&F minus 2 bars and so on
	 * @return P&F or null if no P&F is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 */
	IPointAndFigure getPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int shift) throws JFException;

	
	
    /**
     * Returns Tick Bars for specified instrument, offer side and tick bar size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Tick Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     */
	List<ITickBar> getTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to) throws JFException;
	
	
	/**
     * Returns Tick Bars for specified instrument, offer side and tick bar size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Tick Bar live notification first, before call this method.
     *
	 * @param instrument instrument of the tick bars
	 * @param offerSide offer side of the tick bars
	 * @param tickBarSize tick bar size of the tick bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 */
	List<ITickBar> getTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Tick Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to, ITickBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Reads Tick Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, ITickBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Returns tick bar for specified instrument, offer side and tick bar size,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
	 * 
	 * @param instrument instrument of the tick bar
	 * @param offerSide bid or ask side of the tick bar
	 * @param tickBarSize tick bar size of the tick bar
	 * @param shift number of tick bar back in time staring from current tick bar. 1 - previous tick bar, 2 - current tick bar minus 2 bars and so on
	 * @return range bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 */
	ITickBar getTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int shift) throws JFException;
	
	
	
    /**
     * Returns Range Bars for specified instrument, offer side and price range.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Range Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     */
	List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to) throws JFException;
	
	/**
     * Returns Range Bars for specified instrument, offer side and price range.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     *
	 * @param instrument instrument of the Range bars
	 * @param offerSide offer side of the Range bars
	 * @param priceRange price range of the Range bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 */
	List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Range Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to, IRangeBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Reads Range Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRangeBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;

	/**
     * Returns range bar for specified instrument, offer side and price range,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
	 * 
	 * @param instrument instrument of the range bar
	 * @param offerSide bid or ask side of the range bar
	 * @param priceRange price range of the range bar
	 * @param shift number of range bar back in time staring from current range bar. 1 - previous range bar, 2 - current range bar minus 2 bars and so on
	 * @return range bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 */
	IRangeBar getRangeBar(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int shift) throws JFException;
	
	
	
	/**
	 * Returns tick for specified instrument, that is shifted back in time for number in ticks specified in <code>shift</code>
     * parameter, 0 - current tick, 1 - previous tick.
     * 
     * @param instrument instrument of the tick
     * @param shift number of tick back in time staring from current tick. 1 - previous tick, 2 - current tick minus 2 ticks and so on
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform) or other errors
	 */
	public ITick getTick(Instrument instrument, int shift) throws JFException;

	
	
	
	/**
	 * Returns renko bar for specified instrument, offer side and brick size,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
	 * 
	 * @param instrument instrument of the Renko bar
	 * @param offerSide bid or ask side of the Renko bar
	 * @param brickSize price range of the Renko bar
	 * @param shift number of bar back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
	 * @return Renko bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 */
	IRenkoBar getRenkoBar(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int shift) throws JFException;

    /**
     * Returns Renko Bars for specified instrument, offer side and brick size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Renko Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     */
	List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to) throws JFException;

	/**
     * Returns Renko Bars for specified instrument, offer side and brick size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is not included in result
     * 
     * Subscribe to Renko Bar live notification first, before call this method.
     *
	 * @param instrument instrument of the Renko bars
	 * @param offerSide offer side of the Renko bars
	 * @param brickSize price range of the Renko bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 */
	List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Renko Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;

	/**
     * Reads Renko Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is > 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRenkoBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
}
