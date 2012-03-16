package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import java.io.*;
import java.util.*;

public class SMMA_ONTICK implements IStrategy {
	private IEngine engine;
	private IConsole console;
	private IHistory history;
	private IIndicators indicators;
	private int counter =0;
	private double [] filteredSma90;
	private double [] filteredSma10;
	private IOrder order = null;
	  private IContext context;

	@Configurable("Instrument")
	public Instrument selectedInstrument = Instrument.EURUSD;
	@Configurable("Period")
	public Period selectedPeriod = Period.ONE_MIN;
	@Configurable("SMA filter")
	public Filter indicatorFilter = Filter.WEEKENDS;
	

	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
		 this.context = context;
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
		for (IOrder order : engine.getOrders()) {
			engine.getOrder(order.getLabel()).close();
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
		if (!instrument.equals(selectedInstrument)) {
			return;
		}
		/*
		 File dirFile = context.getFilesDir();
		   if (!dirFile.exists()) {
	            console.getErr().println("Please create files directory in My Strategies");
	            context.stop();
	        }
	        File file = new File(dirFile, "last10bars.txt");
	        console.getOut().println("Writing to file " + file);
	        try {
	            PrintWriter pw = new PrintWriter(new FileOutputStream(file.toString(),true));
	           
	            pw.println(tick.getTime() + "," + tick.getAsk() + "," + tick.getBid() + "," + tick.getAskVolume());
	          
	            pw.close();
	        } catch (IOException e) {
	            e.printStackTrace(console.getErr());
	            
	        }
	        */
		IBar prevBar = history.getBar(instrument, selectedPeriod, OfferSide.BID, 1);
		filteredSma90 = indicators.smma(instrument, selectedPeriod, OfferSide.BID, AppliedPrice.CLOSE, 30,
				indicatorFilter, 2, prevBar.getTime(), 0);
		filteredSma10 = indicators.smma(instrument, selectedPeriod, OfferSide.BID, AppliedPrice.CLOSE, 10,
				indicatorFilter, 2, prevBar.getTime(), 0);

		// SMA10 crossover SMA90 from UP to DOWN
		if ((filteredSma10[1] < filteredSma10[0]) && (filteredSma10[1] < filteredSma90[1]) && (filteredSma10[0] >= filteredSma90[0])) {
			if (engine.getOrders().size() > 0) {
				for (IOrder orderInMarket : engine.getOrders()) {
					if (orderInMarket.isLong()) {
						print("Closing Long position");
						orderInMarket.close();
					}
				}
			}
			if ((order == null) || (order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
				print("Create SELL");
				order = engine.submitOrder(getLabel(instrument), instrument, OrderCommand.SELL, 0.01);
			}
		}
		// SMA10 crossover SMA90 from DOWN to UP
		if ((filteredSma10[1] > filteredSma10[0]) && (filteredSma10[1] > filteredSma90[1]) && (filteredSma10[0] <= filteredSma90[0])) {
			if (engine.getOrders().size() > 0) {
				for (IOrder orderInMarket : engine.getOrders()) {
					if (!orderInMarket.isLong()) {
						print("Closing Short position");
						orderInMarket.close();
					}
				}
			}
			if ((order == null) || (!order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
				order = engine.submitOrder(getLabel(instrument), instrument, OrderCommand.BUY, 0.01);
			}
		}
	}	

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
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

}