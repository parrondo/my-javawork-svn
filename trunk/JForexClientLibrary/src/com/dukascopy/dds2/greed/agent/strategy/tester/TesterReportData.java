/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TesterReportData
/*     */   implements ITesterReport
/*     */ {
/*     */   public String strategyName;
/*     */   public long from;
/*     */   public long to;
/*     */   public double initialDeposit;
/*     */   public double finishDeposit;
/*     */   public List<String[]> parameterValues;
/*     */   public double turnover;
/*     */   public double commission;
/*  46 */   public InstrumentReportData[] instrumentReportData = new InstrumentReportData[Instrument.values().length];
/*     */ 
/* 204 */   public List<TesterEvent> eventLog = new ArrayList();
/*     */   public long[] perfStats;
/*     */   public int[] perfStatCounts;
/* 208 */   public int perfStackIndex = -1;
/* 209 */   public long[] perfStack = new long[30];
/*     */ 
/*     */   public TesterReportData()
/*     */   {
/*     */   }
/*     */ 
/*     */   public TesterReportData(String strategyName, long from, long to, double deposit, List<String[]> parameterValues)
/*     */   {
/*  31 */     this(strategyName, from, to, deposit);
/*  32 */     setParameterValues(parameterValues);
/*     */   }
/*     */ 
/*     */   public TesterReportData(String strategyName, long from, long to, double deposit) {
/*  36 */     this.strategyName = strategyName;
/*  37 */     this.from = from;
/*  38 */     this.to = to;
/*  39 */     this.initialDeposit = deposit;
/*     */   }
/*     */ 
/*     */   public void setParameterValues(List<String[]> parameterValues) {
/*  43 */     this.parameterValues = parameterValues;
/*     */   }
/*     */ 
/*     */   public InstrumentReportData getInstrumentReportData(Instrument instr)
/*     */   {
/*  56 */     InstrumentReportData reportData = this.instrumentReportData[instr.ordinal()];
/*  57 */     return reportData;
/*     */   }
/*     */ 
/*     */   public InstrumentReportData getOrCreateInstrumentReportData(Instrument instr)
/*     */   {
/*  67 */     InstrumentReportData reportData = this.instrumentReportData[instr.ordinal()];
/*  68 */     if (reportData == null) {
/*  69 */       reportData = new InstrumentReportData();
/*  70 */       this.instrumentReportData[instr.ordinal()] = reportData;
/*     */     }
/*  72 */     return reportData;
/*     */   }
/*     */ 
/*     */   public void setInstrumentReportData(Instrument instr, InstrumentReportData data)
/*     */   {
/*  77 */     this.instrumentReportData[instr.ordinal()] = data;
/*     */   }
/*     */ 
/*     */   public void setLastTick(Instrument instr, ITick tick)
/*     */   {
/*  83 */     this.instrumentReportData[instr.ordinal()].lastTick = tick;
/*     */   }
/*     */ 
/*     */   public void addEvent(TesterEvent event)
/*     */   {
/*  89 */     this.eventLog.add(event);
/*     */   }
/*     */ 
/*     */   public List<TesterEvent> getEvents()
/*     */   {
/*  95 */     return this.eventLog;
/*     */   }
/*     */ 
/*     */   public void addCommission(double value)
/*     */   {
/* 101 */     this.commission = StratUtils.roundHalfEven(this.commission + value, 2);
/*     */   }
/*     */ 
/*     */   public void addTurnover(double value)
/*     */   {
/* 107 */     this.turnover = StratUtils.roundHalfEven(this.turnover + value, 2);
/*     */   }
/*     */ 
/*     */   public String getStrategyName()
/*     */   {
/* 113 */     return this.strategyName;
/*     */   }
/*     */ 
/*     */   public long getFrom()
/*     */   {
/* 118 */     return this.from;
/*     */   }
/*     */ 
/*     */   public long getTo()
/*     */   {
/* 123 */     return this.to;
/*     */   }
/*     */ 
/*     */   public List<String[]> getParameterValues()
/*     */   {
/* 128 */     return this.parameterValues;
/*     */   }
/*     */ 
/*     */   public double getCommission()
/*     */   {
/* 133 */     return this.commission;
/*     */   }
/*     */ 
/*     */   public double getTurnover()
/*     */   {
/* 138 */     return this.turnover;
/*     */   }
/*     */ 
/*     */   public double getFinishDeposit()
/*     */   {
/* 143 */     return this.finishDeposit;
/*     */   }
/*     */ 
/*     */   public void setFinishDeposit(double value)
/*     */   {
/* 148 */     this.finishDeposit = value;
/*     */   }
/*     */ 
/*     */   public double getInitialDeposit()
/*     */   {
/* 153 */     return this.initialDeposit;
/*     */   }
/*     */ 
/*     */   public void perfInitiate()
/*     */   {
/* 159 */     this.perfStats = new long[ITesterReport.PerfStats.values().length];
/* 160 */     this.perfStatCounts = new int[ITesterReport.PerfStats.values().length];
/*     */   }
/*     */ 
/*     */   public void perfStart(long value)
/*     */   {
/* 165 */     this.perfStackIndex += 1;
/* 166 */     if (this.perfStackIndex < this.perfStack.length)
/* 167 */       this.perfStack[this.perfStackIndex] = 0L;
/*     */   }
/*     */ 
/*     */   public long perfLast()
/*     */   {
/* 173 */     return this.perfStackIndex < this.perfStack.length ? this.perfStack[this.perfStackIndex] : 0L;
/*     */   }
/*     */ 
/*     */   public void perfAdd(ITesterReport.PerfStats stats, long value)
/*     */   {
/* 179 */     this.perfStats[stats.ordinal()] += value;
/* 180 */     this.perfStatCounts[stats.ordinal()] += 1;
/*     */   }
/*     */ 
/*     */   public void perfStop(long value)
/*     */   {
/* 186 */     this.perfStackIndex -= 1;
/* 187 */     if ((this.perfStackIndex >= 0) && (this.perfStackIndex < this.perfStack.length))
/* 188 */       this.perfStack[this.perfStackIndex] -= value;
/*     */   }
/*     */ 
/*     */   public long[] getPerfStats()
/*     */   {
/* 195 */     return this.perfStats;
/*     */   }
/*     */ 
/*     */   public int[] getPerfStatCounts()
/*     */   {
/* 200 */     return this.perfStatCounts; } 
/*     */   public static class TesterEvent { public EventType type;
/*     */     public long time;
/*     */     public String label;
/*     */     public Instrument instrument;
/*     */     public double amount;
/*     */     public IEngine.OrderCommand orderCommand;
/*     */     public double openPrice;
/*     */     public OpenTrigger openTrigger;
/*     */     public double closePrice;
/*     */     public double closeAmount;
/*     */     public CloseTrigger closeTrigger;
/*     */     public String text;
/*     */     public IOrder[] ordersMerged;
/*     */ 
/* 216 */     public static enum CloseTrigger { CLOSE_BY_STRATEGY, CLOSE_BY_STOP_LOSS, CLOSE_BY_TAKE_PROFIT, CLOSE_BY_MC, CANCEL_BY_NO_MARGIN, 
/* 217 */       CANCEL_BY_STRATEGY, CANCEL_BY_TIMEOUT, CANCEL_BY_MC, CANCEL_BY_VALIDATION, CANCEL_BY_NO_LIQUIDITY, MERGE_BY_MC, MERGE_BY_STRATEGY;
/*     */     }
/*     */ 
/*     */     public static enum OpenTrigger
/*     */     {
/* 215 */       OPEN_BY_STRATEGY, OPEN_BY_MC;
/*     */     }
/*     */ 
/*     */     public static enum EventType
/*     */     {
/* 213 */       ORDER_ENTRY, ORDER_CHANGED, ORDER_FILLED, ORDER_CLOSE, ORDER_CANCEL, MESSAGE, MARGIN_CALL, MARGIN_CUT, 
/* 214 */       COMMISSIONS, OVERNIGHTS, ORDERS_MERGED, EXCEPTION, CANCELED_BY_USER;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData
 * JD-Core Version:    0.6.0
 */