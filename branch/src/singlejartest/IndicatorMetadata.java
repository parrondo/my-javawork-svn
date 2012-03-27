package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

@RequiresFullAccess   
public class IndicatorMetadata implements IStrategy {

	private IConsole console;
	private IIndicators indicators;

	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		this.indicators = context.getIndicators();
		 indicators.registerCustomIndicator(SIndicator.class);
		IIndicator indCOG = context.getIndicators().getIndicator("SMA");
		printIndicatorInfos(indCOG);
	}
	
	private void printIndicatorInfos(IIndicator ind){
		IndicatorInfo info = ind.getIndicatorInfo();
		print(String.format("%s: input count=%s, optional input count=%s, output count=%s", 
				info.getTitle(), info.getNumberOfInputs(), info.getNumberOfOptionalInputs(), info.getNumberOfOutputs()));
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfInputs(); i++){
	        print(String.format("Input %s: %s - %s", i, ind.getInputParameterInfo(i).getName(), ind.getInputParameterInfo(i).getType()));
	    }
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfOptionalInputs(); i++){
	    	print(String.format("Opt Input %s: %s - %s", i, ind.getOptInputParameterInfo(i).getName(), ind.getOptInputParameterInfo(i).getType()));
	    }
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfOutputs(); i++){
	    	print(String.format("Output %s: %s - %s", i, ind.getOutputParameterInfo(i).getName(), ind.getOutputParameterInfo(i).getType()));
	    }
	}
	
	private void print(Object o){
	    console.getOut().println(o);
	}
	

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(IMessage message) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccount(IAccount account) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() throws JFException {
		// TODO Auto-generated method stub
		
	}
	
	public static class Indicator implements IIndicator {
	    private IndicatorInfo indicatorInfo;
	    private InputParameterInfo[] inputParameterInfos;
	    private OptInputParameterInfo[] optInputParameterInfos;
	    private OutputParameterInfo[] outputParameterInfos;
	    private double[][] inputs = new double[1][];
	    private int timePeriod = 2;
	    private double[][] outputs = new double[1][];
	    
	    public void onStart(IIndicatorContext context) {
	        indicatorInfo = new IndicatorInfo("EXAMPIND", "Sums previous values", "My indicators",
	        		false, false, false, 1, 1, 1);
	        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
	        optInputParameterInfos = new OptInputParameterInfo[] {new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
	                new IntegerRangeDescription(2, 2, 100, 1))};
	        outputParameterInfos = new OutputParameterInfo[] {new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE,
	                OutputParameterInfo.DrawingStyle.LINE)};
	    }

	    public IndicatorResult calculate(int startIndex, int endIndex) {
	        //calculating startIndex taking into account lookback value
	        if (startIndex - getLookback() < 0) {
	            startIndex -= startIndex - getLookback();
	        }
	        int i, j;
	        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
	        	double value = 0;
	        	for (int k = timePeriod; k > 0; k--) {
	        		value += inputs[0][i - k];
	        	}
	            outputs[0][j] = value;
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
	        inputs[index] = (double[]) array;
	    }

	    public void setOptInputParameter(int index, Object value) {
	        timePeriod = (Integer) value;
	    }

	    public void setOutputParameter(int index, Object array) {
	        outputs[index] = (double[]) array;
	    }
	}
	
}



