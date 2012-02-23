/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IAccount.AccountState;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Collections;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PlatformAccountImpl
/*     */   implements IAccount
/*     */ {
/*     */   private Currency currency;
/*     */   private double creditLine;
/*     */   private double equity;
/*     */   private double baseEquity;
/*     */   private double balance;
/*     */   private double leverage;
/*     */   private double useOfLeverage;
/*  35 */   private static int marginCutLevel = 200;
/*     */ 
/*  37 */   private static int overTheWeekendLeverage = 30;
/*     */   private static double stopLossLevel;
/*  41 */   private static Set<String> clientIds = new HashSet(0);
/*     */   private boolean global;
/*     */   private String accountId;
/*     */   private IAccount.AccountState accountState;
/*     */ 
/*     */   public PlatformAccountImpl(AccountInfoMessage accountInfoMessage)
/*     */   {
/*  52 */     updateFromMessage(accountInfoMessage);
/*     */   }
/*     */ 
/*     */   public void updateFromMessage(AccountInfoMessage accountInfoMessage) {
/*  56 */     this.currency = accountInfoMessage.getCurrency();
/*  57 */     this.leverage = accountInfoMessage.getLeverage().intValue();
/*  58 */     if (accountInfoMessage.getUsableMargin() != null)
/*  59 */       this.creditLine = accountInfoMessage.getUsableMargin().getValue().multiply(new BigDecimal(this.leverage)).doubleValue();
/*     */     else {
/*  61 */       this.creditLine = 0.0D;
/*     */     }
/*  63 */     if ((accountInfoMessage.getEquity() != null) && (accountInfoMessage.getEquity().getValue().doubleValue() > 0.0D))
/*  64 */       this.equity = accountInfoMessage.getEquity().getValue().doubleValue();
/*     */     else {
/*  66 */       this.equity = 0.0D;
/*     */     }
/*  68 */     if ((accountInfoMessage.getBalance() != null) && (accountInfoMessage.getBalance().getValue().doubleValue() > 0.0D))
/*  69 */       this.balance = accountInfoMessage.getBalance().getValue().doubleValue();
/*     */     else {
/*  71 */       this.balance = 0.0D;
/*     */     }
/*  73 */     if ((accountInfoMessage.getBaseEquity() != null) && (accountInfoMessage.getBaseEquity().getValue().doubleValue() > 0.0D))
/*  74 */       this.baseEquity = accountInfoMessage.getBaseEquity().getValue().doubleValue();
/*     */     else {
/*  76 */       this.baseEquity = 0.0D;
/*     */     }
/*  78 */     if ((accountInfoMessage.getEquity() != null) && (accountInfoMessage.getEquity().getValue().doubleValue() > 0.0D) && (accountInfoMessage.getUsableMargin() != null)) {
/*  79 */       this.useOfLeverage = accountInfoMessage.getEquity().getValue().subtract(accountInfoMessage.getUsableMargin().getValue()).divide(accountInfoMessage.getEquity().getValue(), 2, RoundingMode.HALF_EVEN).doubleValue();
/*  80 */       this.useOfLeverage = StratUtils.round(this.useOfLeverage * 100.0D, 5);
/*     */     } else {
/*  82 */       this.useOfLeverage = 0.0D;
/*     */     }
/*  84 */     if (accountInfoMessage.getAccountState() != null) {
/*  85 */       this.accountState = ((IAccount.AccountState)EnumConverter.convert(accountInfoMessage.getAccountState(), IAccount.AccountState.class));
/*     */     }
/*  87 */     this.global = accountInfoMessage.isGlobal();
/*  88 */     this.accountId = accountInfoMessage.getUserId();
/*     */   }
/*     */ 
/*     */   public double getCreditLine()
/*     */   {
/*  96 */     return this.creditLine;
/*     */   }
/*     */ 
/*     */   public Currency getCurrency()
/*     */   {
/* 104 */     return this.currency;
/*     */   }
/*     */ 
/*     */   public double getEquity()
/*     */   {
/* 113 */     return this.equity;
/*     */   }
/*     */ 
/*     */   public double getBalance()
/*     */   {
/* 121 */     return this.balance;
/*     */   }
/*     */ 
/*     */   public double getLeverage()
/*     */   {
/* 128 */     return this.leverage;
/*     */   }
/*     */ 
/*     */   public double getUseOfLeverage() {
/* 132 */     return this.useOfLeverage;
/*     */   }
/*     */ 
/*     */   public int getMarginCutLevel()
/*     */   {
/* 137 */     return marginCutLevel;
/*     */   }
/*     */ 
/*     */   public int getOverWeekEndLeverage()
/*     */   {
/* 142 */     return overTheWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal() {
/* 146 */     return this.global;
/*     */   }
/*     */ 
/*     */   public String getAccountId()
/*     */   {
/* 151 */     return this.accountId;
/*     */   }
/*     */ 
/*     */   public Set<String> getClientIds()
/*     */   {
/* 156 */     return clientIds;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 162 */     return "Equity = " + getEquity() + " " + getCurrency() + " Leverage=" + getLeverage();
/*     */   }
/*     */ 
/*     */   public IAccount.AccountState getAccountState()
/*     */   {
/* 170 */     return this.accountState;
/*     */   }
/*     */ 
/*     */   public double getStopLossLevel()
/*     */   {
/* 177 */     return stopLossLevel;
/*     */   }
/*     */ 
/*     */   public double getBaseEquity()
/*     */   {
/* 185 */     return this.baseEquity;
/*     */   }
/*     */ 
/*     */   public static void updateStaticValues(AccountInfoMessage accountInfoMessage) {
/* 189 */     String mcLeverageUse = accountInfoMessage.getString("mcLevUse");
/* 190 */     if (mcLeverageUse != null) {
/* 191 */       marginCutLevel = Integer.parseInt(mcLeverageUse);
/*     */     }
/*     */ 
/* 194 */     if (accountInfoMessage.getWeekendLeverage() != null) {
/* 195 */       overTheWeekendLeverage = accountInfoMessage.getWeekendLeverage().intValue();
/*     */     }
/*     */ 
/* 198 */     Set clientIds = accountInfoMessage.getClientIds();
/* 199 */     if (clientIds != null) {
/* 200 */       clientIds = Collections.unmodifiableSet(clientIds);
/*     */     }
/* 202 */     String mcEquityLimit = accountInfoMessage.getString("equityLimit");
/* 203 */     if (!ObjectUtils.isNullOrEmpty(mcEquityLimit))
/* 204 */       stopLossLevel = new BigDecimal(mcEquityLimit).doubleValue();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PlatformAccountImpl
 * JD-Core Version:    0.6.0
 */