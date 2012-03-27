package singlejartest;

import java.io.File;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class CustomIndicatorStrategy2 implements IStrategy {
	private IConsole console;
	private IIndicators indicators;
	
	public void onStart(IContext context) throws JFException {
		this.console = context.getConsole();
		this.indicators = context.getIndicators();
        
		//register custom indicator located in ...\JForex\Strategies\files folder
        indicators.registerCustomIndicator(new File(context.getFilesDir() + System.getProperty("file.separator") + "Indicator.jfx"));
		//register custom indicator defined inside a strategy
        indicators.registerCustomIndicator(Indicator.class);
        
        Object[] firstIndicatorValues =  indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_HOUR, new  OfferSide[] {OfferSide.BID}, "EXAMPIND", 
        		new AppliedPrice[]{AppliedPrice.CLOSE}, new Object[]{4}, 0);
        console.getOut().println("first indicator value: " + ((Object[])firstIndicatorValues)[0]);
        
        Object[] secondIndicatorValues =  indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_HOUR, new  OfferSide[] {OfferSide.BID}, "INNERINDICATOR", 
        		new AppliedPrice[]{AppliedPrice.CLOSE}, new Object[]{4}, 0);
        console.getOut().println("second indicator value: " + ((Object[])secondIndicatorValues)[0]);
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}
	
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
    
    public static class Indicator implements IIndicator {
        private IndicatorInfo indicatorInfo = 
        	new IndicatorInfo("INNERINDICATOR", "Sums previous values", "My indicators", false, false, false, 1, 1, 1);
        private InputParameterInfo[] inputParameterInfos = 
        	new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
        private OptInputParameterInfo[] optInputParameterInfos = 
        	new OptInputParameterInfo[] {new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(2, 2, 100, 1))};
        private OutputParameterInfo[] outputParameterInfos = new OutputParameterInfo[] {new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE,
                OutputParameterInfo.DrawingStyle.LINE)};
        private double[][] inputs = new double[1][];
        private int timePeriod = 2;
        private double[][] outputs = new double[1][];
        
        public void onStart(IIndicatorContext context) { }

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
