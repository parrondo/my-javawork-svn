/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */  enum Condition
/*     */ {
/* 107 */   BID_LESS("price.alert.condition.bid", "<", true), 
/* 108 */   BID_GREATER("price.alert.condition.bid", ">", true), 
/* 109 */   ASK_LESS("price.alert.condition.ask", "<", false), 
/* 110 */   ASK_GREATER("price.alert.condition.ask", ">", false);
/*     */ 
/*     */   private String key;
/*     */   private String param;
/*     */   private boolean bidCondition;
/*     */ 
/* 117 */   private Condition(String key, String param, boolean bidCondition) { this.key = key;
/* 118 */     this.param = param;
/* 119 */     this.bidCondition = bidCondition; }
/*     */ 
/*     */   public boolean isBidCondition()
/*     */   {
/* 123 */     return this.bidCondition;
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey() {
/* 127 */     return this.key;
/*     */   }
/*     */ 
/*     */   public String getLocalizationParam() {
/* 131 */     return this.param;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.Condition
 * JD-Core Version:    0.6.0
 */