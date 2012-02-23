/*    */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*    */ 
/*    */ public class CommodityAmountJSpinner extends AmountJSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public CommodityAmountJSpinner(Instrument instrument)
/*    */   {
/* 24 */     super(instrument);
/* 25 */     this.currentInstrument = instrument;
/* 26 */     setEnabled(InstrumentAvailabilityManager.getInstance().isAllowed(instrument));
/*    */   }
/*    */ 
/*    */   public void changeLot()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.CommodityAmountJSpinner
 * JD-Core Version:    0.6.0
 */