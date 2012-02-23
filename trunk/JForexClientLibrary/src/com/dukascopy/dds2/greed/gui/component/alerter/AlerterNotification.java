/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */  enum AlerterNotification
/*     */ {
/* 138 */   POPUP("price.alert.popup"), 
/* 139 */   BEEP("price.alert.alarm");
/*     */ 
/*     */   private String key;
/*     */ 
/* 144 */   private AlerterNotification(String key) { this.key = key; }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 148 */     return this.key;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.AlerterNotification
 * JD-Core Version:    0.6.0
 */