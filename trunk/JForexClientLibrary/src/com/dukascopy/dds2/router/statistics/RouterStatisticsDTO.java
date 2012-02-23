/*     */ package com.dukascopy.dds2.router.statistics;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class RouterStatisticsDTO
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 0L;
/*     */   private String routerName;
/*  14 */   private Map<String, ProxyStatisticsEntry> proxyStatistics = new HashMap();
/*  15 */   private long proxyOrdersNotAcceptedCount = 0L;
/*     */ 
/*  18 */   private Map<String, MinMaxAverageLong> executorOrderExecutionTimes = new HashMap();
/*     */   private MinMaxAverageLong orderExecutionTime;
/*  24 */   private Map<String, Map<String, MinMaxAverageBigDecimal>> executorSlippages = new HashMap();
/*     */ 
/*  27 */   private Map<String, MinMaxAverageLong> instrumentFeedAggregationTimes = new HashMap();
/*     */ 
/*  30 */   private Map<String, Map<String, Long>> executorInstrumentTicks = new HashMap();
/*     */ 
/*  33 */   private Map<String, SmartFilterStatisticsEntry> smartFilterStatistics = new HashMap();
/*     */ 
/*  36 */   private Map<String, Map<String, Long>> executorSpreadFilterFilteredCounts = new HashMap();
/*     */ 
/*     */   public String getRouterName() {
/*  39 */     return this.routerName;
/*     */   }
/*     */ 
/*     */   public void setRouterName(String routerName) {
/*  43 */     this.routerName = routerName;
/*     */   }
/*     */ 
/*     */   public Map<String, ProxyStatisticsEntry> getProxyStatistics() {
/*  47 */     return this.proxyStatistics;
/*     */   }
/*     */ 
/*     */   public void setProxyStatistics(Map<String, ProxyStatisticsEntry> proxyStatistics) {
/*  51 */     this.proxyStatistics = proxyStatistics;
/*     */   }
/*     */ 
/*     */   public long getProxyOrdersNotAcceptedCount() {
/*  55 */     return this.proxyOrdersNotAcceptedCount;
/*     */   }
/*     */ 
/*     */   public void setProxyOrdersNotAcceptedCount(long proxyOrdersNotAcceptedCount) {
/*  59 */     this.proxyOrdersNotAcceptedCount = proxyOrdersNotAcceptedCount;
/*     */   }
/*     */ 
/*     */   public Map<String, MinMaxAverageLong> getExecutorOrderExecutionTimes() {
/*  63 */     return this.executorOrderExecutionTimes;
/*     */   }
/*     */ 
/*     */   public void setExecutorOrderExecutionTimes(Map<String, MinMaxAverageLong> executorOrderExecutionTimes)
/*     */   {
/*  68 */     this.executorOrderExecutionTimes = executorOrderExecutionTimes;
/*     */   }
/*     */ 
/*     */   public MinMaxAverageLong getOrderExecutionTime() {
/*  72 */     return this.orderExecutionTime;
/*     */   }
/*     */ 
/*     */   public void setOrderExecutionTime(MinMaxAverageLong orderExecutionTime) {
/*  76 */     this.orderExecutionTime = orderExecutionTime;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, MinMaxAverageBigDecimal>> getExecutorSlippages() {
/*  80 */     return this.executorSlippages;
/*     */   }
/*     */ 
/*     */   public void setExecutorSlippages(Map<String, Map<String, MinMaxAverageBigDecimal>> executorSlippages)
/*     */   {
/*  85 */     this.executorSlippages = executorSlippages;
/*     */   }
/*     */ 
/*     */   public Map<String, MinMaxAverageLong> getInstrumentFeedAggregationTimes() {
/*  89 */     return this.instrumentFeedAggregationTimes;
/*     */   }
/*     */ 
/*     */   public void setInstrumentFeedAggregationTimes(Map<String, MinMaxAverageLong> instrumentFeedAggregationTimes)
/*     */   {
/*  94 */     this.instrumentFeedAggregationTimes = instrumentFeedAggregationTimes;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, Long>> getExecutorInstrumentTicks() {
/*  98 */     return this.executorInstrumentTicks;
/*     */   }
/*     */ 
/*     */   public void setExecutorInstrumentTicks(Map<String, Map<String, Long>> executorInstrumentTicks)
/*     */   {
/* 103 */     this.executorInstrumentTicks = executorInstrumentTicks;
/*     */   }
/*     */ 
/*     */   public Map<String, SmartFilterStatisticsEntry> getSmartFilterStatistics() {
/* 107 */     return this.smartFilterStatistics;
/*     */   }
/*     */ 
/*     */   public void setSmartFilterStatistics(Map<String, SmartFilterStatisticsEntry> smartFilterStatistics) {
/* 111 */     this.smartFilterStatistics = smartFilterStatistics;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<String, Long>> getExecutorSpreadFilterFilteredCounts() {
/* 115 */     return this.executorSpreadFilterFilteredCounts;
/*     */   }
/*     */ 
/*     */   public void setExecutorSpreadFilterFilteredCounts(Map<String, Map<String, Long>> executorSpreadFilterFilteredCounts)
/*     */   {
/* 120 */     this.executorSpreadFilterFilteredCounts = executorSpreadFilterFilteredCounts;
/*     */   }
/*     */ 
/*     */   public synchronized void resetStatistics()
/*     */   {
/* 125 */     this.proxyStatistics.clear();
/* 126 */     this.proxyOrdersNotAcceptedCount = 0L;
/*     */ 
/* 129 */     this.executorOrderExecutionTimes.clear();
/*     */ 
/* 132 */     this.orderExecutionTime = null;
/*     */ 
/* 135 */     this.executorSlippages.clear();
/*     */ 
/* 138 */     this.instrumentFeedAggregationTimes.clear();
/*     */ 
/* 141 */     this.executorInstrumentTicks.clear();
/*     */ 
/* 144 */     this.smartFilterStatistics.clear();
/*     */ 
/* 147 */     this.executorSpreadFilterFilteredCounts.clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.RouterStatisticsDTO
 * JD-Core Version:    0.6.0
 */