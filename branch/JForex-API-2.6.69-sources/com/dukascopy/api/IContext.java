/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * 
 */
package com.dukascopy.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.dukascopy.api.feed.IBarFeedListener;
import com.dukascopy.api.feed.IPointAndFigureFeedListener;
import com.dukascopy.api.feed.IRangeBarFeedListener;
import com.dukascopy.api.feed.IRenkoBarFeedListener;
import com.dukascopy.api.feed.ITickBarFeedListener;

/**
 * Gives access to the various parts of the system
 * 
 * @author Denis Larka, Dmitry Shohov
 */
public interface IContext {
    /**
     * Returns interface of the main engine (order submitting, merging etc)
     * 
     * @return main engine
     */
    IEngine getEngine();
    
    /**
     * Returns first chart for specified instrument. If there is more than one chart for this instrument, then method returns one of them,
     * but always the same.
     * 
     * @param instrument currency pair
     * @return chart
     */
    IChart getChart(Instrument instrument);

    /**
     * Returns set of charts for specified instrument.
     * 
     * @param instrument currency pair
     * @return <code>Set&lt;IChart&gt;</code>
     */
    Set<IChart> getCharts(Instrument instrument);

    /**
     * @return corresponding {@link IChart} object of the last active chart, i.e., the chart which last had focus
     */
    IChart getLastActiveChart();
    
    /**
     * Returns an interface which provides control
     * to custom strategy tabs in the main and
     * bottom panels
     * @return singleton instance of the IUserInterface
     */
    IUserInterface getUserInterface();
    
    /**
     * Returns interface that allows access to history data
     * 
     * @return interface for history access
     */
    IHistory getHistory();
    
    /**
     * Returns interface that allows to write messages into the Messages table
     * 
     * @return interface for messages sending
     */
    IConsole getConsole();
    
    /**
     * Returns interface that allows to calculate indicator values
     * 
     * @return interface for indicator calculations
     */
    IIndicators getIndicators();

    /**
     * Returns last known state of the account info. This state is updated once in 5 seconds and can be inaccurate
     * if significant price changes happen on the market
     *
     * @return account
     */
    IAccount getAccount();

    /**
     * Returns interface that allows to start/stop and control other strategies
     * @return
     */
    IStrategies getStrategies();
    
    /**
     * Returns interface that allows to handle downloadable strategies
     * @return interface for downloadable strategies handling
     */
    IDownloadableStrategies getDownloadableStrategies();
    
    /**
     * Returns interface with JForex utility methods, e.g., currency converter
     * @return
     */
    JFUtils getUtils();
    
    /**
     * Returns interface that allows access to system data. 
     * @return interface for system data
     */
    IDataService getDataService();

    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Method returns fast after invoking and doesn't  wait for subscription will be done.
     * Also locks the instruments so that user could not unsubscribe while strategy is running.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.</br>
     * Equivalent to {@link #setSubscribedInstruments(Set, boolean)} with lock set as <tt>false</tt>
     * @param instruments set of instruments, that strategy needs for it's work
     */
    void setSubscribedInstruments(Set<Instrument> instruments);
    
    
    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Also locks the instruments so that user could not unsubscribe while strategy is running.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.
     *
     * @param instruments set of instruments, that strategy needs for it's work
     * @param lock <code>false</code> - method returns fast after invoking and doesn't wait for subscription will be done. <code>true</code> - otherwise. 
     */
    void setSubscribedInstruments(Set<Instrument> instruments, boolean lock);
    
    
    /**
     * Returns set of the currently subscribed instruments
     * 
     * @return set of the subscribed instruments
     */
    Set<Instrument> getSubscribedInstruments();

    /**
     * Stops strategy execution. 
     * Current task will be completed.
     * This method doesn't work in IStrategy.onStart()
     */
    void stop();
    
    /**
     * Returns true if user requested to stop the strategy.
     * Can be used to check if strategy was stopped (for example from non strategy threads running in parallel)
     *
     * @return true if strategy stop was requested 
     */
    boolean isStopped();
    
    /**
     * Returns true if strategy is granted full access. 
     * 
     * @return true of false depending on access level
     */
    boolean isFullAccessGranted();
   
    /**
     * Every strategy executes in it's own thread. This will ensure single threaded model: Any handle method of {@link IStrategy}
     * will be executed in order.
     * Submission of orders can be called only from this thread.
     * If some critical action like submitting order needs to be called from another thread, you need to use this method to access
     * strategy thread. For instance:
     * <pre>
     * Thread thread = new Thread(new Runnable() {
     *      public void run() {
     *          try {
     *              context.executeTask(task);
     *          } catch (Exception e) {
     *              console.getErr().println(Thread.currentThread().getName() + " " + e);
     *          }
     *      }
     *  });
     * thread.start();
     * </pre>    
     * @param <T> type of the return value
     * @param callable task to execute
     * @return <code>Future&lt;T&gt;</code> that can be used to get result of execution 
     */
    <T> Future<T> executeTask(Callable<T> callable);

    /**
	 * Returns directory where reading and writing is allowed. Usually ~/My Documents/My Strategies/files
     * 
     * @return directory with free read/write access
	 */
    File getFilesDir();

    /**
     * Pauses historical testing, doesn't have any effect if not in historical tester
     */
    void pause();
    
    /**
     * Subscribes passed listener on bars feed notification by passed instrument, period and offer side.
     * 
     * @param instrument {@link Instrument} of bars to listen
     * @param period {@link Period} period of bars to listen (Tick period is not supported).
     * @param offerSide {@link OfferSide} of bars to listen
     * @param listener {@link IBarFeedListener} listener
     */
    void subscribeToBarsFeed(
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			IBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from bars feed notification
     * 
     * @param listener
     */
    void unsubscribeFromBarsFeed(IBarFeedListener listener);
    
    /**
     * Subscribes passed listener on range bars feed notification by passed instrument, offer side and price range
     * 
     * @param instrument {@link Instrument} of bars to listen
     * @param offerSide {@link OfferSide} of bars to listen
     * @param priceRange {@link PriceRange} of bars to listen
     * @param listener {@link IRangeBarFeedListener} listener
     */
    void subscribeToRangeBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange priceRange,
    		IRangeBarFeedListener listener
    );
    
    /**
     * Unsubscribes passed listener from range bars feed notification
     * 
     * @param listener
     */
    void unsubscribeFromRangeBarFeed(IRangeBarFeedListener listener);
    
    /**
     * Subscribes passed listener on point and figure feed notification by passed instrument, offer side, price range and reversal amount
     * 
     * @param instrument {@link Instrument} of P&Fs to listen
     * @param offerSide {@link OfferSide} of P&Fs to listen
     * @param priceRange {@link PriceRange} of P&Fs to listen
     * @param reversalAmount {@link ReversalAmount} of P&Fs to listen
     * @param listener {@link IPointAndFigureFeedListener} listener
     */
    void subscribeToPointAndFigureFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange priceRange,
    		ReversalAmount reversalAmount,
    		IPointAndFigureFeedListener listener
    );

    /**
     * Unsubscribes passed listener from point and figure feed notification
     * 
     * @param listener
     */
    void unsubscribeFromPointAndFigureFeed(IPointAndFigureFeedListener listener);
    
    /**
     * Subscribes passed listener on tick bar feed notification by passed instrument, offer side and tick bar size
     *  
     * @param instrument {@link Instrument} of tick bars to listen
     * @param offerSide {@link OfferSide} of tick bars to listen
     * @param tickBarSize {@link TickBarSize} of tick bars to listen
     * @param listener {@link ITickBarFeedListener} listener
     */
    void subscribeToTickBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		TickBarSize tickBarSize,
    		ITickBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from tick bar feed notification
     * 
     * @param listener
     */
    void unsubscribeFromTickBarFeed(ITickBarFeedListener listener);
    
    
    /**
     * Subscribes passed listener on renko bars feed notification by passed instrument, offer side and brick size
     * 
     * @param instrument {@link Instrument} of renko bars to listen
     * @param offerSide {@link OfferSide} of renko bars to listen
     * @param brickSize {@link PriceRange brick size} of renko bars to listen 
     * @param listener {@link IRenkoBarFeedListener} listener
     */
    void subscribeToRenkoBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange brickSize,
    		IRenkoBarFeedListener listener
    );
    
    /**
     * Unsubscribes passed listener from renko bars feed notification
     * 
     * @param listener
     */
    void unsubscribeFromRenkoBarFeed(IRenkoBarFeedListener listener);

    
    /**
     * Register a listener for a strategy configuration parameter which is preceded by a {@link Configurable} annotation.
     * Listener will listen changes of specified property for running strategy.
     * @param parameter either the name of strategy field which is preceded by a {@link Configurable} annotation or
     *                  {@link Configurable#value()} - the assigned configurable parameter name that appears besides 
     *                  its value in GUI.
     * @param listener Listener which will get {@link PropertyChangeEvent}s
     */
    void addConfigurationChangeListener(String parameter, PropertyChangeListener listener);
    /**
     * Unregister a listener for a certain configuration parameter which is preceded by a {@link Configurable} annotation.
     * @param parameter either the name of strategy field which is preceded by a {@link Configurable} annotation or
     *                  {@link Configurable#value()} - the assigned configurable parameter name that appears besides 
     *                  its value in GUI.
     * @param listener Listener which will get {@link PropertyChangeEvent}s
     */
    void removeConfigurationChangeListener(String parameter, PropertyChangeListener listener);
    
}
