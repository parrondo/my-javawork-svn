/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Map;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * Created by: S.Vishnyakov
 * Date: Jul 23, 2009
 * Time: 11:48:57 AM
 */
public class HeikinAshiIndicator implements IIndicator, IDrawingIndicator {
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private Object[][] outputs = new Object[1][];  

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("HeikinAshi", "Heikin Ashi", "", true, false, false, 1, 0, 1);
        indicatorInfo.setRecalculateAll(true);
        inputParameterInfos = new InputParameterInfo[]{
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        outputParameterInfos = new OutputParameterInfo[]{
    		new OutputParameterInfo("HeikinAshi", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE) {{
	            setColor(Color.BLUE.darker());
	            setDrawnByIndicator(true);
    		}}
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] firstCandle = new double[4];
        firstCandle[OPEN] = inputs[0][0][startIndex - 1];
        firstCandle[CLOSE] = (inputs[0][0][startIndex - 1] + inputs[0][1][startIndex - 1] + inputs[0][2][startIndex - 1] + inputs[0][3][startIndex - 1]) / 4;
        firstCandle[HIGH] = Math.max(Math.max(inputs[0][2][startIndex - 1], firstCandle[OPEN]), firstCandle[CLOSE]);
        firstCandle[LOW] = Math.min(Math.min(inputs[0][3][startIndex - 1], firstCandle[OPEN]), firstCandle[CLOSE]);
        
        int resIndex = 0;
        for (int i = startIndex; i <= endIndex; i++, resIndex++) {
        	double[] prevCandle = i == startIndex ? firstCandle : (double[])outputs[0][resIndex - 1];
        	double[] newCandle = new double[4];
        	newCandle[OPEN] = (prevCandle[OPEN] + prevCandle[CLOSE]) / 2;
			newCandle[CLOSE] = (inputs[0][0][i] + inputs[0][1][i] + inputs[0][2][i] + inputs[0][3][i]) / 4;
			newCandle[HIGH] = Math.max(Math.max(inputs[0][2][i], newCandle[OPEN]), newCandle[CLOSE]);
			newCandle[LOW] = Math.min(Math.min(inputs[0][3][i], newCandle[OPEN]), newCandle[CLOSE]);
            outputs[0][resIndex] = newCandle;
        }
        return new IndicatorResult(startIndex, resIndex);
    }

    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke, IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes, Map<Color, java.util.List<Point>> handles) {
        Object[] values = (Object[]) values2;
        if (values2 != null) {
            // check for visible candles
            for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(), j =
                    0; j < indicatorDrawingSupport.getNumberOfCandlesOnScreen(); i++, j++) {
                if (values[i] != null) {
                    g.setColor(color);
                    double[] candle = (double[]) values[i];
                    int candleMiddle = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                    // draw Max
                    if (candle[OPEN] > candle[CLOSE]) {
                        g.drawLine(candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[HIGH]), candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]));
                    } else {
                        g.drawLine(candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[HIGH]), candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]));
                    }
                    // draw Min
                    if (candle[OPEN] < candle[CLOSE]) {
                        g.drawLine(candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[LOW]), candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]));
                    } else {
                        g.drawLine(candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[LOW]), candleMiddle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]));
                    }
                    // draw middle horizontal
                    int halfCandle = (int) indicatorDrawingSupport.getCandleWidthInPixels() / 2;
                    g.drawLine(candleMiddle - halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]), candleMiddle + halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]));
                    g.drawLine(candleMiddle - halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]), candleMiddle + halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]));
                    // draw middle vertical
                    g.drawLine(candleMiddle - halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]), candleMiddle - halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]));
                    g.drawLine(candleMiddle + halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]), candleMiddle + halfCandle, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]));

                    Color fillColor;
                    // fill the middle of candle
                    if (candle[OPEN] > candle[CLOSE]) {
                        fillColor = new Color(252, 239, 150);
                        g.setColor(fillColor);
                        g.fillRect(candleMiddle - halfCandle + 1, (int) indicatorDrawingSupport.getYForValue(candle[OPEN]) + 1, (int) indicatorDrawingSupport.getCandleWidthInPixels() - 2, (int) Math.abs(indicatorDrawingSupport.getYForValue(candle[OPEN]) - indicatorDrawingSupport.getYForValue(candle[CLOSE])) - 1);
                    } else {
                        fillColor = new Color(7, 139, 248);
                        g.setColor(fillColor);
                        g.fillRect(candleMiddle - halfCandle + 1, (int) indicatorDrawingSupport.getYForValue(candle[CLOSE]) + 1, (int) indicatorDrawingSupport.getCandleWidthInPixels() - 2, (int) Math.abs(indicatorDrawingSupport.getYForValue(candle[OPEN]) - indicatorDrawingSupport.getYForValue(candle[CLOSE])) - 1);
                    }
                }
            }
        }
        return null;
    }

    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) {
        if (index <= inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    public int getLookback() {
        return 1;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[0] = (double[][]) array;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        return null;
    }

    public void setOptInputParameter(int index, Object value) {

    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (Object[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}