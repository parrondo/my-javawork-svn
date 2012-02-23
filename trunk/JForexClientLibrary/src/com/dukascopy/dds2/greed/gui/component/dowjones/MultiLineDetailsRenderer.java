/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*     */ 
/*     */ import com.dukascopy.api.ICalendarMessage.Detail;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarColumn;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*     */ 
/*     */ public class MultiLineDetailsRenderer extends AbstractHighlightedTableCellRenderer<JTextArea>
/*     */ {
/*  23 */   private static final Border RED_BORDER = BorderFactory.createLineBorder(Color.RED);
/*     */ 
/*  26 */   protected DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(198, 198, 250));
/*     */ 
/*     */   public MultiLineDetailsRenderer() {
/*  29 */     super(new JTextArea());
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex)
/*     */   {
/*  41 */     List details = (List)value;
/*     */ 
/*  43 */     int rowHeight = Math.max(1, details.size()) * table.getRowHeight();
/*  44 */     if (table.getRowHeight(rowIndex) != rowHeight) {
/*  45 */       table.setRowHeight(rowIndex, rowHeight);
/*     */     }
/*     */ 
/*  48 */     JTextArea comp = (JTextArea)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, columnIndex);
/*  49 */     comp.setRows(Math.max(1, details.size()));
/*     */ 
/*  51 */     if (((DJNewsTableModel)table.getModel()).isHighlighted(table.convertRowIndexToModel(rowIndex))) {
/*  52 */       comp.setBorder(RED_BORDER);
/*     */     }
/*  54 */     return comp;
/*     */   }
/*     */ 
/*     */   protected void customizeTextComponent()
/*     */   {
/*  62 */     JTextArea textArea = (JTextArea)getTextComponent();
/*  63 */     textArea.setEditable(false);
/*  64 */     textArea.setLineWrap(false);
/*     */   }
/*     */ 
/*     */   protected String getValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex)
/*     */   {
/*  75 */     StringBuilder sb = new StringBuilder();
/*  76 */     List details = (List)value;
/*  77 */     CalendarColumn column = CalendarColumn.values()[table.convertColumnIndexToModel(columnIndex)];
/*  78 */     if (details != null) {
/*  79 */       for (ICalendarMessage.Detail detail : details) {
/*  80 */         String columnValue = getValue(detail, column);
/*  81 */         if (columnValue != null) {
/*  82 */           sb.append(columnValue);
/*     */         }
/*  84 */         sb.append(System.getProperty("line.separator"));
/*     */       }
/*     */     }
/*  87 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String getValue(ICalendarMessage.Detail detail, CalendarColumn column)
/*     */   {
/*  96 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$dowjones$calendar$CalendarColumn[column.ordinal()]) { case 1:
/*  97 */       return detail.getActual();
/*     */     case 2:
/*  98 */       return detail.getDescription();
/*     */     case 3:
/*  99 */       return detail.getExpected();
/*     */     case 4:
/* 100 */       return detail.getPrevious();
/*     */     }
/* 102 */     return "?";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.MultiLineDetailsRenderer
 * JD-Core Version:    0.6.0
 */