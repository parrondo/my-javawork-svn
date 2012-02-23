/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public class StrategiesTableHeader extends JTableHeader
/*    */ {
/* 22 */   private final StrategyTableComparator comparator = new StrategyTableComparator();
/*    */ 
/*    */   public StrategiesTableHeader()
/*    */   {
/* 26 */     addMouseListener(new MouseAdapter()
/*    */     {
/*    */       public void mouseClicked(MouseEvent e)
/*    */       {
/* 31 */         int columnIndex = StrategiesTableHeader.this.columnModel.getColumnIndexAtX(e.getX());
/* 32 */         if (columnIndex == -1) {
/* 33 */           return;
/*    */         }
/* 35 */         TableColumn column = StrategiesTableHeader.this.columnModel.getColumn(columnIndex);
/*    */ 
/* 37 */         JTable table = StrategiesTableHeader.this.getTable();
/* 38 */         StrategiesTableModel tableModel = (StrategiesTableModel)table.getModel();
/* 39 */         List strategies = tableModel.getStrategies();
/* 40 */         StrategiesTableHeader.this.comparator.setSortingColumn(column);
/* 41 */         Collections.sort(strategies, StrategiesTableHeader.this.comparator);
/* 42 */         tableModel.fireTableDataChanged();
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public StrategyTableComparator getComparator() {
/* 49 */     return this.comparator;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting.StrategiesTableHeader
 * JD-Core Version:    0.6.0
 */