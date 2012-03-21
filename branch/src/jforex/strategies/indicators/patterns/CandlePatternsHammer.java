package jforex.strategies.indicators.patterns;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IndicatorInfo;

/**
 * The strategy demonstrates how to use hammer candlestick patter indicator:
 * - On its start (i.e. in the onStart method) finds hammer pattern occurrences 
 *   over the last 100 candlesticks and prints to console the latest occurrence by shift.
 * - On every candlestick (i.e. in the onBar method) checks if it is of hammer pattern.
 * Also the strategy plots the indicator on chart.
 *
 */

@RequiresFullAccess
public class CandlePatternsHammer implements IStrategy {
	
	@Configurable("")
	public Period period = Period.TEN_MINS;
	@Configurable("")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("")
	public OfferSide side = OfferSide.BID;
	@Configurable("")
	public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
	@Configurable("")
	public int candleCount = 100;
	@Configurable("Plot on chart?")
	public boolean plotOnChart = true;
	
	private String indName = "CDLHAMMER";
	
	private IIndicators indicators;
	private IConsole console;
	private IHistory history;
	private IContext context;
	private IChart chart;	

	@Override
	public void onStart(IContext context) throws JFException {
		indicators = context.getIndicators();
		console = context.getConsole();
		history = context.getHistory();
		chart = context.getChart(instrument);
		this.context = context;
				
		int candlesBefore = candleCount, candlesAfter = 0;
		long currBarTime = history.getBar(instrument, period, side, 0).getTime();
		
		IIndicator indicator = context.getIndicators().getIndicator(indName);

		Object[] patternUni = indicators.calculateIndicator(instrument, period, new OfferSide[] { side }, indName,
				new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { }, Filter.NO_FILTER, candlesBefore, currBarTime, candlesAfter);

		//all candle patterns have just one output - we're good with 1-dimensional array
		int[] values = (int[]) patternUni[0];
		Set<Integer> occurrences = new LinkedHashSet<Integer>();

		for(int i=0; i < values.length; i++){
			int shift = values.length - 1 - i;
			if(values[i] != 0){
				occurrences.add(shift);
			}
		}
		int lastOccurrence = occurrences.isEmpty() 
			? -1 
			: Collections.min(occurrences);
		
		print(String.format("%s pattern occurances over last %s bars=%s; last occurrence shift=%s; all occurences: %s",
				indicator.getIndicatorInfo().getTitle(), candleCount,occurrences.size(), lastOccurrence, occurrences.toString()
				));			
		
		if (plotOnChart) {
			chart.addIndicator(indicator);
		}

	}
	
	private void print(Object o){
	    console.getOut().println(o);
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		
		if(instrument != this.instrument || period != this.period){
			return;
		}

		IIndicator indicator = context.getIndicators().getIndicator(indName);
		IndicatorInfo info = indicator.getIndicatorInfo();
		
		//shift of just finished bar
		int shift = 1;
		Object[] patternUni = indicators.calculateIndicator(instrument, period, new OfferSide[] { side }, indName,
				new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { }, shift);

		//all candle patterns have just one output - we're good with 1-dimensional array
		int patternValue = (Integer) patternUni[0];

		if(patternValue != 0){
			print(String.format("%s pattern of value %s occurred at bar: %s",
					info.getTitle(), patternValue, bidBar.toString() ));	
		}				
			
	}	

	@Override
	public void onMessage(IMessage message) throws JFException {}

	@Override
	public void onAccount(IAccount account) throws JFException {}

	@Override
	public void onStop() throws JFException {}

}
