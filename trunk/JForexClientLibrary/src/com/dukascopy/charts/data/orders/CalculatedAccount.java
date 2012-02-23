/*     */ package com.dukascopy.charts.data.orders;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IAccount.AccountState;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CalculatedAccount
/*     */   implements IAccount
/*     */ {
/*     */   private Currency currency;
/*     */   private double leverage;
/*     */   private double baseEquity;
/*     */   private double balance;
/*  17 */   private int marginCutLevel = 200;
/*  18 */   private int overTheWeekendLeverage = 30;
/*     */   private double accountInfoCreditLine;
/*     */   private double accountInfoEquity;
/*     */   private double accountInfoUseOfLeverage;
/*     */   private double calculationTime;
/*     */   private double calculatedEquity;
/*     */   private double calculatedUseOfLeverage;
/*     */   private double calculatedCreditLine;
/*     */   private boolean global;
/*     */   private String accountId;
/*     */   private double stopLossLevel;
/*     */   private IAccount.AccountState accountState;
/*     */ 
/*     */   public void setCurrency(Currency currency)
/*     */   {
/*  36 */     this.currency = currency;
/*     */   }
/*     */ 
/*     */   public void setLeverage(double leverage) {
/*  40 */     this.leverage = leverage;
/*     */   }
/*     */ 
/*     */   public void setBaseEquity(double baseEquity) {
/*  44 */     this.baseEquity = baseEquity;
/*     */   }
/*     */ 
/*     */   public void setBalance(double balance) {
/*  48 */     this.balance = balance;
/*     */   }
/*     */ 
/*     */   public void setMarginCutLevel(int marginCutLevel) {
/*  52 */     this.marginCutLevel = marginCutLevel;
/*     */   }
/*     */ 
/*     */   public void setOverTheWeekendLeverage(int overTheWeekendLeverage) {
/*  56 */     this.overTheWeekendLeverage = overTheWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public void setAccountInfoCreditLine(double accountInfoCreditLine) {
/*  60 */     this.accountInfoCreditLine = accountInfoCreditLine;
/*     */   }
/*     */ 
/*     */   public void setAccountInfoEquity(double accountInfoEquity) {
/*  64 */     this.accountInfoEquity = accountInfoEquity;
/*     */   }
/*     */ 
/*     */   public void setAccountInfoUseOfLeverage(double accountInfoUseOfLeverage) {
/*  68 */     this.accountInfoUseOfLeverage = accountInfoUseOfLeverage;
/*     */   }
/*     */ 
/*     */   public void setAccountState(IAccount.AccountState accountState)
/*     */   {
/*  75 */     this.accountState = accountState;
/*     */   }
/*     */ 
/*     */   public double getCalculationTime() {
/*  79 */     return this.calculationTime;
/*     */   }
/*     */ 
/*     */   public void setCalculationTime(double calculationTime) {
/*  83 */     this.calculationTime = calculationTime;
/*     */   }
/*     */ 
/*     */   public double getCalculatedEquity() {
/*  87 */     return this.calculatedEquity;
/*     */   }
/*     */ 
/*     */   public void setCalculatedEquity(double calculatedEquity) {
/*  91 */     this.calculatedEquity = calculatedEquity;
/*     */   }
/*     */ 
/*     */   public double getCalculatedUseOfLeverage() {
/*  95 */     return this.calculatedUseOfLeverage;
/*     */   }
/*     */ 
/*     */   public void setCalculatedUseOfLeverage(double calculatedUseOfLeverage) {
/*  99 */     this.calculatedUseOfLeverage = calculatedUseOfLeverage;
/*     */   }
/*     */ 
/*     */   public double getCalculatedCreditLine() {
/* 103 */     return this.calculatedCreditLine;
/*     */   }
/*     */ 
/*     */   public void setCalculatedCreditLine(double calculatedCreditLine) {
/* 107 */     this.calculatedCreditLine = calculatedCreditLine;
/*     */   }
/*     */ 
/*     */   public double getBaseEquity() {
/* 111 */     return this.baseEquity;
/*     */   }
/*     */ 
/*     */   public double getAccountInfoCreditLine() {
/* 115 */     return this.accountInfoCreditLine;
/*     */   }
/*     */ 
/*     */   public double getAccountInfoEquity() {
/* 119 */     return this.accountInfoEquity;
/*     */   }
/*     */ 
/*     */   public double getAccountInfoUseOfLeverage() {
/* 123 */     return this.accountInfoUseOfLeverage;
/*     */   }
/*     */ 
/*     */   public void setGlobal(boolean global) {
/* 127 */     this.global = global;
/*     */   }
/*     */ 
/*     */   public void setAccountId(String accountId) {
/* 131 */     this.accountId = accountId;
/*     */   }
/*     */ 
/*     */   public void setStopLossLevel(double stopLossLevel)
/*     */   {
/* 138 */     this.stopLossLevel = stopLossLevel;
/*     */   }
/*     */ 
/*     */   public Currency getCurrency()
/*     */   {
/* 146 */     return this.currency;
/*     */   }
/*     */ 
/*     */   public double getEquity()
/*     */   {
/* 151 */     return this.calculatedEquity;
/*     */   }
/*     */ 
/*     */   public double getBalance()
/*     */   {
/* 156 */     return this.balance;
/*     */   }
/*     */ 
/*     */   public double getLeverage()
/*     */   {
/* 161 */     return this.leverage;
/*     */   }
/*     */ 
/*     */   public double getUseOfLeverage()
/*     */   {
/* 166 */     return this.calculatedUseOfLeverage;
/*     */   }
/*     */ 
/*     */   public double getCreditLine()
/*     */   {
/* 171 */     return this.calculatedCreditLine;
/*     */   }
/*     */ 
/*     */   public int getMarginCutLevel()
/*     */   {
/* 176 */     return this.marginCutLevel;
/*     */   }
/*     */ 
/*     */   public int getOverWeekEndLeverage()
/*     */   {
/* 181 */     return this.overTheWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal()
/*     */   {
/* 186 */     return this.global;
/*     */   }
/*     */ 
/*     */   public String getAccountId()
/*     */   {
/* 191 */     return this.accountId;
/*     */   }
/*     */ 
/*     */   public Set<String> getClientIds()
/*     */   {
/* 196 */     return new HashSet();
/*     */   }
/*     */ 
/*     */   public IAccount.AccountState getAccountState()
/*     */   {
/* 204 */     return this.accountState;
/*     */   }
/*     */ 
/*     */   public double getStopLossLevel()
/*     */   {
/* 211 */     return this.stopLossLevel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.orders.CalculatedAccount
 * JD-Core Version:    0.6.0
 */