/*   */ package com.dukascopy.dds2.common;
/*   */ 
/*   */ import java.math.BigDecimal;
/*   */ import java.util.Comparator;
/*   */ 
/*   */ public class AskComparator
/*   */   implements Comparator<BigDecimal>
/*   */ {
/*   */   public int compare(BigDecimal ask1, BigDecimal ask2)
/*   */   {
/* 9 */     return ask1.compareTo(ask2);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.common.AskComparator
 * JD-Core Version:    0.6.0
 */