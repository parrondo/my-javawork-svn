/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class DateTableCellRenderer extends AbstractHighlightedTableCellRenderer<JTextField>
/*    */ {
/* 18 */   private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT0");
/*    */ 
/* 20 */   public static final SimpleDateFormat SHORT_FORMAT = new SimpleDateFormat() { } ;
/*    */ 
/* 24 */   public static final SimpleDateFormat LONG_FORMAT = new SimpleDateFormat() { } ;
/*    */   private final SimpleDateFormat format;
/*    */ 
/*    */   public DateTableCellRenderer(SimpleDateFormat format)
/*    */   {
/* 31 */     super(new JTextField());
/* 32 */     this.format = format;
/* 33 */     this.format.setTimeZone(TIME_ZONE);
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 43 */     return (JTextField)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*    */   }
/*    */ 
/*    */   protected void customizeTextComponent()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected String getValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex)
/*    */   {
/* 58 */     return this.format.format((Date)value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.DateTableCellRenderer
 * JD-Core Version:    0.6.0
 */