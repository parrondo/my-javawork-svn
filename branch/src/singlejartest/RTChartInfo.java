package singlejartest;

import java.util.*;

import singlejartest.CrossPoint.CrossType;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

public class RTChartInfo {
	private IBar highBar;
	private IBar lowBar;
	
	private TrendInfo trendInfo=null;
	private MAInfo maInfo=null;
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
			int initBarNum, long time) throws JFException {
		crossPoint=new CrossPoint(null);
		maInfo=new MAInfo(context);
		maInfo.initSMMA1030Cross(instrument, period, initBarNum, time);
		if(crossPoint.getCrossType()==null){
			System.out.println("bars number is not enough");
			System.exit(0);
		}	
//		CreateHighBarList(instrument,period,numberOfCandlesBefore,time);
//		CreateLowBarList(instrument,period,numberOfCandlesBefore,time);
		trendInfo=new TrendInfo(context);
		trendInfo.findTrend(instrument, period,TrendInfo.TrendLength, time);
	}
	
	public void updateChart(Instrument instrument, Period period,
			long time)throws JFException{
		trendInfo.findTrend(instrument, period,TrendInfo.TrendLength, time);
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
