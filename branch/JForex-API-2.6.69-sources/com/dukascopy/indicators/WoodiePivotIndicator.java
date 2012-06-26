/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.*;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.*;
import java.text.DecimalFormat;

/**
 * Created by: S.Vishnyakov
 * Date: Mar 1, 2010
 * Time: 10:09:46 AM
 */
public class WoodiePivotIndicator implements IIndicator, IDrawingIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private IBar[][] inputs = new IBar[2][];
    private double[][] outputs = new double[7][];
    private InputParameterInfo dailyInput ;
    private final GeneralPath generalPath = new GeneralPath();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    private int[] outputXValues;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("WOODPIVOT", "Woodie Pivot", "Overlap Studies", true, false, true, 2, 1, 7);
        indicatorInfo.setSparseIndicator(true);
        indicatorInfo.setRecalculateAll(true);
        dailyInput = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
        dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
        dailyInput.setFilter(Filter.WEEKENDS);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Main Input data", InputParameterInfo.Type.BAR), dailyInput};

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

        optInputParameterInfos = new OptInputParameterInfo[] {
              new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(7 , periodValues, periodNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Central Point (P)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Resistance (R1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Support (S1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Resistance (R2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Support (S2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Resistance (R3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            },
            new OutputParameterInfo("Support (S3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                {
                    setDrawnByIndicator(true);
                }
            }
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        int i, j;
        IBar bar = null;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            int index = j;
            /*
             * Find the same time in inputs[i] as from inputs[0] by index
             */
            int timeIndex = getTimeIndex(inputs[0][index].getTime(), inputs[1]);

            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
             if (bar != null && timeIndex > -1) {
                bar = inputs[0][inputs[0].length - 2];
                // P
                double p = (2* bar.getClose() + bar.getHigh() + bar.getLow())/4;
                outputs[0][j] = p;
                // R1
                outputs[1][j] = 2 * p -  bar.getLow();
                // S1
                outputs[2][j] = 2 * p -  bar.getHigh();
                // R2
                outputs[3][j] = p +  bar.getHigh() -  bar.getLow();
                // S2
                outputs[4][j] = p -  (bar.getHigh() - bar.getLow());
                // R3
                outputs[5][j] = bar.getHigh()  + 2 * (p - bar.getLow());
                // S3
                outputs[6] [j] = bar.getLow() - 2 * (bar.getHigh() - p);
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
            IBar trg = target[i];
            if (time < trg.getTime() && time >= (trg.getTime() - dailyInput.getPeriod().getInterval())) {
                return (i - 1);
            } else if (i == (target.length - 1) && time >= trg.getTime()) {
                return (i - 1);
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
            case 0 : dailyInput.setPeriod(Period.ONE_MIN);
            break;
            case 1 : dailyInput.setPeriod(Period.FIVE_MINS);
            break;
            case 2 : dailyInput.setPeriod(Period.TEN_MINS);
            break;
            case 3 : dailyInput.setPeriod(Period.FIFTEEN_MINS);
            break;
            case 4 : dailyInput.setPeriod(Period.THIRTY_MINS);
            break;
            case 5 : dailyInput.setPeriod(Period.ONE_HOUR);
            break;
            case 6 : dailyInput.setPeriod(Period.FOUR_HOURS);
            break;
            case 7 : dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
            break;
            case 8 : dailyInput.setPeriod(Period.WEEKLY);
            break;
            case 9 : dailyInput.setPeriod(Period.MONTHLY);
            break;
            default: dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public Point drawOutput(
    		Graphics g, 
    		int outputIdx, 
    		Object values, 
    		Color color, 
    		Stroke stroke, 
    		IIndicatorDrawingSupport indicatorDrawingSupport, 
    		java.util.List<Shape> shapes, 
    		Map<Color, java.util.List<Point>> handles
    ) {
        if (values != null) {
        	double[] outputs = ((double[]) values);
            Graphics2D g2 = (Graphics2D) g;
            generalPath.reset();

            g2.setColor(color);
            g2.setStroke(stroke);

            Integer previousX = null;
            int maxWidth = indicatorDrawingSupport.getChartWidth() + 50;

            if (outputIdx == getFirstEnabledOutputIndex()){
            	outputXValues = new int[outputs.length];
            	Arrays.fill(outputXValues, -1);
            }
            for (int i = outputs.length - 1; i >= 0; i--) {
                double d = outputs[i];

                if (Double.isNaN(d)) {
                    continue;
                }

                if (outputIdx == getFirstEnabledOutputIndex()){                 
                	outputXValues[i] = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }
                int x = (outputXValues == null || i > outputXValues.length - 1 || outputXValues[i] == -1) ? 
            			(int) indicatorDrawingSupport.getMiddleOfCandle(i) : outputXValues[i];
                int y = (int) indicatorDrawingSupport.getYForValue(d);

                if (previousX != null && ((0 <= previousX && previousX <= maxWidth) || (0 <= x && x <= maxWidth))) {
                    generalPath.moveTo(previousX.intValue(), y);
                    if (i >= outputs.length - 2 && indicatorDrawingSupport.isLastCandleInProgress()) {
                        int fontSize = 9;
                        g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), fontSize));
                        String valueStr = decimalFormat.format(d);
                        String name = outputParameterInfos[outputIdx].getName();
                        String lineCode = name.substring(name.length() - 5, name.length());

                        String text = lineCode + " - " + valueStr;
                        g2.drawString(text, indicatorDrawingSupport.getChartWidth() - 70, y - 4);

                        g.drawLine(x, y, maxWidth, y);
                    }
                    generalPath.lineTo(x, y);
                }
                previousX = x;
            }
            g2.draw(generalPath);
        }
        return null;
    }
    
    private int getFirstEnabledOutputIndex(){
		for (int i = 0; i < getIndicatorInfo().getNumberOfOutputs(); i++){
			if (outputParameterInfos[i].isShowOutput()) {
				return i; 
			}
		}
		return -1;
	}
}