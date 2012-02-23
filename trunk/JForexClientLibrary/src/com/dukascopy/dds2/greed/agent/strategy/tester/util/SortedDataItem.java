/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.util;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.TimedData;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ 
/*    */ public class SortedDataItem
/*    */ {
/*    */   private Instrument instrument;
/*    */   private JForexPeriod jForexPeriod;
/*    */   private TimedData askBar;
/*    */   private TimedData bidBar;
/*    */ 
/*    */   public SortedDataItem()
/*    */   {
/* 19 */     this(null, null, null, null);
/*    */   }
/*    */ 
/*    */   public SortedDataItem(Instrument instrument, JForexPeriod jForexPeriod, TimedData askBar, TimedData bidBar)
/*    */   {
/* 29 */     this.instrument = instrument;
/* 30 */     this.jForexPeriod = jForexPeriod;
/* 31 */     this.askBar = askBar;
/* 32 */     this.bidBar = bidBar;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument() {
/* 36 */     return this.instrument;
/*    */   }
/*    */   public void setInstrument(Instrument instrument) {
/* 39 */     this.instrument = instrument;
/*    */   }
/*    */   public JForexPeriod getJForexPeriod() {
/* 42 */     return this.jForexPeriod;
/*    */   }
/*    */   public void setJForexPeriod(JForexPeriod jForexPeriod) {
/* 45 */     this.jForexPeriod = jForexPeriod;
/*    */   }
/*    */   public TimedData getAskBar() {
/* 48 */     return this.askBar;
/*    */   }
/*    */   public void setAskBar(TimedData askBar) {
/* 51 */     this.askBar = askBar;
/*    */   }
/*    */   public TimedData getBidBar() {
/* 54 */     return this.bidBar;
/*    */   }
/*    */   public void setBidBar(TimedData bidBar) {
/* 57 */     this.bidBar = bidBar;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return String.valueOf(getInstrument()) + " " + String.valueOf(getJForexPeriod()) + " " + String.valueOf(getAskBar()) + " " + String.valueOf(getBidBar());
/*    */   }
/*    */ 
/*    */   public long getAskOrBidBarTime()
/*    */   {
/* 69 */     TimedData bar = getAskOrBidData();
/* 70 */     return bar.getTime();
/*    */   }
/*    */ 
/*    */   public TimedData getAskOrBidData() {
/* 74 */     TimedData bar = getAskBar() == null ? getBidBar() : getAskBar();
/* 75 */     return bar;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataItem
 * JD-Core Version:    0.6.0
 */