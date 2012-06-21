package com.dukascopy.indicators;

import java.awt.Color;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * 
 * @author anatoly.pokusayev
 *
 */

public class VortexIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];    
    private double[][] outputs = new double[2][];
    private int period = 14;
    private IIndicator trueRange;
    private IIndicatorsProvider indProvider;
    
    public void onStart(IIndicatorContext context) {
    	trueRange = context.getIndicatorsProvider().getIndicator("TRANGE");
        indicatorInfo = new IndicatorInfo("VORTEX", "Vortex indicator", "", false, false, false, 1, 1, 2);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.PRICE)};
        optInputParameterInfos = new OptInputParameterInfo[] {
        		new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period, 1, 500, 1))};
        outputParameterInfos = new OutputParameterInfo[] {
        		new OutputParameterInfo("+VM", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE){{setColor(Color.BLUE);}},        		
        		new OutputParameterInfo("-VM", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE){{setColor(Color.RED);}} };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        double[] trangeOutput = new double[inputs[0][0].length - trueRange.getLookback()];  
        trueRange.setInputParameter(0, inputs[0]);
        trueRange.setOutputParameter(0, trangeOutput);
        trueRange.calculate(0, inputs[0][0].length - 1);
             
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
        	double positiveMovementSum = 0, negativeMovementSum = 0;
        	for (int k = i - period; k < i; k++) {
            	positiveMovementSum += Math.abs(inputs[0][2][k] - inputs[0][3][k - 1]); 
            	negativeMovementSum += Math.abs(inputs[0][3][k] - inputs[0][2][k - 1]);
            }
            
        	double trueRangeSum = 0;
        	for (int k = i - period - 1; k < i - 1; k++) {
            	trueRangeSum += trangeOutput[k];
            }
            
            outputs[0][j] = positiveMovementSum / trueRangeSum;
            outputs[1][j] = negativeMovementSum / trueRangeSum;
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
    	if (trueRange == null && indProvider != null){
    		trueRange = indProvider.getIndicator("TRANGE");
    		return period + trueRange.getLookback();
    	}     
    	else if (trueRange != null){
    		return period + trueRange.getLookback();
    	}
    	else {
    		return period + 1;
    	}
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
   		period = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
