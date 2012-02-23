/*   */ package com.dukascopy.dds2.common;
/*   */ 
/*   */ import java.math.BigDecimal;
/*   */ import java.util.Comparator;
/*   */ 
/*   */ public class BidComparator
/*   */   implements Comparator<BigDecimal>
/*   */ {
/*   */   public int compare(BigDecimal bid1, BigDecimal bid2)
/*   */   {
/* 9 */     return -bid1.compareTo(bid2);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.common.BidComparator
 * JD-Core Version:    0.6.0
 */