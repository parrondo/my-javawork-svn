/*    */ package com.dukascopy.charts.utils.map.entry;
/*    */ 
/*    */ public class ThreeKeyEntry<K1, K2, K3, V>
/*    */   implements IThreeKeyEntry<K1, K2, K3, V>
/*    */ {
/*    */   private final K1 k1;
/*    */   private final K2 k2;
/*    */   private final K3 k3;
/*    */   private final V v;
/*    */ 
/*    */   public ThreeKeyEntry(K1 k1, K2 k2, K3 k3, V v)
/*    */   {
/* 19 */     this.k1 = k1;
/* 20 */     this.k2 = k2;
/* 21 */     this.k3 = k3;
/* 22 */     this.v = v;
/*    */   }
/*    */ 
/*    */   public K1 getKey1()
/*    */   {
/* 27 */     return this.k1;
/*    */   }
/*    */ 
/*    */   public K2 getKey2()
/*    */   {
/* 32 */     return this.k2;
/*    */   }
/*    */ 
/*    */   public K3 getKey3()
/*    */   {
/* 37 */     return this.k3;
/*    */   }
/*    */ 
/*    */   public V getValue()
/*    */   {
/* 42 */     return this.v;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 47 */     return "<" + String.valueOf(getKey1()) + " - " + String.valueOf(getKey2()) + " - " + String.valueOf(getKey3()) + " - " + String.valueOf(getValue()) + ">";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.map.entry.ThreeKeyEntry
 * JD-Core Version:    0.6.0
 */