/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*     */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import java.io.Serializable;
/*     */ import java.util.Currency;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class StrategyTestBean
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private transient Set<Instrument> instruments;
/*     */   private String period;
/*     */   private String unitName;
/*     */   private int unitCount;
/*     */   private OfferSide offerSide;
/*     */   private ITesterClient.InterpolationMethod interpolationMethod;
/*     */   private ITesterClient.DataLoadingMethod dataLoadingMethod;
/*     */   private int priceDiffInPips;
/*     */   private String timeUnitType;
/*     */   private int timeUnitCount;
/*     */   private String range;
/*     */   private boolean rangeSelected;
/*     */   private long fromDate;
/*     */   private long toDate;
/*     */   private boolean eventLogEnabled;
/*     */   private boolean processingStatsEnabled;
/*     */   private boolean optimization;
/*     */   private boolean visualMode;
/*     */   private boolean showBalance;
/*     */   private boolean showEquity;
/*     */   private boolean showProfitLoss;
/*     */   private String chartTemplate;
/*     */   private int testSpeed;
/*     */   private transient JForexPeriod chartPeriod;
/*     */   private String strategyBinaryPath;
/*     */   private double initialDeposit;
/*     */   private String accountCurrency;
/*     */   private int maxLeverage;
/*     */   private int mcLeverage;
/*     */   private Commissions commissions;
/*     */   private Overnights overnights;
/*     */   private double mcEquity;
/*     */   private boolean messagesDisabled;
/*     */   private boolean saveMessagesToFile;
/*     */   private String messagesFilePath;
/*     */   private boolean reportsDisabled;
/*     */   private boolean saveReportFile;
/*     */   private String reportFilePath;
/*     */   private double balanseDropDown;
/*  78 */   private int panelChartId = -1;
/*     */   private int executionSpeed;
/*  82 */   private boolean cutFlatTicks = true;
/*     */ 
/*     */   public int getPanelChartId()
/*     */   {
/*  91 */     return this.panelChartId;
/*     */   }
/*     */ 
/*     */   public void setPanelChartId(int panelChartId) {
/*  95 */     this.panelChartId = panelChartId;
/*     */   }
/*     */ 
/*     */   public String getStrategyBinaryPath() {
/*  99 */     return this.strategyBinaryPath;
/*     */   }
/*     */ 
/*     */   public void setStrategyBinaryPath(String strategyBinaryPath) {
/* 103 */     this.strategyBinaryPath = strategyBinaryPath;
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getInstruments() {
/* 107 */     return this.instruments;
/*     */   }
/*     */ 
/*     */   public void setInstruments(Set<Instrument> instruments) {
/* 111 */     this.instruments = instruments;
/*     */   }
/*     */ 
/*     */   public Period getPeriod() {
/* 115 */     if (this.period != null)
/*     */     {
/* 117 */       return Period.valueOf(this.period);
/*     */     }
/* 119 */     if (this.unitName != null)
/*     */     {
/* 121 */       Unit unit = Unit.valueOf(this.unitName);
/* 122 */       return Period.createCustomPeriod(unit, this.unitCount);
/*     */     }
/*     */ 
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/* 130 */     if (period == null) {
/* 131 */       this.period = null;
/* 132 */       this.unitName = null;
/* 133 */       this.unitCount = -1;
/*     */     }
/* 135 */     else if (period.ordinal() >= 0)
/*     */     {
/* 137 */       this.period = period.name();
/* 138 */       this.unitName = null;
/* 139 */       this.unitCount = -1;
/*     */     }
/*     */     else
/*     */     {
/* 143 */       this.period = null;
/* 144 */       this.unitName = period.getUnit().name();
/* 145 */       this.unitCount = period.getNumOfUnits();
/*     */     }
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/* 150 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide) {
/* 154 */     this.offerSide = offerSide;
/*     */   }
/*     */ 
/*     */   public ITesterClient.InterpolationMethod getInterpolationMethod() {
/* 158 */     return this.interpolationMethod;
/*     */   }
/*     */ 
/*     */   public void setInterpolationMethod(ITesterClient.InterpolationMethod interpolationMethod) {
/* 162 */     this.interpolationMethod = interpolationMethod;
/*     */   }
/*     */ 
/*     */   public String getRange() {
/* 166 */     return this.range;
/*     */   }
/*     */ 
/*     */   public void setRange(String range) {
/* 170 */     this.range = range;
/*     */   }
/*     */ 
/*     */   public boolean isRangeSelected() {
/* 174 */     return this.rangeSelected;
/*     */   }
/*     */ 
/*     */   public void setRangeSelected(boolean rangeSelected) {
/* 178 */     this.rangeSelected = rangeSelected;
/*     */   }
/*     */ 
/*     */   public long getFromDate() {
/* 182 */     return this.fromDate;
/*     */   }
/*     */ 
/*     */   public void setFromDate(long fromDate) {
/* 186 */     this.fromDate = fromDate;
/*     */   }
/*     */ 
/*     */   public long getToDate() {
/* 190 */     return this.toDate;
/*     */   }
/*     */ 
/*     */   public void setToDate(long toDate) {
/* 194 */     this.toDate = toDate;
/*     */   }
/*     */ 
/*     */   public boolean isSaveReportFile() {
/* 198 */     return this.saveReportFile;
/*     */   }
/*     */ 
/*     */   public void setSaveReportFile(boolean saveReport) {
/* 202 */     this.saveReportFile = saveReport;
/*     */   }
/*     */ 
/*     */   public boolean isEventLogEnabled() {
/* 206 */     return this.eventLogEnabled;
/*     */   }
/*     */ 
/*     */   public void setEventLogEnabled(boolean eventLogEnabled) {
/* 210 */     this.eventLogEnabled = eventLogEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isProcessingStatsEnabled() {
/* 214 */     return this.processingStatsEnabled;
/*     */   }
/*     */ 
/*     */   public void setProcessingStatsEnabled(boolean processingStatsEnabled) {
/* 218 */     this.processingStatsEnabled = processingStatsEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isVisualMode() {
/* 222 */     return this.visualMode;
/*     */   }
/*     */ 
/*     */   public void setVisualMode(boolean visualMode) {
/* 226 */     this.visualMode = visualMode;
/*     */   }
/*     */ 
/*     */   public boolean isShowBalance() {
/* 230 */     return this.showBalance;
/*     */   }
/*     */ 
/*     */   public void setShowBalance(boolean showBalance) {
/* 234 */     this.showBalance = showBalance;
/*     */   }
/*     */ 
/*     */   public boolean isShowEquity() {
/* 238 */     return this.showEquity;
/*     */   }
/*     */ 
/*     */   public void setShowEquity(boolean showEquity) {
/* 242 */     this.showEquity = showEquity;
/*     */   }
/*     */ 
/*     */   public boolean isShowProfitLoss() {
/* 246 */     return this.showProfitLoss;
/*     */   }
/*     */ 
/*     */   public void setShowProfitLoss(boolean showProfitLoss) {
/* 250 */     this.showProfitLoss = showProfitLoss;
/*     */   }
/*     */ 
/*     */   public String getChartTemplate() {
/* 254 */     return this.chartTemplate;
/*     */   }
/*     */ 
/*     */   public void setChartTemplate(String chartTemplate) {
/* 258 */     this.chartTemplate = chartTemplate;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getChartPeriod() {
/* 262 */     return this.chartPeriod;
/*     */   }
/*     */ 
/*     */   public void setChartPeriod(JForexPeriod chartPeriod) {
/* 266 */     this.chartPeriod = chartPeriod;
/*     */   }
/*     */ 
/*     */   public double getInitialDeposit() {
/* 270 */     return this.initialDeposit;
/*     */   }
/*     */ 
/*     */   public void setInitialDeposit(double initialDeposit) {
/* 274 */     this.initialDeposit = initialDeposit;
/*     */   }
/*     */ 
/*     */   public String getAccountCurrency() {
/* 278 */     return this.accountCurrency;
/*     */   }
/*     */ 
/*     */   public void setAccountCurrency(Currency accountCurrency) {
/* 282 */     this.accountCurrency = accountCurrency.getCurrencyCode();
/*     */   }
/*     */ 
/*     */   public int getMaxLeverage() {
/* 286 */     return this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public void setMaxLeverage(int maxLeverage) {
/* 290 */     this.maxLeverage = maxLeverage;
/*     */   }
/*     */ 
/*     */   public int getMcLeverage() {
/* 294 */     return this.mcLeverage;
/*     */   }
/*     */ 
/*     */   public void setMcLeverage(int mcLeverage) {
/* 298 */     this.mcLeverage = mcLeverage;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 302 */     return this.commissions;
/*     */   }
/*     */ 
/*     */   public void setCommissions(Commissions commissions) {
/* 306 */     this.commissions = commissions;
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 310 */     return this.overnights;
/*     */   }
/*     */ 
/*     */   public void setOvernights(Overnights overnights) {
/* 314 */     this.overnights = overnights;
/*     */   }
/*     */ 
/*     */   public double getMcEquity() {
/* 318 */     return this.mcEquity;
/*     */   }
/*     */ 
/*     */   public void setMcEquity(double mcEquity) {
/* 322 */     this.mcEquity = mcEquity;
/*     */   }
/*     */ 
/*     */   public int getExecutionSpeed() {
/* 326 */     return this.executionSpeed;
/*     */   }
/*     */ 
/*     */   public void setExecutionSpeed(int executionSpeed) {
/* 330 */     this.executionSpeed = executionSpeed;
/*     */   }
/*     */ 
/*     */   public boolean isCutFlatTicks() {
/* 334 */     return this.cutFlatTicks;
/*     */   }
/*     */ 
/*     */   public void setCutFlatTicks(boolean cutFlatTicks) {
/* 338 */     this.cutFlatTicks = cutFlatTicks;
/*     */   }
/*     */ 
/*     */   public boolean isSaveMessagesToFile() {
/* 342 */     return this.saveMessagesToFile;
/*     */   }
/*     */ 
/*     */   public void setSaveMessagesToFile(boolean saveMessagesToFile) {
/* 346 */     this.saveMessagesToFile = saveMessagesToFile;
/*     */   }
/*     */ 
/*     */   public String getMessagesFilePath() {
/* 350 */     return this.messagesFilePath;
/*     */   }
/*     */ 
/*     */   public void setMessagesFilePath(String messagesFilePath) {
/* 354 */     this.messagesFilePath = messagesFilePath;
/*     */   }
/*     */ 
/*     */   public String getReportFilePath() {
/* 358 */     return this.reportFilePath;
/*     */   }
/*     */ 
/*     */   public void setReportFilePath(String reportFilePath) {
/* 362 */     this.reportFilePath = reportFilePath;
/*     */   }
/*     */ 
/*     */   public boolean isOptimization() {
/* 366 */     return this.optimization;
/*     */   }
/*     */ 
/*     */   public void setOptimization(boolean optimization) {
/* 370 */     this.optimization = optimization;
/*     */   }
/*     */ 
/*     */   public boolean isMessagesDisabled() {
/* 374 */     return this.messagesDisabled;
/*     */   }
/*     */ 
/*     */   public void setMessagesDisabled(boolean disabled) {
/* 378 */     this.messagesDisabled = disabled;
/*     */   }
/*     */ 
/*     */   public boolean isReportsDisabled() {
/* 382 */     return this.reportsDisabled;
/*     */   }
/*     */ 
/*     */   public void setReportsDisabled(boolean reportsDisabled) {
/* 386 */     this.reportsDisabled = reportsDisabled;
/*     */   }
/*     */ 
/*     */   public double getBalanseDropDown() {
/* 390 */     return this.balanseDropDown;
/*     */   }
/*     */ 
/*     */   public void setBalanseDropDown(double balanseDropDown) {
/* 394 */     this.balanseDropDown = balanseDropDown;
/*     */   }
/*     */ 
/*     */   public void setDataLoadingMethod(ITesterClient.DataLoadingMethod dataLoadingMethod) {
/* 398 */     this.dataLoadingMethod = dataLoadingMethod;
/*     */   }
/*     */ 
/*     */   public ITesterClient.DataLoadingMethod getDataLoadingMethod() {
/* 402 */     return this.dataLoadingMethod;
/*     */   }
/*     */ 
/*     */   public int getPriceDiffInPips() {
/* 406 */     return this.priceDiffInPips;
/*     */   }
/*     */ 
/*     */   public void setPriceDiffInPips(int priceDiffInPips) {
/* 410 */     this.priceDiffInPips = priceDiffInPips;
/*     */   }
/*     */ 
/*     */   public String getTimeUnitType() {
/* 414 */     return this.timeUnitType;
/*     */   }
/*     */ 
/*     */   public void setTimeUnitType(String timeUnitType) {
/* 418 */     this.timeUnitType = timeUnitType;
/*     */   }
/*     */ 
/*     */   public int getTimeUnitCount() {
/* 422 */     return this.timeUnitCount;
/*     */   }
/*     */ 
/*     */   public void setTimeUnitCount(int timeUnitCount) {
/* 426 */     this.timeUnitCount = timeUnitCount;
/*     */   }
/*     */ 
/*     */   public int getTestSpeed() {
/* 430 */     return this.testSpeed;
/*     */   }
/*     */ 
/*     */   public void setTestSpeed(int testSpeed) {
/* 434 */     this.testSpeed = testSpeed;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.StrategyTestBean
 * JD-Core Version:    0.6.0
 */