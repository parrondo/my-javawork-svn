/*    */ package com.dukascopy.charts.data.orders;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class ExposureData
/*    */   implements Cloneable
/*    */ {
/*    */   public Instrument instrument;
/* 17 */   public BigDecimal amount = BigDecimal.ZERO;
/*    */   public IEngine.OrderCommand side;
/*    */   public BigDecimal price;
/*    */   public long time;
/*    */ 
/*    */   public ExposureData(Instrument instrument)
/*    */   {
/* 23 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public ExposureData clone()
/*    */   {
/*    */     try
/*    */     {
/* 30 */       return (ExposureData)super.clone();
/*    */     } catch (Exception e) {
/*    */     }
/* 33 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.orders.ExposureData
 * JD-Core Version:    0.6.0
 */