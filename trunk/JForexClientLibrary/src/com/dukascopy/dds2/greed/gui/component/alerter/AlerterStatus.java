/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */  enum AlerterStatus
/*     */ {
/* 155 */   INACTIVE("price.alert.status.inactive"), 
/* 156 */   ACTIVE("price.alert.status.active"), 
/* 157 */   COMPLETED("price.alert.status.completed");
/*     */ 
/*     */   private String key;
/*     */ 
/* 162 */   private AlerterStatus(String key) { this.key = key; }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 166 */     return this.key;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.AlerterStatus
 * JD-Core Version:    0.6.0
 */