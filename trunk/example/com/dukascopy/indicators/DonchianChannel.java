package com.dukascopy.indicators;

import java.awt.Color;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;
/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * @author anatoly.pokusayev
 *
 */
public class DonchianChannel implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private int timePeriod = 10;
    private double[][] outputs = new double[2][];
    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("DONCHIANCHANNEL", "Donchian Channel", "Volatility Indicators",
                true, false, false, 1, 1, 2);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.PRICE)};
        optInputParameterInfos = new OptInputParameterInfo[] {new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(20, 1, 500, 1))};
        outputParameterInfos = new OutputParameterInfo[] {
        		new OutputParameterInfo("High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE){
        			{ setColor(Color.GREEN);}
        		},            
        		new OutputParameterInfo("Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE){
        			{setColor(Color.RED);}
        		}
        };       
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {    	        
    	if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }
        int i, j;                
        for (i = startIndex , j = 0; i <= endIndex; i++, j++) {
        	double highestHigh = inputs[0][2][i - 1], lowestLow = inputs[0][3][i - 1];            
            for (int k = timePeriod; k > 1; k--) {        
            	highestHigh = inputs[0][2][i - k] > highestHigh ? inputs[0][2][i - k] : highestHigh;
            	lowestLow = inputs[0][3][i - k] < lowestLow ? inputs[0][3][i - k] : lowestLow;
            }                        
            outputs[0][j] = highestHigh;
            outputs[1][j] = lowestLow;
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
        return timePeriod;
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
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}