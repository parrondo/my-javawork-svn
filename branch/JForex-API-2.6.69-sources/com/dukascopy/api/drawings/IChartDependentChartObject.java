/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import com.dukascopy.api.IChartObject;

public interface IChartDependentChartObject extends IChartObject {

    /**
     * Sets time value for figure edge specified by <code>pointIndex</code>.
     * 
     * @param pointIndex
     *            - index of figure edge.
     * @param timeValue
     *            - time value.
     * @exception IllegalArgumentException
     *                - if <code>pointIndex</code> specified is invalid.
     */
    void setTime(int pointIndex, long timeValue);

    /**
     * Sets price value for figure edge specified by <code>pointIndex</code>.
     * 
     * @param pointIndex
     *            - index of figure edge.
     * @param priceValue
     *            - price value.
     * @exception IllegalArgumentException
     *                - if <code>pointIndex</code> specified is invalid.
     */
    void setPrice(int pointIndex, double priceValue);

    /**
     * Moves first point of the object to the new position dragging whole object
     * with all other points. Do not initiate chart repaint immediately, use
     * IChart.repaint() method if necessary.
     * 
     * @param time
     *            new time of the first point
     * @param price
     *            new price of the first point
     */
    @Override
    void move(long time, double price);
    
    
    /**
     * Returns stick to candle/bar time option state on X axis while drawing. Actual only for uniform periods.
     * System default value: <b>true</b>
     */
    boolean isStickToCandleTimeEnabled();
    
    /**
     * Sets stick to candle/bar time option. Actual only for uniform periods.
     * System default value: <b>true</b>
     * @param enabled
     */
    void setStickToCandleTimeEnabled(boolean enabled);

}
