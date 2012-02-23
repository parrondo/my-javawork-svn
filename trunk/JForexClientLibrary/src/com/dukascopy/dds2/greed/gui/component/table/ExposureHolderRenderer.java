/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel.ExposureHolder;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger.AmountLot;
/*    */ import java.awt.SystemColor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.MessageFormat;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ 
/*    */ public class ExposureHolderRenderer extends DefaultTableCellRenderer
/*    */ {
/* 20 */   private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("###0.######");
/*    */ 
/* 22 */   private static final String[] template = new String[LotAmountChanger.AmountLot.values().length];
/*    */ 
/*    */   protected void setValue(Object value)
/*    */   {
/* 31 */     if (!(value instanceof ExposureTableModel.ExposureHolder)) {
/* 32 */       return;
/*    */     }
/* 34 */     ExposureTableModel.ExposureHolder holder = (ExposureTableModel.ExposureHolder)value;
/* 35 */     LotAmountChanger.AmountLot amountLot = LotAmountChanger.AmountLot.fromValue(LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(holder.instrument)));
/*    */ 
/* 37 */     String amountL = AMOUNT_FORMAT.format(LotAmountChanger.calculateAmountForDifferentLot(holder.amountL, LotAmountChanger.AmountLot.UNITS.value(), amountLot.value()));
/*    */ 
/* 39 */     String amountS = AMOUNT_FORMAT.format(LotAmountChanger.calculateAmountForDifferentLot(holder.amountS, LotAmountChanger.AmountLot.UNITS.value(), amountLot.value()));
/*    */ 
/* 41 */     String countL = holder.countL + "";
/* 42 */     String countS = holder.countS + "";
/* 43 */     setForeground(SystemColor.textText);
/* 44 */     setText(MessageFormat.format(template[amountLot.ordinal()], new Object[] { amountL, amountS, countL, countS }));
/* 45 */     setHorizontalAlignment(4);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 25 */     template[LotAmountChanger.AmountLot.MILLIONS.ordinal()] = "<html><font color=#00c800>{0} mil.</font> / <font color=#e60000>{1} mil.</font> (<font color=#00c800>{2}</font>/<font color=#e60000>{3}</font>)</html>";
/* 26 */     template[LotAmountChanger.AmountLot.THOUSANDS.ordinal()] = "<html><font color=#00c800>{0} th.</font> / <font color=#e60000>{1} th.</font> (<font color=#00c800>{2}</font>/<font color=#e60000>{3}</font>)</html>";
/* 27 */     template[LotAmountChanger.AmountLot.UNITS.ordinal()] = "<html><font color=#00c800>{0}</font> / <font color=#e60000>{1}</font> (<font color=#00c800>{2}</font>/<font color=#e60000>{3}</font>)</html>";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.ExposureHolderRenderer
 * JD-Core Version:    0.6.0
 */