package singlejartest;

import java.util.*;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;


public class TrendInfo {

	public enum TrendType {
		Rectangle, UpTrend, DownTrend, HighWave, LowWave,
	}

	private IEngine engine;
	private IConsole console;
	private IContext context = null;
	private IHistory history;
	private IIndicators indicators;
	private IChart chart;

	private List<Integer> hBarLocation = new ArrayList<Integer>();
	private List<Integer> lBarLocation = new ArrayList<Integer>();

	private List<IBar> hBarList = new ArrayList<IBar>();
	private List<IBar> lBarList = new ArrayList<IBar>();
	private List<IBar> rawBarList;
	public  final static int TrendLength=120;
	public final static int RightInterval=20;
	public final static int LeftInterval=10;

	private IBar lBar1_120period;
	private IBar lBar2_120period;
	private IBar lBar3_120period;
	public static TrendType trendType=null;
	
	public TrendInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void findTrend(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		
		CreateHLBarsList(instrument, period, trendLength, time);
		hBarLocation.add(searchBarsList(hBarList.get(0).getTime(), rawBarList));
		hBarLocation.add(searchBarsList(hBarList.get(1).getTime(), rawBarList));
		hBarLocation.add(searchBarsList(hBarList.get(2).getTime(), rawBarList));

		lBarLocation.add(searchBarsList(lBarList.get(0).getTime(), rawBarList));
		lBarLocation.add(searchBarsList(lBarList.get(1).getTime(), rawBarList));
		lBarLocation.add(searchBarsList(lBarList.get(2).getTime(), rawBarList));
		
		if(hBarLocation.get(0)>TrendLength-RightInterval&&hBarLocation.get(1)>TrendLength-RightInterval
				&&hBarLocation.get(2)>TrendLength-RightInterval&&hBarList.get(0).getClose()-lBarList.get(0).getClose()>0.0080){
			TrendInfo.trendType=TrendType.UpTrend;		
		}
		else if(hBarLocation.get(0)<LeftInterval&&hBarLocation.get(1)<LeftInterval&&hBarLocation.get(2)<LeftInterval
				&&hBarList.get(0).getClose()-lBarList.get(0).getClose()>0.0080){
			TrendInfo.trendType=TrendType.DownTrend;	
		}
		else if(hBarList.get(0).getClose()-lBarList.get(0).getClose()<0.0050){
			TrendInfo.trendType=TrendType.Rectangle;
		}
		
	}

	protected int searchBarsList(long time, List<IBar> barsList)
			throws JFException {
		int i = 0;
		for (IBar bar : barsList) {
			if (time == bar.getTime()) {
				return i;
			}
			i++;
		}
		throw new JFException("can't find the element");
	}

	public void CreateHLBarsList(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		rawBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, trendLength, time, 0);
		hBarList.addAll(rawBarList);
		lBarList.addAll(rawBarList);
		Collections.sort(hBarList, new IBarCompareHigh());
		Collections.sort(lBarList, new IBarCompareLow());
		// for (IBar bar : hBarList)
		// System.out.println(bar.getHigh());
		return;
	}

	public void CreateLowBarList(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		lBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, trendLength, time, 0);

		return;
	}

}
