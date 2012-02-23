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

/**
 * Created by: S.Vishnyakov
 * Date: Jan 23, 2009
 * Time: 1:59:16 PM
 */
public class VolumeWAP implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] inputsAveragePrice = new double[1][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 5;
    private IIndicator sumPriceIndicator;
    private IIndicator sumDivPriceIndicator;

    public void onStart(IIndicatorContext context) {
        sumPriceIndicator = context.getIndicatorsProvider().getIndicator("SUM");
        sumDivPriceIndicator = context.getIndicatorsProvider().getIndicator("SUM");
        indicatorInfo = new IndicatorInfo("VolumeWAP", "Volume Weighted Average Price", "Overlap Studies", true, false, false, 2, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE),
    		new InputParameterInfo("inReal", InputParameterInfo.Type.DOUBLE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(4, 2, 100, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("VWAP", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
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

        double[] multPrice = new double[endIndex - startIndex + 1 + getLookback()];
        double[] sumPrice = new double[endIndex - startIndex + 1 + getLookback()];
        double[] sumDiv = new double[endIndex - startIndex + 1 + getLookback()];

        //multPrice
        for (int resIndex = 0; resIndex <= endIndex; resIndex++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            multPrice[resIndex] = inputs[0][4][resIndex] * inputsAveragePrice[0][resIndex];
        }

        sumPriceIndicator.setInputParameter(0, multPrice);
        sumPriceIndicator.setOptInputParameter(0, timePeriod);
        sumPriceIndicator.setOutputParameter(0, sumPrice);
        IndicatorResult sumResult = sumPriceIndicator.calculate(0, multPrice.length - 1);

        sumDivPriceIndicator.setInputParameter(0, inputs[0][4]);
        sumDivPriceIndicator.setOptInputParameter(0, timePeriod);
        sumDivPriceIndicator.setOutputParameter(0, sumDiv);
        IndicatorResult divResult = sumDivPriceIndicator.calculate(startIndex - 1, endIndex);

        int i, k;
        for (i = 1, k = sumResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i - 1] = sumPrice[i] / sumDiv[i];
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
        return timePeriod;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        switch (index) {
            case 0:
                inputs[0] = (double[][]) array;
                break;
            case 1:
                inputsAveragePrice[0] = (double[]) array;
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
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}