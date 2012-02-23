/*    */ package com.dukascopy.dds2.greed.model;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public enum OrderCondition
/*    */ {
/* 27 */   MARKET("SD_MK", "L_MK"), 
/*    */ 
/* 29 */   GREATER_BID("SD_B_G", "L_BG"), 
/* 30 */   LESS_BID("SD_B_L", "L_BL"), 
/*    */ 
/* 32 */   GREATER_ASK("SD_A_G", "L_AG"), 
/* 33 */   LESS_ASK("SD_A_L", "L_AL"), 
/*    */ 
/* 35 */   LIMIT_ASK("SD_A_E", "L_AE"), 
/* 36 */   LIMIT_BID("SD_B_E", "L_BE"), 
/*    */ 
/* 38 */   MIT_ASK("SD_A_E_MIT", "L_AES"), 
/* 39 */   MIT_BID("SD_B_E_MIT", "L_BES");
/*    */ 
/*    */   private final String popupTextKey;
/*    */   private final String buttonTextKey;
/*    */ 
/* 45 */   private OrderCondition(String popupTextKey, String buttonTextKey) { this.popupTextKey = popupTextKey;
/* 46 */     this.buttonTextKey = buttonTextKey; }
/*    */ 
/*    */   public String popupTextKey() {
/* 49 */     return this.popupTextKey;
/*    */   }
/* 51 */   public String buttonTextKey() { return this.buttonTextKey; }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return LocalizationManager.getText(this.popupTextKey);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.OrderCondition
 * JD-Core Version:    0.6.0
 */