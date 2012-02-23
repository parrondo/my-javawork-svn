/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Dimension;
import java.awt.Font;

public interface IWidgetChartObject extends IFillableChartObject {
    
    /**
     * String constant to be used in PropertyChangeListener
     */
    public static final String PROPERTY_WIDGET_POSX = "widget.posx";
    /**
     * String constant to be used in PropertyChangeListener
     */
    public static final String PROPERTY_WIDGET_POSY = "widget.posy";
    
    /**
     * Checks whether widget header is visible or not.
     * @return
     */
    boolean isHeaderVisible();
    
    /**
     * Sets widget header visibility status.
     * @param visible
     */
    void setHeaderVisible(boolean visible);
    
    /**
     * Returns current position of Widgets left border on chart.
     * @return value 0.0f - 1.0f
     */
    float getPosX();
    
    /**
     * Sets position of Widgets left border on chart.
     * @param value  range: 0.0f - 1.0f
     */
    void setPosX(float value);
    
    /**
     * Returns current position of Widgets upper border on chart. In proportion from top to bottom.
     * @return value 0.0f - 1.0f
     */
    float getPosY();
    
    /**
     * Sets position of Widgets upper border on chart. In proportion from top to bottom.
     * @param value  range: 0.0f - 1.0f
     */
    public void setPosY(float value);
    
    /**
     * Returns current size of Widget
     */
    Dimension getSize();
    
    /**
     * Sets preferred size for Widget. It could be adjusted automatically to display all values correctly.
     * @param dimension
     */
    void setPreferredSize(Dimension dimension);
    
    /**
     * Returns Widget default font settings.
     */
    Font getFont();
    
    /**
     * Sets default font settings for Widget.
     * @param f
     */
    void setFont(Font f);
}
