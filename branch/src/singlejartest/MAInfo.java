package singlejartest;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import singlejartest.CrossPoint.CrossType;

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

	private CrossPoint smma1030Cross =new CrossPoint(null);
	private List<CrossPoint> sma510CrossList= new ArrayList<CrossPoint>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MAInfo.class);

	public MAInfo(IContext context) {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void findFirstSMMA1030Cross(Instrument instrument, Period period,
			int initBarNum, long time) throws JFException {
		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, initBarNum, time, 0);

		for (IBar bar : barsList) {
			double[] smma30 = indicators.smma(instrument, period,
					OfferSide.BID, AppliedPrice.CLOSE, 30, Filter.WEEKENDS, 2,
					bar.getTime(), 0);
			double[] smma10 = indicators.smma(instrument, period,
					OfferSide.BID, AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2,
					bar.getTime(), 0);

			if (isUpCrossOver(smma10, smma30)) {
				smma1030Cross.setCrossType(CrossType.UpCross);
				smma1030Cross.setTime(bar.getTime());
				smma1030Cross.setCrossBar(bar);
				smma1030Cross.setCrossPrice(smma30[1]);
			}
			if (isDownCrossOver(smma10, smma30)) {
				smma1030Cross.setCrossType(CrossType.DownCross);
				smma1030Cross.setTime(bar.getTime());
				smma1030Cross.setCrossBar(bar);
				smma1030Cross.setCrossPrice(smma10[1]);
			}
		}
	}

	public void findSMA510Cross(Instrument instrument, Period period,
			int initBarNum, long time) throws JFException {
		if (smma1030Cross == null) {
			LOGGER.error("smma1030Croos was not init!");
			throw new JFException("smma1030Croos was not init!");
		}
		
		List<IBar> barsList = history.getBars(instrument, period,
				OfferSide.BID, Filter.WEEKENDS, initBarNum, time, 0);

		for (IBar bar : barsList) {
			if (bar.getTime() < smma1030Cross.getCrossBar().getTime())
				continue;

			double[] smma10 = indicators.smma(instrument, period,
					OfferSide.BID, AppliedPrice.CLOSE, 10, Filter.WEEKENDS, 2,
					bar.getTime(), 0);
			double[] sma5 = indicators
					.sma(instrument, period, OfferSide.BID, AppliedPrice.CLOSE,
							5, Filter.WEEKENDS, 2, bar.getTime(), 0);
			
			if (isUpCrossOver(sma5, smma10)) {
				CrossPoint sma510UpCross=new CrossPoint(null);
				sma510UpCross.setCrossType(CrossType.UpCross);
				sma510UpCross.setTime(bar.getTime());
				sma510UpCross.setCrossBar(bar);
				sma510UpCross.setCrossPrice(smma10[1]);
				sma510CrossList.add(sma510UpCross);
			}
			if (isDownCrossOver(sma5, smma10)) {
				CrossPoint sma510DownCross=new CrossPoint(null);
				sma510DownCross.setCrossType(CrossType.DownCross);
				sma510DownCross.setTime(bar.getTime());
				sma510DownCross.setCrossBar(bar);
				sma510DownCross.setCrossPrice(sma5[1]);
				sma510CrossList.add(sma510DownCross);
			}
		}

	}

	public boolean isDownCrossOver(double[] fast, double[] slow )
			throws JFException {
		if ((fast[1] < fast[0]) && (fast[1] < slow[1]) && (fast[0] >= slow[0])) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpCrossOver(double[] fast,double[] slow )
			throws JFException {
		if ((fast[1] > fast[0]) && (fast[1] > slow[1]) && (fast[0] <= slow[0])) {
			return true;
		} else
			return false;
	}
	public CrossPoint getSmma1030Cross() {
		return smma1030Cross;
	}

	public void setSmma1030Cross(CrossPoint smma1030Cross) {
		this.smma1030Cross = smma1030Cross;
	}
	

	public List<CrossPoint> getSma510CrossList() {
		return sma510CrossList;
	}

	public void setSma510CrossList(List<CrossPoint> sma510CrossList) {
		this.sma510CrossList = sma510CrossList;
	}

}
