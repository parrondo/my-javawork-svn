/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Point;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;

/**
 * Defines method that will be called for outputs with drawnByIndicator attribute set to true
 *
 * @author Dmitry Shohov
 */
public interface IDrawingIndicator {
    /**
     * This method will be called once for every output that marked as drawnByIndicator, every time when chart surface needs to be redrawn
     *
     * @param g graphical context
     * @param outputIdx index of the output parameter
     * @param values array of the values (int[], double[] or Object[] depending of the type of the output)
     * @param color color that was choosed by the user when addind indicator
     * @param stroke stroke that was choosed by the user when adding indicator
     * @param indicatorDrawingSupport provides information about candles positions indexes etc
     * @param shapes graphical shapes can be added to this list. This makes it possible to show correct popup when user right clicks on the chart over one of the shapes in this list
     * @param handles cooridinate points with its colors can be added to this list.
     * @return X-Y coordinates of last point drawn on the graphics. This point
     * will be used drawing last value marker on chart. The marker will not be
     * drawn if <code>null</code> returned.
     */
    public Point drawOutput(Graphics g, int outputIdx, Object values, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes,
                           Map<Color, List<Point>> handles);
}
