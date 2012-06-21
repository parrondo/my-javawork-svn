/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * Allows to draw graphical objects on the chart
 *
 * @author Denis Larka
 */
public interface IChart extends Iterable<IChartObject> {

    /**
     * Type of the graphical object
     */
    public enum Type {
        //TODO: i'm almost sure there is mistakes in object descriptions
        /**
         * Vertical line
         */
        VLINE,
        /**
         * Horizontal line
         */
        HLINE,

        /**
         * @deprecated use PRICEMARKER instead
         */
        ORDER_LINE,

        /**
         * Vertical line with time text
         */
        TIMEMARKER,
        
        /**
         * Horizontally resizable rectangle between two time points
         */
        TIMERANGE,
        
        /**
         * Horizontal line with price text
         */
        PRICEMARKER,

        /**
         * Multiple line between many points
         */
        POLY_LINE,

        /**
         * Short line between two points
         */
        SHORT_LINE,
        /**
         * Long line drawn through two points
         */
        LONG_LINE,
        /**
         * Ray line drawn through two points. Finite in one direction and infinite in the other.
         */
        RAY_LINE,

        /**
         * @deprecated isn't supported
         */
        TREND,
        /**
         * Trend line by angle
         */
        TRENDBYANGLE,

        /**
         * Fibonacci channel lines
         */
        FIBOCHANNEL,
        /**
         * Linear regression
         */
        REGRESSION,
        /**
         * Equidistant channel lines
         */
        CHANNEL,
        
        /**
         * Standard deviation
         */
        STDDEVCHANNEL,

        /**
         * Gann line
         */
        GANNLINE,
        /**
         * Gann fan
         */
        GANNFAN,
        /**
         * Gann grid
         */
        GANNGRID,

        /**
         * Retracement
         */
        FIBO,
        /**
         * Time zones
         */
        FIBOTIMES,
        /**
         * Fan
         */
        FIBOFAN,
        /**
         * Arcs
         */
        FIBOARC,
        /**
         * Expansion
         */
        EXPANSION,

        /**
         * Rectangle
         */
        RECTANGLE,
        /**
         * Triangle
         */
        TRIANGLE,
        /**
         * Ellipse
         */
        ELLIPSE,

        /**
         * Andrew's pitchfork
         */
        PITCHFORK,
        /**
         * Cycle lines (periods)
         */
        CYCLES,
        /**
         * Percent (horizontal)
         */
        PERCENT,

        /**
         * Text
         */
        TEXT,
        /**
         * Text label
         */
        LABEL,
        
        /**
         * Arrow up
         */
        SIGNAL_UP,
        /**
         * Arrow down
         */
        SIGNAL_DOWN,
        
        /**
         * Widget displaying OHLC data
         */
        OHLC_INFORMER,
        
        /**
         * Widget for searching and displaying patterns on chart
         */
        PATTERN_WIDGET,

    }

    /**
     * Draws graphical object that requires up to 3 points
     * 
     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @param time3 time of third point
     * @param price3 price of third point
     * @return graphical object 
     */
    @Deprecated
    public IChartObject draw(String key, Type type, long time1, double price1, long time2, double price2, long time3, double price3);

    /**
     * Draws graphical object that requires up to 2 points
     * 
     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @return graphical object 
     */
    @Deprecated
    public IChartObject draw(String key, Type type, long time1, double price1, long time2, double price2);

    /**
     * Draws graphical object that requires only 1 point

     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * 
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of the point
     * @param price1 price of the point
     * @return graphical object
     */
    @Deprecated
    public IChartObject draw(String key, Type type, long time1, double price1);

    /**
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * Draws graphical object that requires up to 3 points. Object can be selected, moved and changed by the user
     *
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @param time3 time of third point
     * @param price3 price of third point
     * @return graphical object
     */
    @Deprecated
    public IChartObject drawUnlocked(String key, Type type, long time1, double price1, long time2, double price2, long time3, double price3);

    /**
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * Draws graphical object that requires up to 2 points. Object can be selected, moved and changed by the user
     *
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @return graphical object
     */
    @Deprecated
    public IChartObject drawUnlocked(String key, Type type, long time1, double price1, long time2, double price2);

    /**
     * Draws graphical object that requires only 1 point. Object can be selected, moved and changed by the user
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of the point
     * @param price1 price of the point
     * @return graphical object
     */
    @Deprecated
    public IChartObject drawUnlocked(String key, Type type, long time1, double price1);

    /**
     * Moves graphical object to new coordinates.
     * Do not initiate chart repaint immediately, use IChart.repaint() method if necessary.
     *
     * @param objectToMove chart object to move
     * @param newTime new time coordinate
     * @param newPrice new price coordinate
     */
    public void move(IChartObject objectToMove, long newTime, double newPrice);

    /**
     * Moves graphical object to new coordinates.
     * Do not initiate chart repaint immediately, use IChart.repaint() method if necessary.
     *
     * @param chartObjectKey key of the chart object to move
     * @param newTime new time coordinate
     * @param newPrice new price coordinate
     */
    public void move(String chartObjectKey, long newTime, double newPrice);

    /**
     * Writes a comment in the upper left corner. Line is
     * splitted by new line characters
     *
     * @param comment string to display
     */
    public void comment(String comment);

    /**
     * Sets the horizontal position of the comment's text.
     *
     * @param position  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>
     * @exception IllegalArgumentException
     *
     * @see {@link #getCommentHorizontalPosition()}
     * @see java.swing.SwingConstants
     */
    public void setCommentHorizontalPosition(int position);

    /**
     * Returns the horizontal position of the comment's text.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, 
     *           <code>RIGHT</code>.
     * 
     * @see #setCommentHorizontalPosition(int)
     */
    public int getCommentHorizontalPosition();

    /**
     * Sets the vertical position of the comment's text.
     *
     * @param position  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>,
     *           <code>BOTTM</code>
     * @exception IllegalArgumentException
     *
     * @see #getCommentVerticalPosition()
     * @see javax.swing.SwingConstants
     */    
    public void setCommentVerticalPosition(int position);

    /**
     * Returns the vertical position of the comment's text.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>, 
     *           <code>BOTTOM</code>.
     *
     * @see #setCommentVerticalPosition(int)
     */    
    public int getCommentVerticalPosition();

    /**
     * Sets the comment's font.
     *
     * @param font the desired <code>Font</code> for comment
     *
     * @see java.awt.Component#getFont()
     */
    public void setCommentFont(Font font);

    /**
     * Gets the comment's font.
     * @return comment's font; if a font has not been set for comment,
     * then the font defined in chart's theme for drawings is returned
     *
     * @see #setCommentFont(Font)
     */
    public Font getCommentFont();

    /**
     * Sets the foreground color of comment.
     * @param color the color to become comment's foreground color
     * if this parameter is <code>null</code>, then used default text font defined in chart's theme
     *
     * @see #getCommentColor()
     */
    public void setCommentColor(Color color);

    /**
     * Gets the foreground color of comment.
     * @return comments's foreground color
     * if comment does not have a foreground color, then used default text font defined in chart's theme
     *
     * @see #setCommentColor(Color) 
     */
    public Color getCommentColor();

    /**
     * Returns graphical object by key
     * 
     * @param key unique id of the object
     * @return graphical object or null if no object was found by specified key
     */
    public IChartObject get(String key);

    /**
     * Deletes and returns graphical object by key
     * 
     * @param key unique id of the object
     * @return graphical object that was deleted or null if no object was found by specified key
     */
    public IChartObject remove(String key);
    
    /**
     * Deletes graphical object
     * 
     * @param chartObject
     */
    public void remove(IChartObject chartObject);

    /**
     * Deletes graphical objects
     * 
     * @param chartObjects
     * @return list of all graphical objects that were actually removed from chart
     */
    public List<IChartObject> remove(List<IChartObject> chartObjects);
    
    /**
     * Returns all graphical objects
     * 
     * @return list of all graphical objects on the chart
     */
    public List<IChartObject> getAll();

    /**
     * Removes all graphical objects on the chart
     */
    public void removeAll();

    /**
     * Returns number of the graphical objects on the chart
     * 
     * @return number of the graphical objects on the chart
     */
    public int size();


    /**
     * Returns minimal value of the vertical scale of the specified
     * subwindow of the current chart (0-main chart window, the indicators'
     * subwindows are numbered starting from 1). If the subwindow index
     * has not been specified, the minimal value of the price scale
     * of the main chart window is returned.
     * @param index of the (sub-)window
     * @return minimal value of the vertical scale
     */
    public double priceMin(int index);


    /**
     * Returns maximal value of the vertical scale of the specified
     * subwindow of the current chart (0-main chart window, the indicators'
     * subwindows are numbered starting from 1). If the subwindow index
     * has not been specified, the maximal value of the price scale
     * of the main chart window is returned.
     * @param index of the (sub-)window
     * @return maximal value of the vertical scale
     */
    public double priceMax(int index);
    
    /**
     * Sets minimal and maximal value of the vertical scale of
     * the current chart when it is maximally expanded.
     * Note: automatically switches off chart price range autoscaling.
     * @param minPriceValue minimal value of the vertical scale
     * @param maxpriceValue maximal value of the vertical scale
     */
    void setVerticalAxisScale(double minPriceValue, double maxPriceValue);

    /**
     * Switches on/off chart vertical autoscale mode.
     * When autoscale switched off chart is draggable vertically
     * @param autoscale
     */
    void setVerticalAutoscale(boolean autoscale);
    
    /**
     * Returns amount of bars visible on the screen
     * @return amount of bars visible on the screen
     */
    public int getBarsCount();


    /**
     * Returns count of indicator windows on the chart (including main chart).
     * @return total count of chart windows (main window + indicator subwindows)
     */
    public int windowsTotal();

    /**
     * Returns instrument of the chart
     * 
     * @return instrument of the chart
     * @see #getFeedDescriptor()
     */
    public Instrument getInstrument();

    /**
     * Sets chart's instrument
     * @param instrument
     * @deprecated 
     */
    public void setInstrument(Instrument instrument);

    /**
     * Returns selected period
     * 
     * @return selected period
     * @see #getFeedDescriptor()
     */
    public Period getSelectedPeriod();

    /**
     * Returns selected offer side
     * 
     * @return selected offer side
     * @see #getFeedDescriptor()
     */
    public OfferSide getSelectedOfferSide();

    /**
     * Add indicator to the chart
     * 
     * @param indicator as <code>IIndicator</code>
     */
    public void addIndicator(IIndicator indicator);

    /**
     * Add indicator to the chart by specifying initial optional parameters values
     * If optParams equals to null - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     */
    public void addIndicator(IIndicator indicator, Object[] optParams);
    
    /**
     * 
     * Add indicator to the chart by specifying initial optional parameters values, curves colors, drawing styles and line widths
     * If output params are nulls - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     */
    public void addIndicator(
    		IIndicator indicator,
    		Object[] optParams,
    		Color[] outputColors,
    		DrawingStyle[] outputDrawingStyles,
    		int[] outputWidths
    );
    
    /**
     * Add sub indicator
     * 
     * @param subChartId
     * @param indicator as <code>IIndicator</code>
     */
    public void addSubIndicator(Integer subChartId, IIndicator indicator);

    /**
     * Remove specified indicator from the chart
     * @param indicator as <code>IIndicator</code>
     */
    public void removeIndicator(IIndicator indicator);

    /**
     * Return all chart's indicators
     * 
     * @return List<IIndicator>
     */
    public List<IIndicator> getIndicators();

    /**
     * Returns selected Data Type
     * 
     * @return selected Data Type
     * @see #getFeedDescriptor()
     */
    public DataType getDataType();
    
    /**
     * The method returns currently selected Price Range on chart
     * 
     * @return selected Price Range
     * @see #getFeedDescriptor()
     */
    public PriceRange getPriceRange();
    
    /**
     * The method returns currently selected Reversal Amount on the Point And Figure chart
     * For non P&F charts null will be returned
     * 
     * @return selected Reversal Amount
     * @see #getFeedDescriptor()
     */
    public ReversalAmount getReversalAmount();
    
    /**
     * Returns current filter
     * 
     * @return filter
     * @see #getFeedDescriptor()
     */
    public Filter getFilter();
    
    /**
     * Returns chart state described by bean {@link IFeedDescriptor}
     * @return chart state described by bean {@link IFeedDescriptor}
     */
    public IFeedDescriptor getFeedDescriptor();    
    
    /**
     * Refresh and repaint chart
     */
    public void repaint();
   
    /**
     * Returns <code>IChartObjectFactory</code> instance. This factory provides convenience methods to create
     * various graphics objects.
     * @return <code>IChartObjectFactory</code> instance.
     */
    IChartObjectFactory getChartObjectFactory();
    
	/**
	 * Adds object of <code>IChartObject</code> super type to main chart window. 
	 * Please note that only <b>one</b> instance of object can be added per main chart window.
	 * Note: if chart object with the same key already presents on chart it will be replaced with current one.
	 * 
	 * @param object - instance of <code>IChartObject</code> super type.
	 * 
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of main charts.
	 */
	<T extends IChartObject> void addToMainChart(T object);
	
	/**
	 * Adds object of <code>IChartObject</code> super type to sub chart window with specified <code>subChartId</code> and <code>indicatorId</code>. 
	 * Please note that only <b>one</b> instance of object can be added per sub chart window. 
	 * 
	 * 
	 * @param subChartId - sub chart id.
	 * @param indicatorId - indicator function id.
	 * @param object - instance of <code>IChartObject</code> super type.
	 * 
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of sub charts.
	 */
	<T extends IChartObject> void addToSubChart(Integer subChartId, int indicatorId, T object);
	
	/**
	 * Adds object of <code>IChartObject</code> super type to unlocked objects pool of main chart window. 
	 * Please note that only <b>one</b> instance of object can be added to unlocked objects pool per main chart window. 
	 * 
	 * @param object - instance of <code>IChartObject</code> super type.
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of main charts unlocked objects pool.
	 * 
	 * @deprecated Please, use method @link {@link IChart#addToMainChart(IChartObject)} There is no objects division onto locked and unlocked anymore
	 */
	<T extends IChartObject> void addToMainChartUnlocked(T object);

	/**
	 * Checks whether <code>chartObject</code> is unlocked or not. Returns <code>null</code> if chart does not contain specified <code>chartObject</code>
	 * 
	 * @param chartObject
	 * @return <code>true</code> - <code>chartObject</code> is unlocked, <code>false</code> - if not, <code>null</code> - <code>chartObject</code> does not belong to <code>this</code> chart.
	 * 
	 * @deprecated There is no objects division onto locked and unlocked anymore, so this method will always return false
	 */
	Boolean isChartObjectUnlocked(IChartObject chartObject);
	
    /**
     * The method returns currently selected Trade Bar Size on Tick Bar chart
     * For non Tick Bar charts null might be returned
     * 
     * @return selected Tick Bar Size
     * @see #getFeedDescriptor()
     */
	public TickBarSize getTickBarSize();

	/**
	 * The method returns List of chart objects created by current strategy
	 * Empty list is returned if no objects were created
	 * Null is never returned
	 * 
	 * @return chart objects that were create by current strategy
	 */
	public List<IChartObject> getStrategyChartObjects();
	
	/**
	 * Creates a {@link BufferedImage} snapshot of this chart at the given moment.
	 * @return {@link BufferedImage} snapshot of current chart at the method invocation moment.   
	 */
	public BufferedImage getImage();
	
	/**
	 * Applies DataPresentationType to current chart.
	 * Use {@link #getDataType()}.getSupportedPresentationTypes() to get all allowed values.
	 * Use {@link #getDataType()}.isPresentationTypeSupported(DataPresentationType presentationType)
	 *  to check whether current DataType supports presentationType or not.
	 * 
	 * @param presentationType one of DataPresentationType constants, supported by current DataType
	 * @see #getDataType()
	 * @throws IllegalArgumentException if DataPresentationType is not supported by current DataType
	 */
	void setDataPresentationType(DataPresentationType presentationType);
	
	/**
	 * Select specified drawing by key.
	 * Note: locked drawings cannot be selected.
	 * @see IChartObject#isLocked()
	 * @see IChartObject#setLocked(boolean)
	 * @param key
	 */
	void selectDrawing(String key);
	
	/**
	 * Select specified drawing.
     * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param chartObject
	 */
	void selectDrawing(IChartObject chartObject);
	
	/**
	 * Navigates to and selects specified drawing by key.
	 * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param key
	 */
	void navigateAndSelectDrawing(String key);
	
	/**
	 * Navigates to and selects specified drawing.
     * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param chartObject
	 */
	void navigateAndSelectDrawing(IChartObject chartObject);
}