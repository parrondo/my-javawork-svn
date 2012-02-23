/*    */ package com.dukascopy.dds2.greed.agent.strategy.objects;
/*    */ 
/*    */ public class Offer
/*    */ {
/* 13 */   private double volume = 0.0D;
/*    */ 
/* 15 */   private double price = 0.0D;
/*    */ 
/*    */   public Offer(double price, double volume) {
/* 18 */     this.price = price;
/* 19 */     this.volume = volume;
/*    */   }
/*    */ 
/*    */   public double getVolume() {
/* 23 */     return this.volume;
/*    */   }
/*    */ 
/*    */   public double getPrice()
/*    */   {
/* 28 */     return this.price;
/*    */   }
/*    */ 
/*    */   public void setVolume(double volume) {
/* 32 */     this.volume = volume;
/*    */   }
/*    */ 
/*    */   public void setPrice(double price) {
/* 36 */     this.price = price;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.objects.Offer
 * JD-Core Version:    0.6.0
 */