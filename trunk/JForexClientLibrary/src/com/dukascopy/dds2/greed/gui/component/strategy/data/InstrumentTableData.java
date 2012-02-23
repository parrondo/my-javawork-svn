/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.data;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class InstrumentTableData
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public final Instrument instrument;
/*    */   public final String fileName;
/*    */ 
/*    */   public InstrumentTableData(Instrument instrument, String fileName)
/*    */   {
/* 23 */     this.instrument = instrument;
/* 24 */     this.fileName = fileName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.data.InstrumentTableData
 * JD-Core Version:    0.6.0
 */