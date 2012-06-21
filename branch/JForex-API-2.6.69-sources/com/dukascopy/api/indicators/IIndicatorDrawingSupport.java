/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Color;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

/**
 * Various helper methods to be able to draw indicators
 *
 * @author Dmitry Shohov
 */
public interface IIndicatorDrawingSupport {

    /**
     * Returns true if Graphics passed to drawOutput method belong to main chart (where candles are shown) or false
     * if it belongs to separate panel created for this indicator (when IndicatorInfo.isOverChart returns false).
     *
     * @return true for main chart and false for indicator panel under chart
     */
    public boolean isChartPanel();

    /**
     * Returns number of candles currently visible on the screen. This values is usually less than size of the array with values and array with candles
     *
     * @return number of candles currently visible on the screen
     */
    public int getNumberOfCandlesOnScreen();

    /**
     * Index in the array of values and candles. Points to the first candle that is visible on the screen.
     * Depending on type of the output (simple line for example) it may be a good idea to draw only values starting
     * from the candle before first visible candle and to the candle after last visible candle
     *
     * @return index of the first visible candle in the array of values and candles
     */
    public int getIndexOfFirstCandleOnScreen();

    /**
     * Candle width in pixels
     *
     * @return candle width in pixels
     */
    public float getCandleWidthInPixels();

    /**
     * Returns number of pixels between two candles
     *
     * @return number of pixels between two candles
     */
    public float getSpaceBetweenCandlesInPixels();

    /**
     * X coordinate of the candle with index
     *
     * @param index index of the candles or/and value
     * @return x coordinate of the candle
     */
    public float getMiddleOfCandle(int index);

    /**
     * Returns Y coordinate for the specified value. Coordinate is calculated depending on minimum and maximum values for indicator
     *
     * @param value indicator value
     * @return y coordinate for the specified value
     */
    public float getYForValue(double value);
    
    /**
     * Return X coordinate for the specified time. Coordinate is calculated depending on data sequence currently displayed on chart 
     * 
     * @param time
     * @return x coordinate for the specified time
     */
    public int getXForTime(long time);

    /**
     * Returns Y coordinate for the specified value. Coordinate is calculated depending on minimum and maximum values for indicator
     *
     * @param value indicator value
     * @return y coordinate for the specified value
     */
    public float getYForValue(int value);

    /**
     * Returns array of candles. Each one of the candle corresponds to the value with the same index in the array of values for the current output
     *
     * @return array of candles with the same size as the size of the output values array
     */
    public IBar[] getCandles();

    /**
     * Returns true if the last candle is not fully formed yet
     *
     * @return true if last candle is in-progress candle
     */
    public boolean isLastCandleInProgress();

    /**
     * Returns width of the panel in pixels where drawing should happen
     *
     * @return width of the chart
     */
    public int getChartWidth();

    /**
     * Returns height of the panel in pixels where drawing should happen
     *
     * @return height of the chart
     */
    public int getChartHeight();

    /**
     * Returns instrument of the chart
     *
     * @return instrument of the chart
     */
    public Instrument getInstrument();

    /**
     * Returns period of the chart
     *
     * @return period of the chart
     */
    public Period getPeriod();

    /**
     * Return offer side of the chart or null if this is ticks chart
     *
     * @return offer side of the chart or null if this is ticks chart
     */
    public OfferSide getOfferSide();
    
    /**
     * Returns downtrend color for this indicator
     * 
     * @return downtrend color for this indicator
     */
    Color getDowntrendColor();
}
