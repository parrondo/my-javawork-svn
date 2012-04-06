package singlejartest;

import java.util.*;

import singlejartest.CrossPoint.CrossType;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class RTChartInfo {
	private IBar highBar;
	private IBar lowBar;
	private List<IBar> hBarList=new ArrayList<IBar>();
	private List<IBar> lBarList=new ArrayList<IBar>();
	private List<IBar> rawBarList;
	private TrendInfo trendInfo;
	public final int HLBARSHIFT = 40;
	private IBar crossBar;

	private IEngine engine;
	private IConsole console;
	private IContext context = null;
	private IHistory history;
	private IIndicators indicators;
	private IChart chart;
	public  CrossPoint crossPoint=null;

	public RTChartInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void initChart(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		crossPoint=new CrossPoint(null);
		findFirstCross(instrument, period, numberOfCandlesBefore, time);
		if(crossPoint.getCrossType()==null){
			System.out.println("bars number is not enough");
			System.exit(0);
		}	
		CreateHighBarList(instrument,period,numberOfCandlesBefore,time);
		CreateLowBarList(instrument,period,numberOfCandlesBefore,time);
		findTrend();
	}
	
	public void findTrend()throws JFException{
		
		int high1,high2,high3;
		int low1,low2,low3;
		high1=searchBarsList(hBarList.get(0).getTime(), rawBarList);
		high2=searchBarsList(hBarList.get(1).getTime(), rawBarList);
		high3=searchBarsList(hBarList.get(2).getTime(), rawBarList);
		
		low1=searchBarsList(lBarList.get(0).getTime(), rawBarList);
		low2=searchBarsList(lBarList.get(1).getTime(), rawBarList);
		low3=searchBarsList(lBarList.get(2).getTime(), rawBarList);	
	}
	
	protected int searchBarsList(long time,List<IBar> barsList)throws JFException{
		int i=0;
		for(IBar bar:barsList){
			if(time==bar.getTime()){
				return i;
			}
			i++;
		}
		throw new JFException("can't find the element");
	}

	public void findFirstCross(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		
		for (IBar bar : barsList) {
			double[] Smma30 = indicators.smma(instrument, period, OfferSide.BID,
					AppliedPrice.CLOSE, 30, Filter.WEEKENDS, 2, bar.getTime(), 0);
			double[] Smma10 = indicators.smma(instrument, period, OfferSide.BID,
					AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2, bar.getTime(), 0);
			double[] Sma5 = indicators.sma(instrument, period, OfferSide.BID,
					AppliedPrice.CLOSE, 5, Filter.WEEKENDS, 2, bar.getTime(), 0);
			
			if(isUpCrossOver(Smma30, Smma10)){
				crossPoint.setCrossType(CrossType.UpCross);
				crossPoint.setTime(bar.getTime());
				CrossPoint.crossBar=bar;
				CrossPoint.smmaCrossPrice=Smma30[1];
			}
			if(isDownCrossOver(Smma30, Smma10)){
				crossPoint.setCrossType(CrossType.DownCross);
				crossPoint.setTime(bar.getTime());
				CrossPoint.crossBar=bar;
				CrossPoint.smmaCrossPrice=Smma10[1];
			}
		}
	}

	public boolean isDownCrossOver(double[] slow, double[] fast)
			throws JFException {
		if ((fast[1] < fast[0]) && (fast[1] < slow[1])
				&& (fast[0] >= slow[0])) {		
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpCrossOver(double[] slow, double[] fast) 
			throws JFException {
		if ((fast[1] > fast[0]) && (fast[1] > slow[1])
				&& (fast[0] <= slow[0])) {
			return true;
		} else
			return false;
	}

	public void CreateHighBarList(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		rawBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		hBarList.addAll(rawBarList);
		lBarList.addAll(rawBarList);
		Collections.sort(hBarList, new IBarCompareHigh());
		Collections.sort(lBarList, new IBarCompareLow());
//		for (IBar bar : hBarList)
//			System.out.println(bar.getHigh());
		return ;
	}

	public void CreateLowBarList(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		lBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		
		return ;
	}

	public void setHighBar(IBar hBar) {
		this.highBar = hBar;
	}

	protected void LookForHLBar(Instrument instrument, Period period,
			OfferSide side, Filter filter, int numberOfCandlesBefore,
			long time, int numberOfCandlesAfter) {
	}
}
/*
 * public static void main(String[] args) throws Exception { RTChartInfo
 * rtChartInfo = new RTChartInfo();
 * 
 * } }
 */
