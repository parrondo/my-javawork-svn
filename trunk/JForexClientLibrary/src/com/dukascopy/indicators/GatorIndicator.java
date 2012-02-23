/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;

/**
 * Created by: S.Vishnyakov
 * Date: Dec 17, 2009
 */
public class GatorIndicator implements IIndicator, IDrawingIndicator {

    private IIndicator jawSmmaIndicator;
    private IIndicator teethSmmaIndicator;
    private IIndicator lipsSmmaIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[3][];
    private int jawPeriod = 13;
    private int teethPeriod = 8;
    private int lipsPeriod = 5;
    private int jawShift = 8;
    private int teethShift = 5;
    private int lipsShift = 3;

    public void onStart(IIndicatorContext context) {
        jawSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        teethSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        lipsSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");

        indicatorInfo = new IndicatorInfo("GATOR", "Gator Oscillator", "Bill Williams", false, false, true, 1, 3, 3);
        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE) {{
                setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);
            }}
        };
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Jaw Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(jawPeriod, 2, 200, 1)),
            new OptInputParameterInfo("Teeth Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(teethPeriod, 2, 200, 1)),
            new OptInputParameterInfo("Lips Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(lipsPeriod, 2, 200, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
           new OutputParameterInfo("Positive", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM) {{
                setShift(teethShift);
                setColor(Color.GREEN);
                setDrawnByIndicator(true);
            }},
            new OutputParameterInfo("Negative", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM) {{
                setShift(lipsShift);
                setColor(Color.RED);
                setDrawnByIndicator(true);
            }},
            new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE) {{
                setShift(teethShift);
                setColor(Color.BLUE);
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

        double[] jawSma = new double[endIndex - startIndex + 2 + getLookback()];
        double[] teethSma = new double[endIndex - startIndex + 2 + getLookback()];
        double[] lipsSma = new double[endIndex - startIndex + 2 + getLookback()];

        jawSmmaIndicator.setInputParameter(0, inputs[0]);
        teethSmmaIndicator.setInputParameter(0, inputs[0]);
        lipsSmmaIndicator.setInputParameter(0, inputs[0]);

        jawSmmaIndicator.setOutputParameter(0, jawSma);
        teethSmmaIndicator.setOutputParameter(0, teethSma);
        lipsSmmaIndicator.setOutputParameter(0, lipsSma);

        IndicatorResult dJawSmaResult = jawSmmaIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dTeethSmaResult = teethSmmaIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dLipsSmaResult = lipsSmmaIndicator.calculate(startIndex - 1, endIndex);

        int i, k;
        for (i = 1, k = dJawSmaResult.getNumberOfElements(); i < k; i++) {
            double value = Math.abs(jawSma[i] - teethSma[i]);
            outputs[0][i - 1] = value;
            outputs[1][i - 1] = -1 * Math.abs(teethSma[i] - lipsSma[i]);
            outputs[2][i - 1] = 0;
        }

        return new IndicatorResult(startIndex, i - 1);
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
        return (Math.max(teethPeriod + teethShift, Math.max(jawPeriod + jawShift, lipsPeriod + lipsShift)) + 1);
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

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                jawPeriod = (Integer) value;
                break;
            case 1:
                teethPeriod = (Integer) value;
                break;
            case 2:
                lipsPeriod = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
        jawSmmaIndicator.setOptInputParameter(0, jawPeriod);
        teethSmmaIndicator.setOptInputParameter(0, teethPeriod);
        lipsSmmaIndicator.setOptInputParameter(0, lipsPeriod);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }    

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes,
                           Map<Color, List<Point>> handles) {

        int lastX = -1;
        int lastY = -1;

        double[] values = (double[]) values2;
        int shift = teethShift;
        if (outputIdx == 1){
            shift = lipsShift;
        }
        if (values != null) {
            // check for visible candles
            for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - shift, k =
                    i + indicatorDrawingSupport.getNumberOfCandlesOnScreen() + shift; i < k; i++) {
                if (i >=0 && values.length >= i &&((outputIdx == 0 && values[i]!=0) || (outputIdx == 1 && values[i]!=0))) {
                    if (outputIdx == 0) {
                        if (i > 1) {
                          if (Math.abs(values[i]) > Math.abs(values[i-1]))  {
                              g.setColor(Color.GREEN);
                          } else {
                              g.setColor(Color.RED);
                          }
                        } else {
                            g.setColor(color);
                        }
                        int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i + shift) - 4;
                        int y = (int) indicatorDrawingSupport.getYForValue(values[i]);
                        int width = 4;
                        int height =  - (int) indicatorDrawingSupport.getYForValue(values[i]) + (int) indicatorDrawingSupport.getYForValue(0);
                        
                        fillRect(g, x, y, width, height);
                        
                        if (lastX < x + width) {
                            lastX = x + width;
                            lastY = y;
                        }

                    } else if (outputIdx == 1) {
                        if (i > 1) {
                          if (Math.abs(values[i]) > Math.abs(values[i-1]))  {
                              g.setColor(Color.GREEN);
                          } else {
                              g.setColor(Color.RED);
                          }
                        } else {
                            g.setColor(color);
                        }
                        int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i + shift) - 4;
                        int y =    (int) indicatorDrawingSupport.getYForValue(0);
                        int width = 4;
                        int height =  + (int) indicatorDrawingSupport.getYForValue(values[i]) -  (int) indicatorDrawingSupport.getYForValue(0);
                        
                        fillRect(g, x, y, width, height);
                        
                        if (lastX < x + width) {
                            lastX = x + width;
                            lastY = y;
                        }
                    }
                }
            }
        }
        return new Point(lastX, lastY);
    }
    
    private void fillRect(Graphics g, int x, int y, int width, int height){
        if(g.getColor() == Color.GREEN && outputParameterInfos[0].isShowOutput()){
        	g.fillRect(x, y, width, height);
        }
        if(g.getColor() == Color.RED && outputParameterInfos[1].isShowOutput()){
        	g.fillRect(x, y, width, height);
        }
    }
}

