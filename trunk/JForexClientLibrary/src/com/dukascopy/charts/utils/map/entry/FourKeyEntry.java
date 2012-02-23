/*    */ package com.dukascopy.charts.utils.map.entry;
/*    */ 
/*    */ public class FourKeyEntry<K1, K2, K3, K4, V> extends ThreeKeyEntry<K1, K2, K3, V>
/*    */   implements IFourKeyEntry<K1, K2, K3, K4, V>
/*    */ {
/*    */   private final K4 k4;
/*    */ 
/*    */   public FourKeyEntry(K1 k1, K2 k2, K3 k3, K4 k4, V v)
/*    */   {
/* 15 */     super(k1, k2, k3, v);
/* 16 */     this.k4 = k4;
/*    */   }
/*    */ 
/*    */   public K4 getKey4()
/*    */   {
/* 21 */     return this.k4;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.FourKeyEntry
 * JD-Core Version:    0.6.0
 */