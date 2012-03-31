package singlejartest;

import java.util.*;

import com.dukascopy.api.*;

public class RTChartInfo {
	private IBar highBar;
	private IBar lowBar;
	private List<IBar> hBarList;
	private List<IBar> lBarList;
	private TrendInfo trendInfo;
	public final int HLBARSHIFT = 40;

	private IEngine engine;
	private IConsole console;
	private IContext context = null;
	private IHistory history;
	private IChart chart;


	public RTChartInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
	}

	public List<IBar> getHighBarList(Instrument instrument,Period period,
			int numberOfCandlesBefore,long time)  throws JFException {
		hBarList=history.getBars(instrument, period,OfferSide.BID, Filter.WEEKENDS, numberOfCandlesBefore, time, 0);
		Collections.sort(hBarList,new IBarCompareHigh());
		for(IBar bar:hBarList)
			System.out.println(bar.getHigh());
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
	public static void main(String[] args) throws Exception {
		RTChartInfo  rtChartInfo = new RTChartInfo();
		
	}
}
*/
