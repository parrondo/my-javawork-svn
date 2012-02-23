/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract interface ITesterReport
/*     */ {
/*     */   public abstract InstrumentReportData getInstrumentReportData(Instrument paramInstrument);
/*     */ 
/*     */   public abstract void setInstrumentReportData(Instrument paramInstrument, InstrumentReportData paramInstrumentReportData);
/*     */ 
/*     */   public abstract InstrumentReportData getOrCreateInstrumentReportData(Instrument paramInstrument);
/*     */ 
/*     */   public abstract void setLastTick(Instrument paramInstrument, ITick paramITick);
/*     */ 
/*     */   public abstract void addEvent(TesterReportData.TesterEvent paramTesterEvent);
/*     */ 
/*     */   public abstract List<TesterReportData.TesterEvent> getEvents();
/*     */ 
/*     */   public abstract void addTurnover(double paramDouble);
/*     */ 
/*     */   public abstract void addCommission(double paramDouble);
/*     */ 
/*     */   public abstract void setFinishDeposit(double paramDouble);
/*     */ 
/*     */   public abstract String getStrategyName();
/*     */ 
/*     */   public abstract List<String[]> getParameterValues();
/*     */ 
/*     */   public abstract long getFrom();
/*     */ 
/*     */   public abstract long getTo();
/*     */ 
/*     */   public abstract double getCommission();
/*     */ 
/*     */   public abstract double getTurnover();
/*     */ 
/*     */   public abstract double getInitialDeposit();
/*     */ 
/*     */   public abstract double getFinishDeposit();
/*     */ 
/*     */   public abstract void perfInitiate();
/*     */ 
/*     */   public abstract void perfStart(long paramLong);
/*     */ 
/*     */   public abstract long perfLast();
/*     */ 
/*     */   public abstract void perfAdd(PerfStats paramPerfStats, long paramLong);
/*     */ 
/*     */   public abstract void perfStop(long paramLong);
/*     */ 
/*     */   public abstract long[] getPerfStats();
/*     */ 
/*     */   public abstract int[] getPerfStatCounts();
/*     */ 
/*     */   public static enum PerfStats
/*     */   {
/* 115 */     READ_DATA("Ticks/Bars data reads"), 
/* 116 */     WRITE_DATA("Ticks/Bars/Account Info data writing for charts"), 
/* 117 */     ACCOUNT_INFO_CALCS("Account information calculations"), 
/* 118 */     STOP_ORDERS("Conditional orders processing"), 
/* 119 */     HISTORY_CALLS("Historical data calls (including indicators data calls)"), 
/* 120 */     INDICATOR_CALLS("Indicator calculations"), 
/* 121 */     ORDER_CHANGES("Order changes processing \"on server side\""), 
/* 122 */     MC_CHECK("Margin call/cut checks"), 
/* 123 */     ON_START("onStart method calls"), 
/* 124 */     ON_STOP("onStop method calls"), 
/* 125 */     ON_TICK("onTick method calls"), 
/* 126 */     ON_BAR("onBar method calls"), 
/* 127 */     ON_ACCOUNT("onAccount method calls"), 
/* 128 */     ON_MESSAGE("onMessage method calls"), 
/* 129 */     USER_TASKS("User tasks (IContext.executeTask) processing"), 
/* 130 */     OTHER("Other operations"), 
/* 131 */     TICK_BAR_PROCESSING("Ticks/Bars internal processing");
/*     */ 
/*     */     private String text;
/*     */ 
/* 136 */     private PerfStats(String text) { this.text = text;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 141 */       return this.text;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.ITesterReport
 * JD-Core Version:    0.6.0
 */