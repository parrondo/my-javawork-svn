/*     */ package com.dukascopy.transport.common.msg.pojo;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AccountState
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 200706271436L;
/*     */   private String accountId;
/*  33 */   private Map<String, BigDecimal> instrumentsExposure = new HashMap();
/*     */   private Money usableMargin;
/*     */   private Integer leverage;
/*     */ 
/*     */   public AccountState(String accountId, Integer leverage, Map<String, BigDecimal> instrumentsExposure, Money usableMargin)
/*     */   {
/*  41 */     this.accountId = accountId;
/*  42 */     this.instrumentsExposure = instrumentsExposure;
/*  43 */     this.usableMargin = usableMargin;
/*  44 */     this.leverage = leverage;
/*     */   }
/*     */ 
/*     */   public AccountState(String accountId, Money usableMargin)
/*     */   {
/*  49 */     this.accountId = accountId;
/*  50 */     this.usableMargin = usableMargin;
/*     */   }
/*     */ 
/*     */   public Integer getLeverage()
/*     */   {
/*  59 */     return this.leverage;
/*     */   }
/*     */ 
/*     */   public void setLeverage(Integer leverage)
/*     */   {
/*  66 */     this.leverage = leverage;
/*     */   }
/*     */ 
/*     */   public String getAccountId()
/*     */   {
/*  73 */     return this.accountId;
/*     */   }
/*     */ 
/*     */   public void setAccountId(String accountId)
/*     */   {
/*  80 */     this.accountId = accountId;
/*     */   }
/*     */ 
/*     */   public Map<String, BigDecimal> getInstrumentsExposure()
/*     */   {
/*  87 */     return this.instrumentsExposure;
/*     */   }
/*     */ 
/*     */   public void setInstrumentsExposure(Map<String, BigDecimal> instrumentsExposure)
/*     */   {
/*  94 */     this.instrumentsExposure = instrumentsExposure;
/*     */   }
/*     */ 
/*     */   public Money getUsableMargin()
/*     */   {
/* 101 */     return this.usableMargin;
/*     */   }
/*     */ 
/*     */   public void setUsableMargin(Money usableMargin)
/*     */   {
/* 108 */     this.usableMargin = usableMargin;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.pojo.AccountState
 * JD-Core Version:    0.6.0
 */