/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

/*
 * Copyright 2009 Dukascopy (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.*;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * Created by: S.Vishnyakov
 * Date: Oct 21, 2009
 * Time: 1:59:16 PM
 */
public class FibonacciPivotIndicator implements IIndicator, IDrawingIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private IBar[][] inputs = new IBar[2][];
    private double[][] outputs = new double[7][];
    private InputParameterInfo childInput;
    private final GeneralPath generalPath = new GeneralPath();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00000");

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("FIBPIVOT", "Fibonacci Pivot", "Overlap Studies", true, false, true, 2, 1, 7);
        indicatorInfo.setSparseIndicator(true);
        indicatorInfo.setRecalculateAll(true);
        childInput = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
        childInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
        childInput.setFilter(Filter.WEEKENDS);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Main Input data", InputParameterInfo.Type.BAR), childInput};

        int[] periodValues = new int[10];
        String[] periodNames = new String[10];
        periodValues[0] = 0;
        periodNames[0] = Period.ONE_MIN.name();
        periodValues[1] = 1;
        periodNames[1] = Period.FIVE_MINS.name();
        periodValues[2] = 2;
        periodNames[2] = Period.TEN_MINS.name();
        periodValues[3] = 3;
        periodNames[3] = Period.FIFTEEN_MINS.name();
        periodValues[4] = 4;
        periodNames[4] = Period.THIRTY_MINS.name();
        periodValues[5] = 5;
        periodNames[5] = Period.ONE_HOUR.name();
        periodValues[6] = 6;
        periodNames[6] = Period.FOUR_HOURS.name();
        periodValues[7] = 7;
        periodNames[7] = Period.DAILY.name();
        periodValues[8] = 8;
        periodNames[8] = Period.WEEKLY.name();
        periodValues[9] = 9;
        periodNames[9] = Period.MONTHLY.name();

        optInputParameterInfos = new OptInputParameterInfo[]{new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(7, periodValues, periodNames))};

        outputParameterInfos = new OutputParameterInfo[]{new OutputParameterInfo("Central Point (P) ", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
            {
                setDrawnByIndicator(true);
            }
        }};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        int i, j;
        IBar bar = null;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {

            int timeIndex = getTimeIndex(inputs[0][i].getTime(), inputs[1]);

            if (bar != null && timeIndex > -1) {
                // P
                double p = (bar.getClose() + bar.getHigh() + bar.getLow())/3;
                outputs[0][j] = p;
                // R1
                outputs[1][j] = p + 0.382 *(bar.getHigh() -  bar.getLow());
                // S1
                outputs[2][j] = p - 0.382 *(bar.getHigh() -  bar.getLow());
                // R2
                outputs[3][j] = p + 0.618 *(bar.getHigh() -  bar.getLow());
                // S2
                outputs[4][j] = p - 0.618 *(bar.getHigh() -  bar.getLow());
                // R3
                outputs[5][j] = p +  bar.getHigh() - bar.getLow();
                // S3
                outputs[6] [j] = p - (bar.getHigh() - bar.getLow());
            } else {
                outputs[0][j] = Double.NaN;
                outputs[1][j] = Double.NaN;
                outputs[2][j] = Double.NaN;
                outputs[3][j] = Double.NaN;
                outputs[4][j] = Double.NaN;
                outputs[5][j] = Double.NaN;
                outputs[6][j] = Double.NaN;
            }
            if (timeIndex > -1) {
                bar = inputs[1][timeIndex];
            }
        }
        return new IndicatorResult(startIndex, j);
    }

    private int getTimeIndex(long time, IBar[] target) {
        if (target == null) {
            return -1;
        }

        for (int i = target.length - 1; i > 0; i--) {
            IBar currentCandle = target[i];
            if (time < currentCandle.getTime() && time >= (currentCandle.getTime() - childInput.getPeriod().getInterval())) {
                return (i - 1);
            } else if (i == (target.length - 1) && time == currentCandle.getTime()) { 
                return (i);
            }
        }

        return -1;
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
        return 0;
    }

    public int getLookforward() {
        return 0;
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
        inputs[index] = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        int period = (Integer) value;
        switch (period) {
            case 0:
            	childInput.setPeriod(Period.ONE_MIN);
                break;
            case 1:
            	childInput.setPeriod(Period.FIVE_MINS);
                break;
            case 2:
            	childInput.setPeriod(Period.TEN_MINS);
                break;
            case 3:
            	childInput.setPeriod(Period.FIFTEEN_MINS);
                break;
            case 4:
            	childInput.setPeriod(Period.THIRTY_MINS);
                break;
            case 5:
            	childInput.setPeriod(Period.ONE_HOUR);
                break;
            case 6:
            	childInput.setPeriod(Period.FOUR_HOURS);
                break;
            case 7:
            	childInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
                break;
            case 8:
            	childInput.setPeriod(Period.WEEKLY);
                break;
            case 9:
            	childInput.setPeriod(Period.MONTHLY);
                break;
            default:
            	childInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }


    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values, Color color, Stroke stroke, IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes, Map<Color, java.util.List<Point>> handles) {
        if (values != null) {
        	double[] outputs = ((double[]) values);
        	
            Graphics2D g2 = (Graphics2D) g;
            generalPath.reset();

            g2.setColor(color);
            g2.setStroke(stroke);
            int fontSize = 9;
            g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), fontSize));

            Integer previousX = null;
            int maxWidth = indicatorDrawingSupport.getChartWidth() + 50;
            int lastY = 0;
            double lastValue = 0;

            for (int i = 0; i <= outputs.length - 1; i++) {                
            	double currentValue = ((double[]) values)[i];
                
            	if (Double.isNaN(currentValue)) {
                    continue;
                }

                int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                int y = (int) indicatorDrawingSupport.getYForValue(currentValue);

                if (previousX != null && ((previousX >= 0 && previousX <= maxWidth) || (x >= 0 && x <= maxWidth)) ) {                   
                	generalPath.moveTo(previousX.intValue(), y);
                    generalPath.lineTo(x, y);
                }
                previousX = x;               
                lastY = y;
                lastValue = currentValue;
            }
            if (previousX != null){
	            generalPath.moveTo(previousX.intValue(), lastY);        	                   
	            g2.drawString(getOutputText(lastValue, outputIdx), indicatorDrawingSupport.getChartWidth() - 70, lastY - 4);
	            g.drawLine(previousX, lastY, maxWidth, lastY);
	            generalPath.lineTo(previousX, lastY);
            }
            g2.draw(generalPath);
        }
        return null;
    }
    
    private String getOutputText(double outputValue, int outputIndex){
    	 String valueStr = decimalFormat.format(outputValue);
         String name = outputParameterInfos[outputIndex].getName();
         String lineCode = name.substring(name.length() - 5, name.length());         
         String result = lineCode + " - " + valueStr;
         
         return result;
    }
}