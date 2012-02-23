/*    */ package com.dukascopy.dds2.greed.model;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public enum OrderType
/*    */ {
/* 19 */   MARKET("order.type.market"), 
/* 20 */   ENTRY("order.type.entry"), 
/* 21 */   STOP_LOSS("order.type.stop.loss"), 
/* 22 */   TAKE_PROFIT("order.type.take.profit"), 
/* 23 */   BID("order.type.bid"), 
/* 24 */   OFFER("order.type.offer"), 
/* 25 */   MIT("order.type.loss"), 
/* 26 */   LIMIT("order.type.limit"), 
/* 27 */   PART_CLOSE("order.type.part.close"), 
/* 28 */   IF_DONE_STOP("order.type.if.done.stop"), 
/* 29 */   IF_DONE_LIMIT("order.type.if.done.limit");
/*    */ 
/*    */   private final String textKey;
/*    */ 
/* 32 */   private OrderType(String textKey) { this.textKey = textKey;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return LocalizationManager.getText(this.textKey);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.OrderType
 * JD-Core Version:    0.6.0
 */