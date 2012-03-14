package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import java.io.*;
import java.util.*;
import java.text.*;

public class GetBar implements IStrategy {
    private IEngine engine;
    private IConsole console;
    private IContext context;
    private IHistory history;
    private IIndicators indicators;
    private int counter =0;
    private double [] filteredSma90;
    private double [] filteredSma10;
    private IOrder order = null;

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
          if (!instrument.equals(selectedInstrument)) {
            return;
        }
        Date prevBarTime=new Date();
        Date currBarTime=new Date();
         SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
         sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        DateFormat fmt=DateFormat.getDateTimeInstance();

        IBar prevBar = history.getBar(instrument, selectedPeriod, OfferSide.BID, 1);
        IBar currBar= history.getBar(instrument, selectedPeriod, OfferSide.BID, 0);
                        
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
                        currBarTime.setTime(bidBar.getTime());
                        pw.println(sdf.format(prevBarTime)+","+sdf.format(currBarTime)+"," + currBar.getOpen() + "," + currBar.getClose() + "," 
                        + currBar.getHigh() + "," + currBar.getLow() + "," + currBar.getVolume()+","+period.name());
               
                        pw.close();
                        } catch (IOException e) {
                        e.printStackTrace(console.getErr());
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

}
