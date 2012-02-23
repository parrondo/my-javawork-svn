/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IAccount.AccountState;
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class TesterAccount
/*     */   implements IAccount
/*     */ {
/*     */   private Currency currency;
/*     */   private double deposit;
/*     */   private double maxLeverage;
/*     */   private int marginCatLevel;
/*     */   private double marginWeekendCallLeverage;
/*     */   private double mcEquityLimit;
/*     */   private Commissions commission;
/*     */   private Overnights overnights;
/*     */   private String accountId;
/*     */   private double equity;
/*     */   private double realizedEquity;
/*     */   private double realizedEquityWithCommissions;
/*     */   private double useOfLeverage;
/*     */   private double creditLine;
/*     */   private double equity5SecDelayed;
/*     */   private double useOfLeverage5SecDelayed;
/*     */   private double creditLine5SecDelayed;
/*  39 */   private double profitLossOfClosedPositions = 0.0D;
/*  40 */   private double profLossOfOpenPositions = 0.0D;
/*     */   private boolean isGlobal;
/*  43 */   private IAccount.AccountState accountState = IAccount.AccountState.OK;
/*  44 */   private String userTypeDescription = "Other";
/*     */ 
/*     */   public TesterAccount(Currency currency, double deposit, double maxLeverage, int marginCatLevel, double mcEquityLimit, Commissions commission, Overnights overnights, String accountId)
/*     */   {
/*  48 */     this(currency, deposit, maxLeverage, marginCatLevel, 0.0D, mcEquityLimit, commission, overnights, accountId);
/*     */   }
/*     */ 
/*     */   public TesterAccount(Currency currency, double deposit, double maxLeverage, int marginCatLevel, double marginWeekendCallLeverage, double mcEquityLimit, Commissions commission, Overnights overnights, String accountId)
/*     */   {
/*  53 */     setCurrency(currency);
/*  54 */     setDeposit(deposit);
/*  55 */     setEquity(deposit);
/*  56 */     setRealizedEquity(deposit);
/*  57 */     setRealizedEquityWithCommissions(deposit);
/*  58 */     setMaxLeverage(maxLeverage);
/*  59 */     setMarginCatLevel(marginCatLevel);
/*  60 */     setMarginWeekendCallLeverage(marginWeekendCallLeverage);
/*  61 */     setMcEquityLimit(mcEquityLimit);
/*  62 */     setCommissions(commission);
/*  63 */     setOvernights(overnights);
/*  64 */     setAccountId(accountId);
/*     */   }
/*     */ 
/*     */   protected TesterAccount(TesterAccount parent)
/*     */   {
/*  72 */     setCurrency(parent.getCurrency());
/*  73 */     setDeposit(parent.getDeposit());
/*  74 */     setEquity(parent.getEquityActual());
/*  75 */     setRealizedEquity(parent.getRealizedEquity());
/*  76 */     setRealizedEquityWithCommissions(parent.getRealizedEquityWithCommissions());
/*  77 */     setMaxLeverage(parent.getMaxLeverage());
/*  78 */     setMarginCatLevel(parent.getMarginCutLevel());
/*  79 */     setMarginWeekendCallLeverage(parent.getMarginWeekendCallLeverage());
/*  80 */     setMcEquityLimit(parent.getMCEquityLimit());
/*  81 */     setCommissions(parent.getCommissions());
/*  82 */     setOvernights(parent.getOvernights());
/*  83 */     setUseOfLeverage(parent.getUseOfLeverage());
/*  84 */     setCreditLine(parent.getCreditLine());
/*  85 */     setAccountId(parent.getAccountId());
/*  86 */     setProfitLossOfClosedPositions(parent.getProfitLossOfClosedPositions());
/*  87 */     setProfLossOfOpenPositions(parent.getProfLossOfOpenPositions());
/*  88 */     setGlobal(parent.isGlobal());
/*  89 */     setAccountState(parent.getAccountState());
/*  90 */     setUserTypeDescription(parent.getUserTypeDescription());
/*     */   }
/*     */ 
/*     */   public double getCreditLine() {
/*  94 */     return this.creditLine5SecDelayed;
/*     */   }
/*     */ 
/*     */   public Currency getCurrency() {
/*  98 */     return this.currency;
/*     */   }
/*     */ 
/*     */   public double getEquity() {
/* 102 */     return this.equity5SecDelayed;
/*     */   }
/*     */ 
/*     */   public double getBalance() {
/* 106 */     return getDeposit();
/*     */   }
/*     */ 
/*     */   public double getLeverage() {
/* 110 */     return this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public double getUseOfLeverage() {
/* 114 */     return this.useOfLeverage5SecDelayed;
/*     */   }
/*     */ 
/*     */   public void update5SecDelayedValues() {
/* 118 */     this.equity5SecDelayed = this.equity;
/* 119 */     this.useOfLeverage5SecDelayed = this.useOfLeverage;
/* 120 */     this.creditLine5SecDelayed = this.creditLine;
/*     */   }
/*     */ 
/*     */   public double getMCEquityLimit() {
/* 124 */     return this.mcEquityLimit;
/*     */   }
/*     */ 
/*     */   public void setMcEquityLimit(double mcEquityLimit) {
/* 128 */     this.mcEquityLimit = mcEquityLimit;
/*     */   }
/*     */ 
/*     */   public double getDeposit() {
/* 132 */     return this.deposit;
/*     */   }
/*     */ 
/*     */   public void setDeposit(double deposit) {
/* 136 */     this.deposit = deposit;
/*     */   }
/*     */ 
/*     */   public double getMaxLeverage() {
/* 140 */     return this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public void setMaxLeverage(double maxLeverage) {
/* 144 */     this.maxLeverage = maxLeverage;
/*     */   }
/*     */ 
/*     */   public int getMarginCutLevel() {
/* 148 */     return this.marginCatLevel;
/*     */   }
/*     */ 
/*     */   public void setMarginCatLevel(int marginCatLevel) {
/* 152 */     this.marginCatLevel = marginCatLevel;
/*     */   }
/*     */ 
/*     */   public double getMarginWeekendCallLeverage() {
/* 156 */     return this.marginWeekendCallLeverage;
/*     */   }
/*     */ 
/*     */   public void setMarginWeekendCallLeverage(double marginWeekendCallLeverage) {
/* 160 */     this.marginWeekendCallLeverage = marginWeekendCallLeverage;
/*     */   }
/*     */ 
/*     */   public double getUseOfLeverageActual() {
/* 164 */     return this.useOfLeverage;
/*     */   }
/*     */ 
/*     */   public void setUseOfLeverage(double useOfLeverage) {
/* 168 */     this.useOfLeverage = useOfLeverage;
/*     */   }
/*     */ 
/*     */   public void setCurrency(Currency currency) {
/* 172 */     this.currency = currency;
/*     */   }
/*     */ 
/*     */   public void setEquity(double equity) {
/* 176 */     this.equity = equity;
/*     */   }
/*     */ 
/*     */   public double getEquityActual() {
/* 180 */     return this.equity;
/*     */   }
/*     */ 
/*     */   public double getRealizedEquity() {
/* 184 */     return this.realizedEquity;
/*     */   }
/*     */ 
/*     */   public void setRealizedEquity(double realizedEquity) {
/* 188 */     this.realizedEquity = realizedEquity;
/*     */   }
/*     */ 
/*     */   public double getRealizedEquityWithCommissions() {
/* 192 */     return this.realizedEquityWithCommissions;
/*     */   }
/*     */ 
/*     */   public void setRealizedEquityWithCommissions(double realizedEquityWithCommissions) {
/* 196 */     this.realizedEquityWithCommissions = realizedEquityWithCommissions;
/*     */   }
/*     */ 
/*     */   public void setCreditLine(double creditLine) {
/* 200 */     this.creditLine = creditLine;
/*     */   }
/*     */ 
/*     */   public double getCreditLineActual() {
/* 204 */     return this.creditLine;
/*     */   }
/*     */ 
/*     */   public double getAvailableMargin() {
/* 208 */     return this.creditLine / this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 212 */     return this.commission;
/*     */   }
/*     */ 
/*     */   public void setCommissions(Commissions commission) {
/* 216 */     this.commission = commission;
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 220 */     return this.overnights;
/*     */   }
/*     */ 
/*     */   public void setOvernights(Overnights overnights) {
/* 224 */     this.overnights = overnights;
/*     */   }
/*     */ 
/*     */   public int getOverWeekEndLeverage()
/*     */   {
/* 229 */     return 80;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal()
/*     */   {
/* 234 */     return this.isGlobal;
/*     */   }
/*     */ 
/*     */   public void setGlobal(boolean isGlobal) {
/* 238 */     this.isGlobal = isGlobal;
/*     */   }
/*     */ 
/*     */   public String getAccountId()
/*     */   {
/* 243 */     return this.accountId;
/*     */   }
/*     */ 
/*     */   public void setAccountId(String accountId) {
/* 247 */     this.accountId = accountId;
/*     */   }
/*     */ 
/*     */   public Set<String> getClientIds()
/*     */   {
/* 252 */     return new HashSet(0);
/*     */   }
/*     */ 
/*     */   public IAccount.AccountState getAccountState()
/*     */   {
/* 260 */     return this.accountState;
/*     */   }
/*     */ 
/*     */   public double getStopLossLevel()
/*     */   {
/* 268 */     return getMCEquityLimit();
/*     */   }
/*     */ 
/*     */   public void setAccountState(IAccount.AccountState accountState)
/*     */   {
/* 275 */     this.accountState = accountState;
/*     */   }
/*     */ 
/*     */   public double getProfitLossOfClosedPositions() {
/* 279 */     return this.profitLossOfClosedPositions;
/*     */   }
/*     */ 
/*     */   public void setProfitLossOfClosedPositions(double profitLossOfClosedPositions) {
/* 283 */     this.profitLossOfClosedPositions = profitLossOfClosedPositions;
/*     */   }
/*     */ 
/*     */   public double getProfLossOfOpenPositions() {
/* 287 */     return this.profLossOfOpenPositions;
/*     */   }
/*     */ 
/*     */   public void setProfLossOfOpenPositions(double profLossOfOpenPositions) {
/* 291 */     this.profLossOfOpenPositions = profLossOfOpenPositions;
/*     */   }
/*     */ 
/*     */   public double getTotalProfitLoss() {
/* 295 */     return getProfitLossOfClosedPositions() + getProfLossOfOpenPositions();
/*     */   }
/*     */ 
/*     */   public String getUserTypeDescription() {
/* 299 */     return this.userTypeDescription;
/*     */   }
/*     */ 
/*     */   public void setUserTypeDescription(String userTypeDescription) {
/* 303 */     this.userTypeDescription = userTypeDescription;
/*     */   }
/*     */ 
/*     */   public double getBaseEquity()
/*     */   {
/* 311 */     return getRealizedEquity();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount
 * JD-Core Version:    0.6.0
 */