package singlejartest;

import java.util.List;

import com.dukascopy.api.IBar;
import com.tictactec.ta.lib.CandleSettingType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RangeType;
import com.tictactec.ta.lib.RetCode;
//import com.tictactec.ta.lib.test.MyTest;

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
    
    static public double[] BeltHoldOpenin = new double[] {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19};
	static public double[] BeltHoldHighin = new double[] {5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,23.21};
	static public double[] BeltHoldLowin = new double[]  {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,18.82};
	static public double[] BeltHoldClosein = new double[]{5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,23.00};
    
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
    
    public RetCode addMarubozu(List<IBar> MaruLists,List<IBar> bars)
    {
    	lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 4);
    	lib.SetCandleSettings(CandleSettingType.ShadowVeryShort,RangeType.RealBody, 10, 4);
    	BarsToOHLC(bars);
    	retCode = lib.cdlMarubozu(0,bars.size()-1,open,high,low,close,outBegIdx,outNbElement,outputInt);
        if(retCode!=RetCode.Success){
//        	LOGGER.error("Failed: "+retCode);
        	return retCode;
        }
        else 
        {
        	if(outputInt[bars.size()-1-10]==100) {
        		MaruLists.add(bars.get(bars.size()-1));
        		return retCode;
        	}
        		
        	else if (outputInt[bars.size()-1-10]==-100){
        		MaruLists.add(bars.get(bars.size()-1));
        		return retCode;
        	}
        	
        	else if  (outputInt[bars.size()-1]==0){
        		return retCode;
        	}
        	else{
  //      		LOGGER.error("unknown value");
        		return retCode;
        	}
        }
       
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
    public static void main(String[] args) throws Exception {
//		MyTest mytest=new MyTest();
//		mytest.test();
//		mytest.BeltHoldtest();
//		mytest.cdl2CrowsTest();
//		mytest.DarkCloudCoverTest();
//		mytest.cdlMarubozuTest();
	}
    

}
