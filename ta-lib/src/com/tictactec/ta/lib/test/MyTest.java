package com.tictactec.ta.lib.test;

import java.util.Arrays;
import com.tictactec.ta.lib.*;

public class MyTest {
	private double input[];
	private int inputInt[];
	private int output[];
	private int outputInt[];
	private MInteger outBegIdx;
	private MInteger outNbElement;
	private RetCode retCode;
	private Core lib=new Core();;
	private int lookback;
//	static public double[] close = new double[] {91.500000,94.815000,94.375000,95.095000,93.780000,94.625000};
	
	static public double[] Openin = new double[]{60.000000,70.0000,80.0000,90.000000,60.0000,120.000};
	static public double[] Highin = new double[]{70.000000,80.0000,90.0000,100.000000,120.0000,130.000};
	static public double[] Lowin = new double[]{60.000000,70.0000,80.0000,90.000000,30.0000,120.000};
	static public double[] Closein = new double[]{70.000000,80.0000,90.0000,100.000000,60.2000,130.000};
	
	public void test()
    {
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
        lib.cdlDojiLookback( );
        retCode = lib.cdlDoji(0,5,Openin,Highin,Lowin,Closein,outBegIdx,outNbElement,output);
        System.out.println(retCode);
    }
	
	public static void main(String[] args) throws Exception {
		MyTest mytest=new MyTest();
		mytest.test();
	}

}
