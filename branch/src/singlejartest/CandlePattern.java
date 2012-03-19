package singlejartest;

import java.util.List;

import com.dukascopy.api.IBar;
import com.tictactec.ta.lib.CandleSettingType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RangeType;
import com.tictactec.ta.lib.RetCode;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CandlePattern {
	private double input[];
    private int inputInt[];
    private double output[];
    private int outputInt[];
    private double open[];
    private double high[];
    private double low[];
    private double close[];
    private MInteger outBegIdx; 
    private MInteger outNbElement;
    private RetCode retCode;
    private Core lib;
    private int lookback;
    private static final Logger LOGGER = LoggerFactory
			.getLogger(CandlePattern.class);
    
    public CandlePattern() {
        // Create the library (typically done only once).
        lib = new Core();
        input = new double[200];
        inputInt = new int[200];        
        output = new double[200];
        outputInt = new int[200];
        
        open=new double[50];
        high=new double[50];
        low=new double[50];
        close=new double[50];
        
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
    }
    
    public boolean isMarubozu(List<IBar> bars)
    {
    	lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 2.5);
    	retCode = lib.cdlMarubozu(0,10,open,high,low,close,outBegIdx,outNbElement,outputInt);
        if(retCode!=RetCode.Success){
        	LOGGER.info("Failed: "+retCode);
        	return false;
        }
        return true;
    }
    
    public boolean isMarubozuBreak()
	{
		return false;
	}
    
    public boolean BarsToOHLC(List<IBar> bars)
    {
    	int i=0;
    	for (IBar bar : bars) {
    		open[i]=bar.getOpen();
    		high[i]=bar.getHigh();
    		low[i]=bar.getLow();
    		close[i]=bar.getClose();
    		i++;
    	}
    	return true;
    }
    

}
