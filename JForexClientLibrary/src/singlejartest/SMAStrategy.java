package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.GregorianCalendar;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RequiresFullAccess
@Library("log4j-over-slf4j-1.6.4.jar;slf4j-api-1.6.4.jar")
//;slf4j-log4j12-1.5.8.jar;log4j-1.2.14.jar

public class SMAStrategy implements IStrategy {
    private IEngine engine;
    private IConsole console;
    private IContext context;
    private IHistory history;
    private IIndicators indicators;
    private int counter =0;
    private double [] filteredSma90;
    private double [] filteredSma10;
    private IOrder order = null;
    private GregorianCalendar cal;
    private Date currBarTime; 
    private Date prevBarTime;
    private SimpleDateFormat sdf;
    private DateFormat fmt;
//    private static final Logger LOGGER = LoggerFactory.getLogger(SMAStrategy.class);

    @Configurable("Instrument")
    public Instrument selectedInstrument = Instrument.EURUSD;
    @Configurable("Period")
    public Period selectedPeriod = Period.FIFTEEN_MINS;
    @Configurable("SMA filter")
    public Filter indicatorFilter = Filter.WEEKENDS;
    

    public void onStart(IContext context) throws JFException {
        this.context = context;
        this.engine = context.getEngine();
        this.console = context.getConsole();
        this.history = context.getHistory();
        this.indicators = context.getIndicators();
        this.cal = new GregorianCalendar();
        
       prevBarTime=new Date();
       currBarTime=new Date();
       sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
       sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
       fmt=DateFormat.getDateTimeInstance();

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
      
    }    

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
          if (!instrument.equals(selectedInstrument)||!period.equals(Period.FIFTEEN_MINS)) {
            return;
        }      
        IBar currBar= history.getBar(instrument, selectedPeriod, OfferSide.BID, 0);
        if(isFilter(currBar.getTime()) ){return;}
        
        IBar prevBar = history.getBar(instrument, selectedPeriod, OfferSide.BID, 1);
        
       filteredSma90 = indicators.smma(instrument, selectedPeriod, OfferSide.BID, AppliedPrice.CLOSE, 30,
                indicatorFilter, 2, prevBar.getTime(), 0);
        filteredSma10 = indicators.smma(instrument, selectedPeriod, OfferSide.BID, AppliedPrice.CLOSE, 10,
                indicatorFilter, 2, prevBar.getTime(), 0);

        // SMA10 crossover SMA90 from UP to DOWN
        if ((filteredSma10[1] < filteredSma10[0]) && (filteredSma10[1] < filteredSma90[1]) && (filteredSma10[0] >= filteredSma90[0])) {
            if (engine.getOrders().size() > 0) {
                for (IOrder orderInMarket : engine.getOrders()) {
                    if (!orderInMarket.isLong()) {
                        print("Closing Short position");
                        orderInMarket.close();
                    }
                }
            }
            if ((order == null) || (!order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                print("Create Buy");
                order = engine.submitOrder(getLabel(instrument), instrument, OrderCommand.BUY, 0.001);
            }
        
        }
        // SMA10 crossover SMA90 from DOWN to UP
        if ((filteredSma10[1] > filteredSma10[0]) && (filteredSma10[1] > filteredSma90[1]) && (filteredSma10[0] <= filteredSma90[0])) {
            if (engine.getOrders().size() > 0) {
                for (IOrder orderInMarket : engine.getOrders()) {
                    if (orderInMarket.isLong()) {
                        print("Closing Long position");
                        orderInMarket.close();
                        
                        File dirFile = context.getFilesDir();
                        if (!dirFile.exists()) {
                            console.getErr().println("Please create files directory in My Strategies");
                            context.stop();
                        }
                        File file = new File(dirFile, "last10bars.txt");
                        console.getOut().println("Writing to file " + file);
                        try {
                        PrintWriter pw = new PrintWriter(new FileOutputStream(file.toString(),true));
                        prevBarTime.setTime(prevBar.getTime());
                        currBarTime.setTime(currBar.getTime());
                        pw.println(sdf.format(prevBarTime)+","+sdf.format(currBarTime)+","+(order == null) +"," +order.isLong() + "," +  order.getState().equals(IOrder.State.CLOSED) );
               
                        pw.close();
                        } catch (IOException e) {
                        e.printStackTrace(console.getErr());
                        }
                    }
                }
            }
            if ((order == null) || (order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                  print("Create Sell");
                order = engine.submitOrder(getLabel(instrument), instrument, OrderCommand.SELL, 0.001);
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
    
    protected boolean isFilter(long time)
    {
        int day,hour;
        cal.setTimeInMillis(time);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        day=cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        if(cal.get(GregorianCalendar.DAY_OF_WEEK)==GregorianCalendar.FRIDAY)
        {
           hour=cal.get(GregorianCalendar.HOUR_OF_DAY );
 //          currBarTime.setTime(time);
           if(hour>=22)
           {
        	   print(sdf.format(currBarTime)+" filterd OK");
        	   return true;
           }
           else
        	   return false;
           
           
        }
        else
            return false;
        
    }

}