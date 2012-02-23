/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
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
public class PercentBollingerBands implements IIndicator{
	private IIndicator bbandsIndicator;	    
    private int maPeriod = 20;	    	    	
    private double deviation = 2;
    private IIndicatorsProvider indicatorsProvider;	    
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;	
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];	    	    

    public void onStart(IIndicatorContext context) {	    			    	
        indicatorsProvider = context.getIndicatorsProvider();
        bbandsIndicator = indicatorsProvider.getIndicator("BBANDS");
        bbandsIndicator.setOptInputParameter(3, IIndicators.MaType.SMA.ordinal());
        
        indicatorInfo = new IndicatorInfo("PERSBBANDS", "Percent Bollinger Bands", "Momentum Indicators", false, false, true, 1, 2, 1);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)};
                        
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("MAPeriod", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 1, 2000, 1)),
    		new OptInputParameterInfo("Deviation", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(2, -10000, 10000, 0.2, 2))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)   		
		};	        	       
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
    	if (startIndex - getLookback() < 0) {
              startIndex -= startIndex - getLookback();
        }
        if (startIndex > endIndex) {
        	return new IndicatorResult(0, 0);
        }
                                
        int i, j, totalElements = endIndex - startIndex + 1;
        double[] bbandsLowOutput = new double[totalElements];
        double[] bbandsMiddleOutput = new double[totalElements];            		            	    		        	        	                       	        	      
    	double[] bbandsHighOutput = new double[totalElements];
    	double[] bbandsDummyOutput = new double[totalElements];
    	
    	bbandsIndicator.setOptInputParameter(0, maPeriod);
    	bbandsIndicator.setOptInputParameter(1, deviation);
    	bbandsIndicator.setOptInputParameter(2, 2d);    	
    	bbandsIndicator.setOutputParameter(1, bbandsMiddleOutput);
    	    	    
        bbandsIndicator.setInputParameter(0, inputs[0][3]);
        bbandsIndicator.setOutputParameter(0, bbandsLowOutput);    	    	
    	bbandsIndicator.setOutputParameter(2, bbandsDummyOutput);
        bbandsIndicator.calculate(startIndex, endIndex);
                
        bbandsIndicator.setInputParameter(0, inputs[0][2]);
        bbandsIndicator.setOutputParameter(0, bbandsDummyOutput);    	    	
    	bbandsIndicator.setOutputParameter(2, bbandsHighOutput);
        IndicatorResult bbandsHighRes = bbandsIndicator.calculate(startIndex, endIndex);        
    	
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {	        	
        	outputs[0][j] = (inputs[0][1][i] - bbandsLowOutput[j]) / (bbandsHighOutput[j] - bbandsLowOutput[j]);        	         	       
        }		                   	 
        
        return new IndicatorResult(bbandsHighRes.getFirstValueIndex(), bbandsHighRes.getNumberOfElements());	      
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
        return bbandsIndicator.getLookback();
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
    	outputs[index] = (double[]) array;
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
                maPeriod = (Integer) value;
                bbandsIndicator.setOptInputParameter(0, maPeriod);
                break;
            case 1:
                deviation = (Double) value;	                
                bbandsIndicator.setOptInputParameter(1, deviation);	                
                break;                        	            	           
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}
