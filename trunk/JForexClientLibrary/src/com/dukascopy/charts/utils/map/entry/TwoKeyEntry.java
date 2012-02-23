/*    */ package com.dukascopy.charts.utils.map.entry;
/*    */ 
/*    */ public class TwoKeyEntry<K1, K2, V>
/*    */   implements ITwoKeyEntry<K1, K2, V>
/*    */ {
/*    */   private final K1 k1;
/*    */   private final K2 k2;
/*    */   private final V v;
/*    */ 
/*    */   public TwoKeyEntry(K1 k1, K2 k2, V v)
/*    */   {
/* 18 */     this.k1 = k1;
/* 19 */     this.k2 = k2;
/* 20 */     this.v = v;
/*    */   }
/*    */ 
/*    */   public K1 getKey1()
/*    */   {
/* 25 */     return this.k1;
/*    */   }
/*    */ 
/*    */   public K2 getKey2()
/*    */   {
/* 30 */     return this.k2;
/*    */   }
/*    */ 
/*    */   public V getValue()
/*    */   {
/* 35 */     return this.v;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 40 */     return "<" + String.valueOf(getKey1()) + " - " + String.valueOf(getKey2()) + " - " + String.valueOf(getValue()) + ">";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.TwoKeyEntry
 * JD-Core Version:    0.6.0
 */