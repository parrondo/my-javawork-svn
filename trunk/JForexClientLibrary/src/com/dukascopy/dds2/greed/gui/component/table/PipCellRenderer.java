/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import java.text.DecimalFormat;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ 
/*    */ public class PipCellRenderer extends DefaultTableCellRenderer
/*    */ {
/* 16 */   private static final DecimalFormat PIP_FORMAT = new DecimalFormat("#,##0.0");
/*    */ 
/*    */   protected void setValue(Object value)
/*    */   {
/* 27 */     if ((value instanceof BigDecimal)) {
/* 28 */       BigDecimal pipValue = (BigDecimal)value;
/* 29 */       setText(PIP_FORMAT.format(pipValue));
/* 30 */       if (pipValue.compareTo(BigDecimal.ZERO) > -1)
/* 31 */         setForeground(MoneyCellRenderer.COLOR_POSITIVE);
/*    */       else {
/* 33 */         setForeground(MoneyCellRenderer.COLOR_NEGATIVE);
/*    */       }
/* 35 */       setHorizontalAlignment(4);
/*    */     } else {
/* 37 */       setText("N/A");
/* 38 */       setForeground(MoneyCellRenderer.COLOR_POSITIVE);
/* 39 */       setHorizontalAlignment(4);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.PipCellRenderer
 * JD-Core Version:    0.6.0
 */