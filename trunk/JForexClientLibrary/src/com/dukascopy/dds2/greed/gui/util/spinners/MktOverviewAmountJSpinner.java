/*    */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class MktOverviewAmountJSpinner extends AmountJSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MktOverviewAmountJSpinner(Instrument instrument)
/*    */   {
/* 24 */     super(instrument);
/*    */   }
/*    */ 
/*    */   public void changeLot()
/*    */   {
/* 31 */     BigDecimal newLotAmount = LotAmountChanger.getLotAmountForInstrument(this.currentInstrument);
/*    */ 
/* 33 */     BigDecimal newValue = LotAmountChanger.calculateAmountForDifferentLot((BigDecimal)getValue(), this.currentLotAmount, newLotAmount);
/*    */ 
/* 35 */     setMinimum(LotAmountChanger.getMinTradableAmount(this.currentInstrument));
/* 36 */     setMaximum(LotAmountChanger.getMaxTradableAmount());
/* 37 */     setStepSize(LotAmountChanger.getAmountStepSizeForLot(this.currentInstrument, newLotAmount));
/* 38 */     setPrecision(LotAmountChanger.getAmountPrecision(this.currentInstrument, newLotAmount));
/* 39 */     setValue(newValue);
/*    */ 
/* 41 */     this.currentLotAmount = newLotAmount;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.MktOverviewAmountJSpinner
 * JD-Core Version:    0.6.0
 */