/*
 * Copyright 2010 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;

/**
 * @author Sergey Vishnyakov
 */
public class TrendEnvelopesIndicator implements IIndicator {
    private IIndicator lwma;
    private int timePeriod = 14;
    private double deviation = 0.1;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];

    public void onStart(IIndicatorContext context) {
        lwma = context.getIndicatorsProvider().getIndicator("LWMA");

        indicatorInfo =
                new IndicatorInfo("TrendEnvelopes", "Trend Envelope", "Overlap Studies", true, false, true, 1, 2, 2);
        indicatorInfo.setRecalculateAll(true);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)};
        optInputParameterInfos =
                new OptInputParameterInfo[]{new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 1, 500, 1)), new OptInputParameterInfo("Deviation", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(deviation, 0.01, 100, 0.01, 2))};
        outputParameterInfos =
                new OutputParameterInfo[]{new OutputParameterInfo("Low Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                    {
                        setColor(Color.BLUE);
                        setGapAtNaN(true);
                    }
                }, new OutputParameterInfo("High Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {
                    {
                        setColor(Color.ORANGE);
                        setGapAtNaN(true);
                    }
                },};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        lwma.setInputParameter(0, inputs[0][1]);
        lwma.setOptInputParameter(0, timePeriod);
        double[] dsma = new double[endIndex - startIndex + 1 + lwma.getLookback()];
        lwma.setOutputParameter(0, dsma);

        IndicatorResult dSmaResult = lwma.calculate(startIndex - 1, endIndex);

        double[] valuesHigh = new double[dSmaResult.getNumberOfElements()];
        double[] valuesLow = new double[dSmaResult.getNumberOfElements()];
        double[] trend = new double[dSmaResult.getNumberOfElements()];
        int i, k;
        for (i = 1, k = dSmaResult.getNumberOfElements(); i < k; i++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            //outputs[0][i - 1] = Double.NaN;
            //outputs[1][i - 1] = Double.NaN;
            valuesLow[i -1] = (1 - deviation / 100) * dsma[i -1];
            valuesHigh[i -1] = (1 + deviation / 100) * dsma[i -1];
            if (i > 0) {
                trend[i] = trend[i-1];
            }

            if (inputs[0][1][i  - 1 + getLookback()] > valuesHigh[i - 1]) {
                trend[i] = 1;
            }
            if (inputs[0][1][i - 1+ getLookback()] < valuesLow[i - 1]) {
                trend[i] = -1;
            }
            if (trend[i] > 0) {
                outputs[1][i - 1] = Double.NaN;
                if (i > 1 && valuesLow[i -1] < valuesLow[i - 2]) {
                    valuesLow[i - 1] = valuesLow[i - 2];
                }
                outputs[0][i - 1] = valuesLow[i -1];
            } else {
                outputs[0][i - 1] = Double.NaN;
                if (i > 1 && valuesHigh[i -1] > valuesHigh[i - 2]) {
                    valuesHigh[i -1] = valuesHigh[i - 2];
                }
                outputs[1][i - 1] = valuesHigh[i-1];
            }
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
        return (lwma.getLookback() + 1);
    }

    public int getLookforward() {
        return 0;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[][]) array;
    }

    public void setOutputParameter(int index, Object array) {
        switch (index) {
            case 0:
                outputs[index] = (double[]) array;
                break;
            case 1:
                outputs[index] = (double[]) array;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
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
                timePeriod = (Integer) value;
                lwma.setOptInputParameter(0, timePeriod);    
                break;
            case 1:
                deviation = (Double) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}