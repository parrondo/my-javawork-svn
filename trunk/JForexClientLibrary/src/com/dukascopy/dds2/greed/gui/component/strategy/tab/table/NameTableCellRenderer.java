/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarUIConstants;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.io.File;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ 
/*     */ final class NameTableCellRenderer extends DefaultTableCellRenderer
/*     */ {
/*     */   private StrategyNewBean strategy;
/*     */ 
/*     */   public NameTableCellRenderer()
/*     */   {
/* 121 */     setHorizontalAlignment(2);
/*     */   }
/*     */ 
/*     */   public StrategyNewBean getStrategy() {
/* 125 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public void setStrategy(StrategyNewBean strategy) {
/* 129 */     this.strategy = strategy;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/* 135 */     JLabel cell = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*     */ 
/* 137 */     if (getStrategy().getStrategySourceFile() != null)
/*     */     {
/* 139 */       if (getStrategy().getStrategySourceFile().getAbsolutePath().endsWith(".java"))
/* 140 */         cell.setIcon(StrategiesToolbarUIConstants.STRATEGIES_SOURCE_ICON);
/*     */       else {
/* 142 */         cell.setIcon(StrategiesToolbarUIConstants.STRATEGIES_SOURCE_OTHER_ICON);
/*     */       }
/*     */     }
/*     */     else {
/* 146 */       cell.setIcon(StrategiesToolbarUIConstants.STRATEGIES_BINARY_ICON);
/*     */     }
/*     */ 
/* 149 */     if (this.strategy.isStartingOrRunning())
/* 150 */       cell.setFont(cell.getFont().deriveFont(1));
/*     */     else {
/* 152 */       cell.setFont(cell.getFont().deriveFont(0));
/*     */     }
/*     */ 
/* 155 */     return cell;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.NameTableCellRenderer
 * JD-Core Version:    0.6.0
 */