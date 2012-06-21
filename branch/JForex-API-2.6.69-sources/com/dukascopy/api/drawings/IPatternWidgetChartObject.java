/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.util.Set;



public interface IPatternWidgetChartObject extends IWidgetChartObject {
    
    /**
     * Constant used as parameter to setup results sorting 
     */
    public static final int QUALITY = 0;
    /**
     * Constant used as parameter to setup results sorting
     */
    public static final int MAGNITUDE = 1;
    /**
     * Constant used as parameter to setup results sorting 
     */
    public static final int STARTING_TIME = 2;
    /**
     * Constant used as parameter to setup results sorting 
     */
    public static final int SIZE = 3;

    public enum Pattern {
        ASCENDING_TRIANGLE,
        DESCENDING_TRIANGLE,
        CHANNEL_DOWN,
        CHANNEL_UP,
        DOUBLE_BOTTOM,
        DOUBLE_TOP,
        HEAD_AND_SHOULDERS,
        INVERSE_HEAD_AND_SHOULDERS,
        RECTANGLE,
        @Deprecated
        INVERSE_RECTANGLE,
        FLAG,
        FALLING_WEDGE,
        RISING_WEDGE,
        TRIANGLE,
        PENNANT,
        TRIPLE_BOTTOM,
        TRIPLE_TOP
    }
    
    
    void addPattern(Pattern pattern);
    void removePattern(Pattern pattern);
    
    Set<Pattern> getPatternsToAnalyze();

    int getDesiredMinQuality();
    void setDesiredMinQuality(int desiredMinQuality);

    int getDesiredMinMagnitude();
    void setDesiredMinMagnitude(int desiredMinMagnitude);

    String getPivotPointsPrice();
    void setPivotPointsPrice(String pivotPointsPrice);

    int getSortPatternsByCriteria();
    void setSortPatternsByCriteria(int criteria);

    boolean isShowAll();
    void setShowAll(boolean showAll);
    
}
