package singlejartest;

import charts.test.InteractiveRectangleDrawer.MyChartObjectAdapter;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.drawings.*;

import java.io.*;
import java.util.*;
import java.text.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.tictactec.ta.lib.*;

public class SMAStrategy implements IStrategy {
	private IEngine engine;
	private IConsole console;
	private IContext context=null;
	private IHistory history;
	private IChart chart;
	private IIndicators indicators;
	private int counter = 0;
	private double[] filteredSmma30;
	private double[] filteredSmma10;
	private double[] filteredSma5;
	private IOrder order = null;
	private IVerticalLineChartObject VLine;
	private IShortLineChartObject shortLine;
	private ISignalUpChartObject UpSignal;
	private ISignalDownChartObject DownSignal;
	private IChartObjectFactory factory;
	private int linecount = 0;
	private Core lib = new Core();
	private IBar Marubozu = null;
	private ArrayList<IBar> MarubozuLists;
	private CandlePattern cdlPattern;
	private ChartObjectListener ichartobjectlistener;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SMAStrategy.class);

	private List<IBar> highBarList;
	private RTChartInfo rtChartInfo;
	
	@Configurable("Instrument")
	public Instrument selectedInstrument = Instrument.EURUSD;
	@Configurable("Period")
	public Period selectedPeriod = Period.FIFTEEN_MINS;
	@Configurable("SMA filter")
	public Filter indicatorFilter = Filter.WEEKENDS;
	
	public void setInstance(MyChartObjectAdapter instance)
	{
		this.ichartobjectlistener=instance;
	}
	
	public IContext getContext(){
		if(this.context!=null){
			return this.context;
		}
		else {
			return null;
		}
	}
	public void onStart(IContext context) throws JFException {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
		this.chart = context.getChart(Instrument.EURUSD);
		factory = chart.getChartObjectFactory();
		MarubozuLists = new ArrayList<IBar>();
		cdlPattern = new CandlePattern();
		rtChartInfo=new RTChartInfo(context);
		IBar currBar = history.getBar(this.selectedInstrument, Period.FIFTEEN_MINS, OfferSide.ASK, 0);
		IBar prevDailyBar1 = history.getBar(this.selectedInstrument, Period.FIFTEEN_MINS, OfferSide.ASK, 1);
		rtChartInfo.initChart(Instrument.EURUSD,Period.FIFTEEN_MINS,400,currBar.getTime());
		drawSignalDown(rtChartInfo.crossPoint.crossBar);
		print("crossBar at " +rtChartInfo.crossPoint.crossBar+" smma: "+
				CrossPoint.smmaCrossPrice);
		
		
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		print(sdf.format(new Date(message.getCreationTime())) + " "
				+ message.getType() + message.getContent() + " "
				+ message.getOrder());

		// print("<html><font color=\"red\">"+message+"</font>");
	}

	public void onStop() throws JFException {
		for (IOrder order : engine.getOrders()) {
			engine.getOrder(order.getLabel()).close();
		}
		printMarubozu();
		print("version three");
//		chart.addIndicator(indicators.getIndicator("ZIGZAG"));
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {

	}

	public void onBar(Instrument instrument, Period period, IBar askBar,
			IBar bidBar) throws JFException {
		if (!instrument.equals(selectedInstrument)||!period.equals(Period.FIFTEEN_MINS)) {
			return;
		}
		Date prevBarTime = new Date();
		Date currBarTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		DateFormat fmt = DateFormat.getDateTimeInstance();

		IBar prevBar = history.getBar(instrument, selectedPeriod,
				OfferSide.BID, 1);
		IBar currBar = history.getBar(instrument, selectedPeriod,
				OfferSide.BID, 0);
		if (isFilterhey(currBar.getTime())) {
			return; 
		}
//		highBarList=rtChartInfo.getHighBarList(instrument, period, 120, prevBar.getTime());
		
		List<IBar> bars = history.getBars(instrument, Period.FIFTEEN_MINS,
				OfferSide.BID, indicatorFilter, 10 + 1, prevBar.getTime(), 0);
		RetCode retCode = cdlPattern.addMarubozu(MarubozuLists, bars);
		if (retCode != RetCode.Success) {
			LOGGER.error("Failed: " + retCode);
			return;
		}
		if (!MarubozuLists.isEmpty()) {
			if (MarubozuLists.get(MarubozuLists.size() - 1).getTime() == prevBar.getTime()){
//				drawshortLine(prevBar);
//				drawVLine(prevBar);
				drawSignalUp(prevBar);
				;
			}
		}

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		int[] output = new int[100];

		filteredSmma30 = indicators.smma(instrument, selectedPeriod,
				OfferSide.BID, AppliedPrice.CLOSE, 30, indicatorFilter, 2,
				prevBar.getTime(), 0);
		filteredSmma10 = indicators.smma(instrument, selectedPeriod,
				OfferSide.BID, AppliedPrice.CLOSE, 10, indicatorFilter, 2,
				prevBar.getTime(), 0);
		filteredSma5 = indicators.sma(instrument, selectedPeriod,
				OfferSide.BID, AppliedPrice.CLOSE, 5, indicatorFilter, 2,
				prevBar.getTime(), 0);

		// SMA10 crossover SMA90 from UP to DOWN ÏÂ´©
		if ((filteredSmma10[1] < filteredSmma10[0])
				&& (filteredSmma10[1] < filteredSmma30[1])
				&& (filteredSmma10[0] >= filteredSmma30[0])) {
			if (engine.getOrders().size() > 0) {
				for (IOrder orderInMarket : engine.getOrders()) {
					if (!orderInMarket.isLong()) {
						print("Closing Short position");
						orderInMarket.close();
					}
				}
			}
			if ((order == null)
					|| (!order.isLong() && order.getState().equals(
							IOrder.State.CLOSED))) {
				print("Create Buy");
				order = engine.submitOrder(getLabel(instrument), instrument,
						OrderCommand.BUY, 0.001);
			}

		}
		// SMA10 crossover SMA90 from DOWN to UP ÉÏ´©
		if ((filteredSmma10[1] > filteredSmma10[0])
				&& (filteredSmma10[1] > filteredSmma30[1])
				&& (filteredSmma10[0] <= filteredSmma30[0])) {
			if ((filteredSma5[1] > filteredSma5[0])
					&& (filteredSma5[1] > filteredSmma10[1])
					&& (filteredSma5[0] <= filteredSmma10[0])) {
				if (engine.getOrders().size() > 0) {
					for (IOrder orderInMarket : engine.getOrders()) {
						if (orderInMarket.isLong()) {
							print("Closing Long position");
							orderInMarket.close();
						}
					}
				}
				if ((order == null)
						|| (order.isLong() && order.getState().equals(
								IOrder.State.CLOSED))) {
					print("Create Sell");
					order = engine.submitOrder(getLabel(instrument),
							instrument, OrderCommand.SELL, 0.001);
				}
			}

		}
	}

	protected String getLabel(Instrument instrument) {
		String label = instrument.name();
		label = label + (counter++);
		label = label.toUpperCase();
		return label;
	}

	public void print(String message) {
		console.getOut().println(message);
	}

	/*
	 * public void writetofile() { File dirFile = context.getFilesDir(); if
	 * (!dirFile.exists()) { console.getErr().println(
	 * "Please create files directory in My Strategies"); context.stop(); } File
	 * file = new File(dirFile, "last10bars.txt");
	 * console.getOut().println("Writing to file " + file); try { PrintWriter pw
	 * = new PrintWriter(new FileOutputStream( file.toString(), true));
	 * prevBarTime.setTime(prevBar.getTime());
	 * currBarTime.setTime(currBar.getTime());
	 * pw.println(sdf.format(prevBarTime) + "," + sdf.format(currBarTime) + ","
	 * + (order == null) + "," + order.isLong() + "," +
	 * order.getState().equals(IOrder.State.CLOSED));
	 * 
	 * pw.close(); } catch (IOException e) {
	 * e.printStackTrace(console.getErr()); }
	 * 
	 * }
	 */

	protected void drawshortLine(IBar bar) {
		shortLine = factory.createShortLine(new Date(bar.getTime()).toString(),bar.getTime(),
				bar.getHigh()+0.0006,bar.getTime(),bar.getHigh()+0.0020);
//		shortLine = factory.createShortLine(new Date(bar.getTime()).toString());
//		shortLine.setTime(0, bar.getTime());
//		shortLine.setPrice(0, bar.getHigh());
		chart.addToMainChart(shortLine);
	}
	
	protected void drawVLine(IBar bar) {
		VLine = factory.createVerticalLine(new Date(bar.getTime()).toString());
		VLine.setTime(0, bar.getTime());
//		shortLine.setPrice(0, bar.getHigh());
		chart.addToMainChart(VLine);
	}
	
	public void drawSignalUp(IBar bar){
		UpSignal=factory.createSignalUp();
		UpSignal.setTime(0, bar.getTime());
		UpSignal.setPrice(0, bar.getHigh()+0.0010);
		chart.addToMainChart(UpSignal);
	}
	
	public void drawSignalDown(IBar bar){
		DownSignal=factory.createSignalDown();
		DownSignal.setTime(0, bar.getTime());
		DownSignal.setPrice(0, bar.getHigh()+0.0010);
		chart.addToMainChart(DownSignal);
	}
	
	public void printMarubozu()
	{
		if(MarubozuLists.isEmpty()) {
			print("The Marub is empty");
			return;
		}
		for(IBar bar:MarubozuLists)
		{
			print("The Marub is at:" +bar.getTime());
		}
	}

	protected boolean isFilterhey(long time) {
		int hour;
		GregorianCalendar cal = new GregorianCalendar();
		Date currBarTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		cal.setTimeInMillis(time);
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		currBarTime.setTime(time);
		if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.FRIDAY) {
			hour = cal.get(GregorianCalendar.HOUR_OF_DAY);

			if (hour >= 22) {
//				print(sdf.format(currBarTime) + " filterd OK");
				return true;
			} else
				return false;

		} else if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
//			print(sdf.format(currBarTime) + " filterd OK");
			return true;
		} else if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
			hour = cal.get(GregorianCalendar.HOUR_OF_DAY);
			if (hour < 22) {
//				print(sdf.format(currBarTime) + " filterd OK");
				return true;
			} else
				return false;
		} else {
			return false;
		}

	}
	
public	class MyChartObjectAdapter extends ChartObjectAdapter {
		
		@Override
		public void deleted(ChartObjectEvent e) {
			print("deleted " + VLine.getKey());
			// remove label as well
//			chart.remove(label);
		}

		@Override
		public void selected(ChartObjectEvent e) {
			print("selected OK  " + VLine.getKey());
		}

		@Override
		public void moved(ChartObjectEvent e) {
			// move the label to the middle of the rectangle
//			label.setPrice(0, getLabelPrice());
//			label.setTime(0, getLabelTime());
		}
		@Override
		public void attrChanged(ChartObjectEvent e){
			print("attrChanged OK");
			
		}
		@Override
		public void deselected(ChartObjectEvent e) {
			print("deselected OK");
		}

	}

}
