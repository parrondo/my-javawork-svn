/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class Alert
/*     */ {
/*     */   private Instrument instrument;
/*     */   private Condition condition;
/*     */   private BigDecimal price;
/*     */   private AlerterNotification notification;
/*     */   private AlerterStatus status;
/*     */   private Date completedTime;
/*     */ 
/*     */   public Alert(Instrument instrument)
/*     */   {
/*  28 */     this.instrument = instrument;
/*  29 */     this.condition = Condition.values()[0];
/*  30 */     this.notification = AlerterNotification.values()[0];
/*  31 */     this.price = BigDecimal.valueOf(getInitialPrice());
/*  32 */     this.status = AlerterStatus.INACTIVE;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/*  36 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument newInstrument) {
/*  40 */     if (newInstrument != this.instrument) {
/*  41 */       this.instrument = newInstrument;
/*  42 */       this.price = BigDecimal.valueOf(getInitialPrice());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Condition getCondition() {
/*  47 */     return this.condition;
/*     */   }
/*     */ 
/*     */   public void setCondition(Condition condition) {
/*  51 */     this.condition = condition;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice() {
/*  55 */     return this.price;
/*     */   }
/*     */ 
/*     */   public void setPrice(BigDecimal price) {
/*  59 */     this.price = price;
/*     */   }
/*     */ 
/*     */   public AlerterNotification getNotification() {
/*  63 */     return this.notification;
/*     */   }
/*     */ 
/*     */   public void setNotification(AlerterNotification notification) {
/*  67 */     this.notification = notification;
/*     */   }
/*     */ 
/*     */   public AlerterStatus getStatus() {
/*  71 */     return this.status;
/*     */   }
/*     */ 
/*     */   public void setStatus(AlerterStatus status) {
/*  75 */     this.status = status;
/*     */   }
/*     */ 
/*     */   public Date getCompletedTime() {
/*  79 */     return this.completedTime;
/*     */   }
/*     */ 
/*     */   public void complete() {
/*  83 */     this.status = AlerterStatus.COMPLETED;
/*  84 */     this.completedTime = new Date();
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  88 */     return this.instrument + " " + this.condition + " " + this.price;
/*     */   }
/*     */ 
/*     */   private double getInitialPrice() {
/*  92 */     ITick tick = FeedDataProvider.getDefaultInstance().getLastTick(this.instrument);
/*     */ 
/*  94 */     if (tick == null) {
/*  95 */       return this.instrument.getPipValue();
/*     */     }
/*  97 */     if (this.condition.isBidCondition()) {
/*  98 */       return tick.getBid();
/*     */     }
/* 100 */     return tick.getAsk();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.Alert
 * JD-Core Version:    0.6.0
 */