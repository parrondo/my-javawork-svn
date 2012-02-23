/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import java.awt.Color;
/*    */ import java.math.BigDecimal;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Currency;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ 
/*    */ public class MoneyCellRenderer extends DefaultTableCellRenderer
/*    */ {
/* 21 */   private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
/* 22 */   public static final Color COLOR_POSITIVE = new Color(0, 180, 0);
/* 23 */   public static final Color COLOR_NEGATIVE = new Color(200, 0, 0);
/*    */ 
/*    */   protected void setValue(Object value)
/*    */   {
/* 34 */     if ((value instanceof Money)) {
/* 35 */       Money moneyValue = (Money)value;
/* 36 */       if (moneyValue.getValue() == null) {
/* 37 */         setText("N/A");
/* 38 */         setForeground(COLOR_POSITIVE);
/*    */       } else {
/* 40 */         Currency currency = moneyValue.getCurrency();
/* 41 */         String symbol = currency.getSymbol();
/* 42 */         String formatted = symbol + " " + MONEY_FORMAT.format(moneyValue.getValue());
/*    */ 
/* 46 */         setText(formatted);
/*    */ 
/* 48 */         if (moneyValue.getValue().compareTo(BigDecimal.ZERO) > -1)
/* 49 */           setForeground(COLOR_POSITIVE);
/*    */         else
/* 51 */           setForeground(COLOR_NEGATIVE);
/*    */       }
/*    */     }
/* 54 */     else if ((value instanceof String)) {
/* 55 */       setText((String)value);
/* 56 */       setForeground(COLOR_POSITIVE);
/*    */     }
/*    */     else
/*    */     {
/* 62 */       setText("N/A");
/* 63 */       setForeground(COLOR_POSITIVE);
/*    */     }
/* 65 */     setHorizontalAlignment(4);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer
 * JD-Core Version:    0.6.0
 */