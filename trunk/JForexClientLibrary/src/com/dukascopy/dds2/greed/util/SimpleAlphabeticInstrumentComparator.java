/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class SimpleAlphabeticInstrumentComparator
/*    */   implements Comparator<String>
/*    */ {
/*    */   public int compare(String o1, String o2)
/*    */   {
/* 12 */     if ((o1 == null) && (o2 == null)) {
/* 13 */       return 0;
/*    */     }
/* 15 */     if ((o1 != null) && (o2 == null)) {
/* 16 */       return 1;
/*    */     }
/* 18 */     if ((o1 == null) && (o2 != null)) {
/* 19 */       return -1;
/*    */     }
/*    */ 
/* 22 */     return o1.compareToIgnoreCase(o2);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.SimpleAlphabeticInstrumentComparator
 * JD-Core Version:    0.6.0
 */