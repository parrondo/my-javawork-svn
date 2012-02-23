/*    */ package com.dukascopy.dds2.greed.gui.util.lotamount;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class LotAmountLabel extends JLocalizableLabel
/*    */   implements LotAmountChangable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 15 */   private String key = null;
/* 16 */   private Instrument instrument = null;
/*    */ 
/*    */   public LotAmountLabel()
/*    */   {
/* 20 */     LotAmountChanger.addChangable(this);
/* 21 */     setSelectedLotLabel(LotAmountChanger.getCurrentLotAmount());
/*    */   }
/*    */ 
/*    */   public LotAmountLabel(BigDecimal defLotAmount)
/*    */   {
/* 26 */     LotAmountChanger.addChangable(this);
/* 27 */     setSelectedLotLabel(defLotAmount);
/*    */   }
/*    */ 
/*    */   public LotAmountLabel(String key)
/*    */   {
/* 32 */     this.key = key;
/* 33 */     LotAmountChanger.addChangable(this);
/* 34 */     setSelectedLotLabel(LotAmountChanger.getCurrentLotAmount());
/*    */   }
/*    */ 
/*    */   public LotAmountLabel(Instrument instrument)
/*    */   {
/* 39 */     this.instrument = instrument;
/* 40 */     LotAmountChanger.addChangable(this);
/* 41 */     setSelectedLotLabel(LotAmountChanger.getLotAmountForInstrument(getInstrument()));
/*    */   }
/*    */ 
/*    */   private Instrument getInstrument() {
/* 45 */     return this.instrument != null ? this.instrument : LotAmountChanger.getSelectedInstrument();
/*    */   }
/*    */ 
/*    */   public void changeLot()
/*    */   {
/* 50 */     setSelectedLotLabel(LotAmountChanger.getLotAmountForInstrument(getInstrument()));
/*    */   }
/*    */ 
/*    */   public void changeLot(BigDecimal newLotAmount) {
/* 54 */     setSelectedLotLabel(newLotAmount);
/*    */   }
/*    */ 
/*    */   protected void setSelectedLotLabel(BigDecimal lotAmount) {
/* 58 */     if (this.key == null) {
/* 59 */       if (LotAmountChanger.AmountLot.MILLIONS.value().compareTo(lotAmount) == 0) setText("label.amunt.millions");
/* 60 */       if (LotAmountChanger.AmountLot.THOUSANDS.value().compareTo(lotAmount) == 0) setText("label.amunt.thousands");
/* 61 */       if (LotAmountChanger.AmountLot.UNITS.value().compareTo(lotAmount) == 0) setText("label.amunt.units"); 
/*    */     }
/*    */     else {
/* 63 */       setText(this.key);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountLabel
 * JD-Core Version:    0.6.0
 */