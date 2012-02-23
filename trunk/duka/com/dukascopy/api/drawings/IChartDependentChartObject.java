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
     * @author stanislavs.rubens
     */
    public void setTime(int pointIndex, long timeValue);

    /**
     * Sets price value for figure edge specified by <code>pointIndex</code>.
     * 
     * @param pointIndex
     *            - index of figure edge.
     * @param priceValue
     *            - price value.
     * @exception IllegalArgumentException
     *                - if <code>pointIndex</code> specified is invalid.
     * @author stanislavs.rubens
     */
    public void setPrice(int pointIndex, double priceValue);

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
    public void move(long time, double price);

}
