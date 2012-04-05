package singlejartest;

import java.util.*;

import singlejartest.CrossPoint.CrossType;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

public class RTChartInfo {
	private IBar highBar;
	private IBar lowBar;
	private List<IBar> hBarList;
	private List<IBar> lBarList;
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
		
	}

	public void findFirstCross(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		
		for (IBar bar : barsList) {
			if(isUpCrossOver(instrument, period, bar.getTime())){
				crossPoint.setCrossType(CrossType.UpCross);
				crossPoint.setTime(bar.getTime());
				CrossPoint.crossBar=bar;
			}
			if(isDownCrossOver(instrument, period, bar.getTime())){
				crossPoint.setCrossType(CrossType.DownCross);
				crossPoint.setTime(bar.getTime());
				CrossPoint.crossBar=bar;
			}
		}

	}

	public boolean isDownCrossOver(Instrument instrument, Period period, long time)
			throws JFException {

		double[] Smma30 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 30, Filter.WEEKENDS, 2, time, 0);
		double[] Smma10 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2, time, 0);
		double[] Sma30 = indicators.sma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 5, Filter.WEEKENDS, 2, time, 0);

		if ((Smma10[1] < Smma10[0]) && (Smma10[1] < Smma30[1])
				&& (Smma10[0] >= Smma30[0])) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isUpCrossOver(Instrument instrument, Period period,
			long time) throws JFException {

		double[] Smma30 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 30, Filter.WEEKENDS, 2, time, 0);
		double[] Smma10 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2, time, 0);
		double[] Sma30 = indicators.sma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 5, Filter.WEEKENDS, 2, time, 0);

		if ((Smma10[1] > Smma10[0]) && (Smma10[1] > Smma30[1])
				&& (Smma10[0] <= Smma30[0])) {
			return true;
		} else
			return false;
	}

	public List<IBar> getHighBarList(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		hBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		Collections.sort(hBarList, new IBarCompareHigh());
//		for (IBar bar : hBarList)
//			System.out.println(bar.getHigh());
		return this.hBarList;
	}

	public List<IBar> getLowBarList(Instrument instrument, Period period,
			int numberOfCandlesBefore, long time) throws JFException {
		lBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		Collections.sort(lBarList, new IBarCompareLow());
		return this.lBarList;
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
