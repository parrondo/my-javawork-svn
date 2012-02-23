/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ 
/*     */ final class StringTableCellRenderer extends DefaultTableCellRenderer
/*     */ {
/* 162 */   private boolean isRunning = false;
/*     */ 
/*     */   public StringTableCellRenderer() {
/* 165 */     setHorizontalAlignment(0);
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/* 170 */     Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*     */ 
/* 172 */     if (this.isRunning)
/* 173 */       cell.setFont(cell.getFont().deriveFont(1));
/*     */     else {
/* 175 */       cell.setFont(cell.getFont().deriveFont(0));
/*     */     }
/*     */ 
/* 178 */     return cell;
/*     */   }
/*     */ 
/*     */   public boolean isRunning() {
/* 182 */     return this.isRunning;
/*     */   }
/*     */ 
/*     */   public void setRunning(boolean isRunning) {
/* 186 */     this.isRunning = isRunning;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StringTableCellRenderer
 * JD-Core Version:    0.6.0
 */