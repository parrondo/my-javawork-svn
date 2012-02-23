/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

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
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * Created by: S.Vishnyakov
 * Contributors: Dmitry Shohov
 * Date: Feb 11, 2009
 * Time: 12:56:41 PM
 */
public class SMMAIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 14;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("SMMA", "Smoothed Moving Average (SMMA)", "Overlap Studies", true, false, true, 1, 1, 1);
        indicatorInfo.setRecalculateAll(true);        
        inputParameterInfos = new InputParameterInfo[]{
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 200, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("SMMA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
    			setColor(Color.RED);
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

        //calculating value of the first bar
        //lets get sum
        double sum1 = 0;
        for (int i = startIndex - 1; i >= startIndex - timePeriod; i--) {
            sum1 += inputs[0][i];
        }
        outputs[0][0] = sum1 / timePeriod;
        
        //now calculate rest
        int i, j;
        for (i = 1, j = startIndex + 1; j <= endIndex; i++, j++) {
            double prevSumSubtractPrevSmma = outputs[0][i - 1] * (timePeriod - 1);
            outputs[0][i] = (prevSumSubtractPrevSmma + inputs[0][j]) / timePeriod;
        }
        return new IndicatorResult(startIndex, i);
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
        return timePeriod;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[0] = (double[]) array;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}