package com.tictactec.ta.lib.test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dukascopy.api.IBar;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.tictactec.ta.lib.*;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

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
	
	static public ArrayList<Double> Open= new ArrayList<Double>(200);
	static public ArrayList<Double> High=new ArrayList<Double>(200);
	static public ArrayList<Double> Low= new ArrayList<Double>(200);
	static public ArrayList<Double> Close=new ArrayList<Double>(200);
	
	static public double[] gOpenin  = new double[] {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19};
	static public double[] gHighin  = new double[] {5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,23.21};
	static public double[] gLowin  = new double[]  {3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,18.82};
	static public double[] gClosein  = new double[]{5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,20,23.10};
	
	static public double[] BeltHoldOpenin = new double[200];
	static public double[] BeltHoldHighin  = new double[200] ;
	static public double[] BeltHoldLowin = new double[200] ;
	static public double[] BeltHoldClosein = new double[200];
	
	
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
	
	public void  cdlMarubozuTest()
	{
		outBegIdx = new MInteger();
        outNbElement = new MInteger();
        output = new int[200];
//        lib.cdl2CrowsLookback( );
        retCode = lib.cdlMarubozu(0,16,BeltHoldOpenin,BeltHoldHighin,BeltHoldLowin,BeltHoldClosein,outBegIdx,outNbElement,output);
        System.out.println(retCode);
	}
	
	public void ListToarray(){
		int i=0;
		for(Double O:Open){
			BeltHoldOpenin[i]=O;
			i++;
		}
		i=0;
		for(Double H:High){
			BeltHoldHighin[i]=H;
			i++;
		}
		i=0;
		for(Double L:Low){
			BeltHoldLowin[i]=L;
			i++;
		}
		i=0;
		for(Double C:Close){
			BeltHoldClosein[i]=C;
			i++;
		}
//		BeltHoldOpenin
	}
	
	public void copyCustom(){
		BeltHoldOpenin=Arrays.copyOf(gOpenin, gOpenin.length);
		BeltHoldHighin=Arrays.copyOf(gHighin, gHighin.length);
		BeltHoldLowin=Arrays.copyOf(gLowin, gLowin.length);
		BeltHoldClosein=Arrays.copyOf(gClosein, gClosein.length);
		
	}
	
	public static void main(String[] args) throws Exception {
		MyTest mytest=new MyTest();
		CSVReader reader = new CSVReader(new FileReader("opencsv-2.3/examples/15Mdata.csv"));
		List<String[]> allElements = reader.readAll();
		for (String[] strArray:allElements){
			Open.add(Double.valueOf( strArray[1]));
			High.add(Double.valueOf( strArray[2]));
			Low.add(Double.valueOf( strArray[3]));
			Close.add(Double.valueOf( strArray[4]));
		}
//		mytest.test();
//		mytest.BeltHoldtest();
//		mytest.cdl2CrowsTest();
//		mytest.DarkCloudCoverTest();
//		mytest.ListToarray();
		mytest.copyCustom();
		mytest.cdlMarubozuTest();
	}

}
