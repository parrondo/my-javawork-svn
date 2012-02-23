/*    */ package com.dukascopy.charts.data.datacache.nulls;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ 
/*    */ public class NullIBar
/*    */   implements IBar
/*    */ {
/*    */   public double getClose()
/*    */   {
/*  9 */     return (0.0D / 0.0D);
/*    */   }
/*    */ 
/*    */   public double getHigh()
/*    */   {
/* 14 */     return (0.0D / 0.0D);
/*    */   }
/*    */ 
/*    */   public double getLow()
/*    */   {
/* 19 */     return (0.0D / 0.0D);
/*    */   }
/*    */ 
/*    */   public double getOpen()
/*    */   {
/* 24 */     return (0.0D / 0.0D);
/*    */   }
/*    */ 
/*    */   public long getTime()
/*    */   {
/* 29 */     return 0L;
/*    */   }
/*    */ 
/*    */   public double getVolume()
/*    */   {
/* 34 */     return (0.0D / 0.0D);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.nulls.NullIBar
 * JD-Core Version:    0.6.0
 */