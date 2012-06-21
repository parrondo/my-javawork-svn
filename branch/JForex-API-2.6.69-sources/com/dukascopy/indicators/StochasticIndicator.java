package com.dukascopy.indicators;
	
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;
	
	/**
	 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
	 * Please, update the corresponding JS code in case of updating of this class.
	 * 
	 * @author anatoly.pokusayev
	 *
	 */
public class StochasticIndicator implements IIndicator {
		private IIndicatorsProvider indicatorsProvider;	
		private IIndicator slowKMa;
	    private IIndicator slowDMa;
	    private int fastKPeriod = 5;	    	    	    
	    private IndicatorInfo indicatorInfo;
	    private InputParameterInfo[] inputParameterInfos;
	    private OutputParameterInfo[] outputParameterInfos;
	    private OptInputParameterInfo[] optInputParameterInfos;	
	    private double[][][] inputs = new double[1][][];
	    private double[][] outputs = new double[2][];	    	    
	
	    public void onStart(IIndicatorContext context) {	
	    	indicatorsProvider = context.getIndicatorsProvider();
	    	slowKMa = indicatorsProvider.getIndicator("MA");
	    	slowDMa = indicatorsProvider.getIndicator("MA");	        
	        indicatorInfo = new IndicatorInfo("STOCH", "Stochastic", "Momentum Indicators", false, false, true, 1, 5, 2);
	        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)};
	                
	        int[] maValues = new int[IIndicators.MaType.values().length];
	        String[] maNames = new String[IIndicators.MaType.values().length];
	        for (int i = 0; i < maValues.length; i++) {
	            maValues[i] = i;
	            maNames[i] = IIndicators.MaType.values()[i].name();
	        }
	        optInputParameterInfos = new OptInputParameterInfo[] {
	    		new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 1, 2000, 1)),
	    		new OptInputParameterInfo("Slow %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
	    		new OptInputParameterInfo("Slow %K MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
	    		new OptInputParameterInfo("Slow %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
	    		new OptInputParameterInfo("Slow %D MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
			};
	        outputParameterInfos = new OutputParameterInfo[] {
	    		new OutputParameterInfo("Slow %K", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),	    		
	    		new OutputParameterInfo("Slow %D", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)    		
			};
	    }
	
	    public IndicatorResult calculate(int startIndex, int endIndex) {
	    	if (startIndex - getLookback() < 0) {
	              startIndex -= startIndex - getLookback();
	        }
	        if (startIndex > endIndex) {
	        	return new IndicatorResult(0, 0);
	        }	    	
	    		    	
	        int maLookback = slowKMa.getLookback() + slowDMa.getLookback();
	    	double[] kInputs = new double[endIndex - startIndex + 1 + maLookback];	
	    	
	    	int i, j;	        	        	        	       
	    	double[] dmaOutput;	        	        
	        dmaOutput = new double[endIndex - startIndex + 1 + slowDMa.getLookback()];	        		        
	        	        	       
	        for (i = startIndex - maLookback, j = 0; i <= endIndex; i++, j++) {	        	
	        	double highestHigh = inputs[0][2][i], lowestLow = inputs[0][3][i];            	            
	        	
	        	for (int k = (fastKPeriod - 1); k > 0; k--) {        	            
	            	highestHigh = inputs[0][2][i - k] > highestHigh ? inputs[0][2][i - k] : highestHigh;
	            	lowestLow = inputs[0][3][i - k] < lowestLow ? inputs[0][3][i - k] : lowestLow;
	            }	            
	            if (highestHigh - lowestLow == 0){	            	
	            	kInputs[j] = 0;
	            }
	            else{ 
	            	kInputs[j] = 100 * ((inputs[0][1][i] - lowestLow) / (highestHigh - lowestLow));	            	
	            }	            	        
	        }		        	       
	        	      	        
	        slowKMa.setInputParameter(0, kInputs);	       
	        slowKMa.setOutputParameter(0, dmaOutput);	        
	        IndicatorResult kResult = slowKMa.calculate(0, kInputs.length - 1);	       	     
	        
	        if (kResult.getNumberOfElements() == 0) {	
	        	return new IndicatorResult(0, 0);
	        }	        
	        
	        slowDMa.setInputParameter(0, dmaOutput);		   	        
	        slowDMa.setOutputParameter(0, outputs[1]);
        	IndicatorResult dResult = slowDMa.calculate(0, kResult.getNumberOfElements() - 1);	        	
        	
        	if (dResult.getNumberOfElements() == 0) {	
        		return new IndicatorResult(0, 0);
	        }	        
        	
	        System.arraycopy(dmaOutput, dResult.getFirstValueIndex(), outputs[0], 0, dResult.getNumberOfElements());	        		        	        	
	        
		    return new IndicatorResult(kResult.getFirstValueIndex() + dResult.getFirstValueIndex() + (fastKPeriod - 1), dResult.getNumberOfElements());
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
	    	return slowDMa.getLookback() + slowKMa.getLookback() + (fastKPeriod - 1);
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
	                fastKPeriod = (Integer) value;               
	                break;
	            case 1:
	                int slowKPeriod = (Integer) value;	                
	                slowKMa.setOptInputParameter(0, slowKPeriod);	                
	                break;            
	            case 2:
	                int slowKMaType = (Integer) value;	                
	                slowKMa.setOptInputParameter(1, IIndicators.MaType.values()[slowKMaType].ordinal());	                	                
	                break;
	            case 3:
	            	 int slowDPeriod = (Integer) value;	                 
	                 slowDMa.setOptInputParameter(0, slowDPeriod);	                 
	                 break;                           
	            case 4:
	            	int slowDMaType = (Integer) value;	            		            	
	            	slowDMa.setOptInputParameter(1, IIndicators.MaType.values()[slowDMaType].ordinal());	            	
	                break;
	           
	            default:
	                throw new ArrayIndexOutOfBoundsException(index);
	        }
	    }
	}