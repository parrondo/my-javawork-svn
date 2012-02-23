/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public class MinTradableAmounts<K, V> extends Hashtable<K, V>
/*    */ {
/*    */   private V minTradableAmountInUnits;
/*    */ 
/*    */   public MinTradableAmounts(V minTradableAmountInUnits)
/*    */   {
/* 15 */     this.minTradableAmountInUnits = minTradableAmountInUnits;
/*    */   }
/*    */ 
/*    */   public synchronized V get(Object key)
/*    */   {
/* 24 */     Object value = super.get(key);
/* 25 */     if (value == null) {
/* 26 */       return this.minTradableAmountInUnits;
/*    */     }
/* 28 */     return value;
/*    */   }
/*    */ 
/*    */   public synchronized V getMinTradableAmountInUnits(Object key)
/*    */   {
/* 33 */     return get(key);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.MinTradableAmounts
 * JD-Core Version:    0.6.0
 */