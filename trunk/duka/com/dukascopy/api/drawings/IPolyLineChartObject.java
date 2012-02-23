/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;


public interface IPolyLineChartObject extends IChartDependentChartObject {

    /**
     * maximum number of points allowed
     */
    int MAX_POINTS_COUNT = 150;
    
    /**
     * Appends new point to end of the line
     * 
     * @param time
     * @param price
     * @return boolean true if point appended successfully, otherwise false if reached max number of points
     * @see IPolyLineChartObject.MAX_POINTS_COUNT
     */
    boolean addNewPoint(long time, double price);

    /**
     * Removes point at specified position
     * @param index
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    void removePoint(int index);
}
