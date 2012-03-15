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
	private Core lib=new Core();
	private int lookback;
//	static public double[] close = new double[] {91.500000,94.815000,94.375000,95.095000,93.780000,94.625000};
	
	static public double[] Openin = new double[] {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
	static public double[] Highin = new double[] {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
	static public double[] Lowin = new double[]  {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
	static public double[] Closein = new double[]{3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
	
	static public double[] BeltHoldOpenin = new double[] {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19};
	static public double[] BeltHoldHighin = new double[] {5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,29.50};
	static public double[] BeltHoldLowin = new double[]  {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,18.82};
	static public double[] BeltHoldClosein = new double[]{5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,23.00};
	
	public void test()
    {
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
        lib.cdlDojiLookback( );
        retCode = lib.cdlDoji(0,5,Openin,Highin,Lowin,Closein,outBegIdx,outNbElement,output);
        System.out.println(retCode);
    }
	
	public void BeltHoldtest()
	{
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
 //       lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 1.0);
        lib.cdlBeltHoldLookback();
        retCode = lib.cdlBeltHold(0,16,BeltHoldOpenin,BeltHoldHighin,BeltHoldLowin,BeltHoldClosein,outBegIdx,outNbElement,output);
        System.out.println(retCode);
	}
	
	public void  cdl2CrowsTest()
	{
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
     
        lib.cdl2CrowsLookback( );
        retCode = lib.cdl2Crows(0,16,BeltHoldOpenin,BeltHoldHighin,BeltHoldLowin,BeltHoldClosein,outBegIdx,outNbElement,output);
        System.out.println(retCode);
	}
	
	public void DarkCloudCoverTest()
	{
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
 //       lib.SetCandleSettings(CandleSettingType.BodyLong,RangeType.RealBody, 10, 2.0);
//        lib.cdlcdlDarkCloudCover();
        retCode = lib.cdlDarkCloudCover(0,16,BeltHoldOpenin,BeltHoldHighin,BeltHoldLowin,BeltHoldClosein,0,outBegIdx,outNbElement,output);
        System.out.println(retCode);
	}
	
	
	public static void main(String[] args) throws Exception {
		MyTest mytest=new MyTest();
//		mytest.test();
		mytest.BeltHoldtest();
//		mytest.cdl2CrowsTest();
//		mytest.DarkCloudCoverTest();
		
	}

}
