package chanlun;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	private static final Logger LOGGER = LoggerFactory.getLogger(TrendInfo.class);
	
	public TrendInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void findTrend(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		
		hBarList=CreateHighBarsList(instrument, period, trendLength, time);
		lBarList=CreateLowBarList(instrument, period, trendLength, time);
		
		for(int i=0;i<3;i++){
			hBarLocation.add(searchBarsList(hBarList.get(i).getTime(), rawBarList));
			lBarLocation.add(searchBarsList(hBarList.get(i).getTime(), rawBarList));
		}
	
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
	public void printTrendInfo(){
		LOGGER.debug("CurrentTrend:"+TrendInfo.trendType);
		LOGGER.debug("Highest:"+TimeZoneFormat.GMTFormat(hBarList.get(0).getTime()));
		LOGGER.debug("Lowest :"+TimeZoneFormat.GMTFormat(lBarList.get(0).getTime()));		
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

	public List<IBar>  CreateHighBarsList(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		rawBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, trendLength, time, 0);
		List<IBar> hBars=new ArrayList<IBar>();
		hBars.addAll(rawBarList);
		Collections.sort(hBars, new IBarCompareHigh());
		// for (IBar bar : hBarList)
		// System.out.println(bar.getHigh());
		return hBars;
	}

	public List<IBar> CreateLowBarList(Instrument instrument, Period period,
			int trendLength, long time) throws JFException {
		rawBarList = history.getBars(instrument, period, OfferSide.BID,
				Filter.WEEKENDS, trendLength, time, 0);
		List<IBar> lBars=new ArrayList<IBar>();
		lBars.addAll(rawBarList);
		Collections.sort(lBars, new IBarCompareLow());
		return lBars;
	}
	
	public List<IBar> gethBarList() {
		return hBarList;
	}

	public void sethBarList(List<IBar> hBarList) {
		this.hBarList = hBarList;
	}
	
	public List<IBar> getlBarList() {
		return lBarList;
	}

	public void setlBarList(List<IBar> lBarList) {
		this.lBarList = lBarList;
	}

}
