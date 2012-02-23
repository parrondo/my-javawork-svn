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
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * @author Dmitry Shohov
 */
public class VolumeIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];
    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VOLUME", "Volumes", "Volume Indicators", false, false, false, 1, 0, 1);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Volumes", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = inputs[0][4][i];
        }
        return new IndicatorResult(startIndex, j);
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
        return null;
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

    public void setOptInputParameter(int index, Object value) {
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}