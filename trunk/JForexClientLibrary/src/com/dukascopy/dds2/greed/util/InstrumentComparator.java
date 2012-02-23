/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ class InstrumentComparator
/*    */   implements Comparator<String>
/*    */ {
/*    */   public int compare(String pair1, String pair2)
/*    */   {
/* 10 */     if ((pair1.equals(pair2)) || (pair2.equals(pair1)))
/* 11 */       return 0;
/* 12 */     if (("EUR/USD".equals(pair1)) && (("GBP/USD".equals(pair2)) || ("USD/CHF".equals(pair2)) || ("USD/JPY".equals(pair2))))
/* 13 */       return -1;
/* 14 */     if (("GBP/USD".equals(pair1)) && (("USD/CHF".equals(pair2)) || ("USD/JPY".equals(pair2))))
/* 15 */       return -1;
/* 16 */     if (("USD/CHF".equals(pair1)) && ("USD/JPY".equals(pair2))) {
/* 17 */       return -1;
/*    */     }
/* 19 */     if (("USD/JPY".equals(pair1)) && (("USD/CHF".equals(pair2)) || ("GBP/USD".equals(pair2)) || ("EUR/USD".equals(pair2))))
/* 20 */       return 1;
/* 21 */     if (("USD/CHF".equals(pair1)) && (("GBP/USD".equals(pair2)) || ("EUR/USD".equals(pair2))))
/* 22 */       return 1;
/* 23 */     if (("GBP/USD".equals(pair1)) && ("EUR/USD".equals(pair1))) {
/* 24 */       return 1;
/*    */     }
/* 26 */     if (("EUR/USD".equals(pair1)) || ("GBP/USD".equals(pair1)) || ("USD/CHF".equals(pair1)) || ("USD/JPY".equals(pair1))) {
/* 27 */       return -1;
/*    */     }
/* 29 */     if (("EUR/USD".equals(pair2)) || ("GBP/USD".equals(pair2)) || ("USD/CHF".equals(pair2)) || ("USD/JPY".equals(pair2))) {
/* 30 */       return 1;
/*    */     }
/* 32 */     return pair1.compareTo(pair2);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.InstrumentComparator
 * JD-Core Version:    0.6.0
 */