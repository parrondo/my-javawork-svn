package com.tictactec.ta.lib.test;

import java.util.Arrays;
import com.tictactec.ta.lib.*;

public class MyTest {
	private double input[];
	private int inputInt[];
	private double output[];
	private int outputInt[];
	private MInteger outBegIdx;
	private MInteger outNbElement;
	private RetCode retCode;
	private Core lib=new Core();;
	private int lookback;
	static public double[] close = new double[] {91.500000,94.815000,94.375000,95.095000,93.780000,94.625000};
	
	public void testMA_SMA()
    {
        lookback = lib.movingAverageLookback(10,MAType.Sma);
        retCode = lib.movingAverage(0,input.length-1,input,10,MAType.Sma,outBegIdx,outNbElement,output);
    }
	
	public static void main(String[] args) throws Exception {
		
	}

}
