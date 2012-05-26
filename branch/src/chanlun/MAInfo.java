package chanlun;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import chanlun.CrossPoint.CrossType;

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
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class MAInfo {

	private IEngine engine;
	private IConsole console;
	private IContext context = null;
	private IHistory history;
	private IIndicators indicators;
	private IChart chart;

	private CrossPoint smma1030Cross = new CrossPoint(null);

	private List<CrossPoint> sma510CrossList = new ArrayList<CrossPoint>();

	private static final Logger LOGGER = LoggerFactory.getLogger(MAInfo.class);

	public MAInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void initSMMA1030Cross(Instrument instrument, Period period,
			int initBarNum, long time) throws JFException {
		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, initBarNum, time, 0);

		for (IBar bar : barsList) {
			updateSMMA1030Cross(instrument, period, bar);
		}
	}
	
	public void printMAInfo(){
		
		 LOGGER.debug("SMMA1030CP:"+TimeZoneFormat.GMTFormat(smma1030Cross.getCrossBar().getTime()));
		 LOGGER.debug("SMA510CPNUM:"+sma510CrossList.size());
		 if(sma510CrossList.size()>0){
			 LOGGER.debug("FirstSMMA5010CP:"+TimeZoneFormat.GMTFormat(sma510CrossList.get(0).getCrossBar().getTime()));
		 }
		 else {
			 LOGGER.debug("FirstSMMA5010CP: NULL");
		}
	}
	
	public boolean updateSMMA1030Cross(Instrument instrument, Period period,
			IBar bar) throws JFException {

		double[] smma30 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 30, Filter.WEEKENDS, 2, bar.getTime(), 0);
		double[] smma10 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2, bar.getTime(), 0);

		return updateCrossPoint(smma1030Cross, smma10, smma30, bar);

	}

	public void initSMA510CPList(Instrument instrument, Period period,
			int initBarNum, long time) throws JFException {
		if (smma1030Cross == null) {
			LOGGER.error("smma1030Croos was not init!");
			throw new JFException("smma1030Croos was not init!");
		}

		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, initBarNum, time, 0);

		for (IBar bar : barsList) {
			if (bar.getTime() < smma1030Cross.getCrossBar().getTime()) {
				continue;
			}
			updateSMA510CPList(instrument, period, bar);
		}
	}

	public void updateSMA510CPList(Instrument instrument, Period period,
			IBar bar) throws JFException {
		double[] smma10 = indicators.smma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2, bar.getTime(), 0);
		double[] sma5 = indicators.sma(instrument, period, OfferSide.BID,
				AppliedPrice.CLOSE, 5, Filter.WEEKENDS, 2, bar.getTime(), 0);

		CrossPoint lc_sma510Cross = new CrossPoint(null);
		if (updateSMMA1030Cross(instrument, period, bar)) {
			sma510CrossList.clear();
		}
		if (updateCrossPoint(lc_sma510Cross, sma5, smma10, bar)) {
			sma510CrossList.add(lc_sma510Cross);
		}
		// 1030 510½»²æÍ¬ÏòºöÂÔ
		if (sma510CrossList.size() > 0) {
			if (smma1030Cross.getCrossType() == sma510CrossList.get(0).getCrossType()) {
				sma510CrossList.remove(0);
			}
		}
	}

	protected boolean updateCrossPoint(CrossPoint crossPoint, double[] fast,
			double[] slow, IBar bar) throws JFException {
		if (isUpCrossOver(fast, slow)) {
			crossPoint.setCrossType(CrossType.UpCross);
			crossPoint.setTime(bar.getTime());
			crossPoint.setCrossBar(bar);
			crossPoint.setCrossPrice(slow[1]);
			return true;
		}
		if (isDownCrossOver(fast, slow)) {
			crossPoint.setCrossType(CrossType.DownCross);
			crossPoint.setTime(bar.getTime());
			crossPoint.setCrossBar(bar);
			crossPoint.setCrossPrice(fast[1]);
			return true;
		}
		return false;
	}

	public boolean isDownCrossOver(double[] fast, double[] slow)
			throws JFException {
		if ((fast[1] < fast[0]) && (fast[1] < slow[1]) && (fast[0] >= slow[0])) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpCrossOver(double[] fast, double[] slow)
			throws JFException {
		if ((fast[1] > fast[0]) && (fast[1] > slow[1]) && (fast[0] <= slow[0])) {
			return true;
		} else
			return false;
	}

	public CrossPoint getSmma1030CP() {
		return smma1030Cross;
	}

	public void setSmma1030CP(CrossPoint smma1030Cross) {
		this.smma1030Cross = smma1030Cross;
	}

	public List<CrossPoint> getSma510CPList() {
		return sma510CrossList;
	}

	public void setSma510CPList(List<CrossPoint> sma510CrossList) {
		this.sma510CrossList = sma510CrossList;
	}

}
