package singlejartest;

import java.util.List;

import com.dukascopy.api.IBar;
import com.tictactec.ta.lib.CandleSettingType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RangeType;
import com.tictactec.ta.lib.RetCode;

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
    	lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 2.0);
    	retCode = lib.cdlMarubozu(0,16,bars.getOpen(),bar.getHigh(),bar.getLow(),bar.getClose(),outBegIdx,outNbElement,outputInt);
        lib.cdlBeltHoldLookback();
        return true;
    }
    
    public boolean isMarubozuBreak()
	{
		return false;
	}
    
    public boolean BarsToOHLC(List<IBar> bars)
    {
    	for (IBar bar : bars) {
    		open. bar.getOpen();
    	}
    	return false;
    }
    

}
