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
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;
 //
/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * Created by: S.Vishnyakov
 * Date: Jan 7, 2009
 * Time: 4:25:31 PM
 */
public class TD_Sequential implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private int timePeriod = 4;
    private int[][] outputs = new int[2][];

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TD_S", "TD Sequential", "Overlap Studies", true, false, true, 1, 1, 2);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};
        optInputParameterInfos = new OptInputParameterInfo[]{
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(4, 2, 100, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Positive", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) {{
    			setColor(Color.BLUE);
    			setDrawnByIndicator(true);
    		}},
    		new OutputParameterInfo("Negative", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) {{
    			setColor(Color.ORANGE.darker());
    			setDrawnByIndicator(true);
    		}}
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }

        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        int resIndex;
        // 3 possible outcomes: where is days above start and end(start+size)?
        // 1: days <= start  this means first zero results were returned already
        // 2: start < days < end -> zero results have to be added res[start..days-1] to result array
        //   and [days..end] by TDSequential formula
        // 3: start < end <= days -> result array  need to be filled with zeros [start..end]=0
        if (timePeriod <= startIndex) { //1: days <= start < end
            resIndex = 0;
            for (int i = startIndex; i <= endIndex; i++, resIndex++) {
                outputs[0][resIndex] = calcTDSequential(resIndex, outputs[0], i);
                outputs[1][resIndex] = outputs[0][resIndex];
            }
        } else if (timePeriod <= endIndex) { //2:
            resIndex = 0;
            int i = startIndex;
            for (; i < timePeriod; i++, resIndex++) {
                outputs[0][resIndex] = 0;
                outputs[1][resIndex] = 0;
            }
            for (; i < endIndex; i++, resIndex++) {
                outputs[0][resIndex] = calcTDSequential(i, outputs[0], i);
                outputs[1][resIndex] = outputs[0][resIndex];
            }
        } else { //3:
            resIndex = 0;
            for (int i = startIndex; i <= endIndex; i++, resIndex++) {
                outputs[0][resIndex] = 0;
                outputs[1][resIndex] = 0;
            }
        }
        return new IndicatorResult(startIndex, resIndex);
    }

    // assume, that index > days ( otherwise ArrayIndexOutOfBoundsException will be thrown )
    // main logic for TDSequential: compare to source values: a. current & b. days before current
    // (==) -> accumulatedNumber = 0
    // (>) -> accumulatedNumber = accumulatedNumber>0 ? accumulatedNumber++ : 0
    // (<) -> accumulatedNumber = accumulatedNumber<0 ? accumulatedNumber-- : 0
    private int calcTDSequential(int index, int[] outputs, int candleIdx) {
        int accumulatedNumber = 0;
        int result;
        if (index > 0) {
            accumulatedNumber = outputs[index - 1];
        }
        // main calculation:
        double current = inputs[0][candleIdx];
        double previous = current;
        if (index >= timePeriod) {
            previous = inputs[0][candleIdx - timePeriod];
        }
                
        double priorSetupPrice = inputs[0][candleIdx - 1];
        double flipPrice = inputs[0][candleIdx - 1 - timePeriod];  

        if (accumulatedNumber == 0 && priorSetupPrice > flipPrice && current < previous){
        	result = accumulatedNumber - 1;
        }
        else if(accumulatedNumber == 0 && priorSetupPrice < flipPrice && current > previous){
        	result = accumulatedNumber + 1;
        }
        else if (accumulatedNumber != 0 && current > previous) {
            result = (accumulatedNumber > 0) ? (accumulatedNumber + 1) : 0;
        } 
        else if (accumulatedNumber != 0 && current < previous) {
            result = (accumulatedNumber < 0) ? (accumulatedNumber - 1) : 0;
        } 
        else { // (current == previous)
            result = 0;
        }
        // end of main calculation
        return result;
    }

    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes,
                           Map<Color, List<Point>> handles) {
        int[] values = (int[]) values2;
        if (values != null) {
            boolean only9s = false;
            Font defaultTD;
            Font defaultBigTD;
            String fontName = g.getFont().getName();
            if (indicatorDrawingSupport.getCandleWidthInPixels() == 1) {
                //reduce font size when showing candles as lines
                defaultTD = new Font(fontName, Font.PLAIN, 9);
                defaultBigTD = new Font(fontName, Font.BOLD, 11);
            } else if (indicatorDrawingSupport.getCandleWidthInPixels() > 20) {
                //increase font size when showing candles with width more than 20 pixels
                defaultTD = new Font(fontName, Font.PLAIN, 12);
                defaultBigTD = new Font(fontName, Font.BOLD, 15);
            } else {
                //default font
                defaultTD = new Font(fontName, Font.PLAIN, 10);
                defaultBigTD = new Font(fontName, Font.BOLD, 13);
            }
            if (indicatorDrawingSupport.getCandleWidthInPixels() == 1 && indicatorDrawingSupport.getSpaceBetweenCandlesInPixels() == 0) {
                //max zoom out
                only9s = true;
            }
            FontMetrics metricsTD = g.getFontMetrics(defaultTD);
            FontMetrics metricsBigTD = g.getFontMetrics(defaultTD);

            // check for visible candles
            for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(), j =
                    indicatorDrawingSupport.getNumberOfCandlesOnScreen(); j > 0; i++, j--) {
                if ((outputIdx == 0 && values[i] > 0) || (outputIdx == 1 && values[i] < 0)) {
                    FontMetrics metrics;
                    if (values[i] == 9 || values[i] == -9) {
                        g.setFont(defaultBigTD);
                        metrics = metricsBigTD;
                    } else if (!only9s) {
                        g.setFont(defaultTD);
                        metrics = metricsTD;
                    } else {
                        continue;
                    }
                    if (values[i] > 0) {
                        g.setColor(color);
                        String text = Integer.toString(values[i]);
                        int textWidth = metrics.stringWidth(text);
                        g.drawString(text, (int) indicatorDrawingSupport.getMiddleOfCandle(i) - (textWidth / 2),
                                (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getHigh()) - metrics.getDescent());
                    } else if (values[i] < 0) {
                        g.setColor(color);
                        String text = Integer.toString(Math.abs(values[i]));
                        int textWidth = metrics.stringWidth(text);
                        g.drawString(text, (int) indicatorDrawingSupport.getMiddleOfCandle(i) - (textWidth / 2),
                                (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getLow()) + metrics.getAscent() + (values[i] == -9 ? 2 : 0));
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
        return timePeriod + 1;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (int[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}