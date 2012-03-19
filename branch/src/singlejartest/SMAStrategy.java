package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.drawings.*;

import java.io.*;
import java.util.*;
import java.text.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.tictactec.ta.lib.*;

public class SMAStrategy implements IStrategy {
	private IEngine engine;
	private IConsole console;
	private IContext context;
	private IHistory history;
	private IChart chart;
	private IIndicators indicators;
	private int counter = 0;
	private double[] filteredSmma30;
	private double[] filteredSmma10;
	private double[] filteredSma5;
	private IOrder order = null;
	private IVerticalLineChartObject VLine;
	private IChartObjectFactory factory;
	private int linecount = 0;
	private Core lib = new Core();
	private IBar Marubozu = null;

	@Configurable("Instrument")
	public Instrument selectedInstrument = Instrument.EURUSD;
	@Configurable("Period")
	public Period selectedPeriod = Period.ONE_HOUR;
	@Configurable("SMA filter")
	public Filter indicatorFilter = Filter.WEEKENDS;

	public void onStart(IContext context) throws JFException {
		this.context = context;
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
		this.chart = context.getChart(Instrument.EURUSD);
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
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {

	}

	public void onBar(Instrument instrument, Period period, IBar askBar,
			IBar bidBar) throws JFException {
		if (!instrument.equals(selectedInstrument)) {
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
		/*
		 * factory = chart.getChartObjectFactory(); VLine =
		 * factory.createVerticalLine("VerticalLine"+linecount); linecount++;
		 * VLine.setTime(0,currBar.getTime()); chart.addToMainChart(VLine);
		 * 
		 * MInteger outBegIdx = new MInteger(); MInteger outNbElement = new
		 * MInteger(); int[] output = new int[100];
		 */

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
				order = engine.submitOrder(getLabel(instrument), instrument,
						OrderCommand.SELL, 0.001);
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
	public void writetofile() {
		File dirFile = context.getFilesDir();
		if (!dirFile.exists()) {
			console.getErr().println(
					"Please create files directory in My Strategies");
			context.stop();
		}
		File file = new File(dirFile, "last10bars.txt");
		console.getOut().println("Writing to file " + file);
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(
					file.toString(), true));
			prevBarTime.setTime(prevBar.getTime());
			currBarTime.setTime(currBar.getTime());
			pw.println(sdf.format(prevBarTime) + "," + sdf.format(currBarTime)
					+ "," + (order == null) + "," + order.isLong() + ","
					+ order.getState().equals(IOrder.State.CLOSED));

			pw.close();
		} catch (IOException e) {
			e.printStackTrace(console.getErr());
		}

	}
	*/

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
				print(sdf.format(currBarTime) + " filterd OK");
				return true;
			} else
				return false;

		} else if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
			print(sdf.format(currBarTime) + " filterd OK");
			return true;
		} else if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
			hour = cal.get(GregorianCalendar.HOUR_OF_DAY);
			if (hour < 22) {
				print(sdf.format(currBarTime) + " filterd OK");
				return true;
			} else
				return false;
		} else {
			return false;
		}

	}

}
