/*    */ package com.dukascopy.dds2.greed.gui.table.renderers;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class DateTableCellRenderer extends AbstractTableCellRenderer
/*    */ {
/* 17 */   private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT0");
/*    */ 
/* 19 */   public static final SimpleDateFormat SHORT_FORMAT = new SimpleDateFormat() { } ;
/*    */ 
/* 23 */   public static final SimpleDateFormat LONG_FORMAT = new SimpleDateFormat() { } ;
/*    */   private final SimpleDateFormat format;
/*    */ 
/*    */   public DateTableCellRenderer(SimpleDateFormat format)
/*    */   {
/* 31 */     this.format = format;
/* 32 */     this.format.setTimeZone(TIME_ZONE);
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 42 */     JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*    */ 
/* 44 */     Date date = (Date)value;
/*    */ 
/* 46 */     if (date != null) {
/* 47 */       label.setText(this.format.format(date));
/*    */     }
/*    */ 
/* 50 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.table.renderers.DateTableCellRenderer
 * JD-Core Version:    0.6.0
 */