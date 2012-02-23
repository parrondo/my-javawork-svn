/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarUIConstants;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ 
/*     */ final class StatusTableCellRenderer extends DefaultTableCellRenderer
/*     */ {
/* 194 */   private boolean isRunning = false;
/*     */ 
/*     */   public StatusTableCellRenderer() {
/* 197 */     setHorizontalAlignment(0);
/*     */   }
/*     */ 
/*     */   public boolean isRunning() {
/* 201 */     return this.isRunning;
/*     */   }
/*     */ 
/*     */   public void setRunning(boolean isRunning) {
/* 205 */     this.isRunning = isRunning;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/* 210 */     Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 211 */     if (this.isRunning)
/* 212 */       cell.setFont(super.getFont().deriveFont(1));
/*     */     else {
/* 214 */       cell.setFont(super.getFont().deriveFont(0));
/*     */     }
/*     */ 
/* 217 */     if (column == 0) {
/* 218 */       if (this.isRunning)
/* 219 */         ((JLabel)cell).setIcon(StrategiesToolbarUIConstants.STRATEGIES_RUNNING_ICON);
/*     */       else {
/* 221 */         ((JLabel)cell).setIcon(StrategiesToolbarUIConstants.STRATEGIES_STOPPED_ICON);
/*     */       }
/*     */     }
/*     */ 
/* 225 */     return cell;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StatusTableCellRenderer
 * JD-Core Version:    0.6.0
 */