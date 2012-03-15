package singlejartest;

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
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
    }
    
    public boolean isMarubozu()
    {
    	lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 2.0);
        lib.cdlBeltHoldLookback();
        return true;
    }
    

}
