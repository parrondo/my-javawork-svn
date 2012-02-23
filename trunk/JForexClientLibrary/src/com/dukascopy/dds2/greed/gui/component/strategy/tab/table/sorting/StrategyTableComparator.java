/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*    */ import java.util.Comparator;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public class StrategyTableComparator
/*    */   implements Comparator<StrategyNewBean>
/*    */ {
/*    */   private static final int SORT_ASC = 1;
/*    */   private static final int SORT_DESC = -1;
/* 22 */   private Map<TableColumn, Integer> sortingMap = new HashMap();
/*    */   private TableColumn sortingColumn;
/*    */ 
/*    */   public int compare(StrategyNewBean o1, StrategyNewBean o2)
/*    */   {
/* 28 */     if ("NAME_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 29 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getName().compareTo(o2.getName());
/*    */     }
/*    */ 
/* 32 */     if ("START_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 33 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getStartTime().compareTo(o2.getStartTime());
/*    */     }
/*    */ 
/* 36 */     if ("TIME_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 37 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getDurationTime().compareTo(o2.getDurationTime());
/*    */     }
/*    */ 
/* 40 */     if ("END_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 41 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getEndTime().compareTo(o2.getEndTime());
/*    */     }
/*    */ 
/* 44 */     if ("TYPE_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 45 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getType().compareTo(o2.getType());
/*    */     }
/*    */ 
/* 50 */     if ("STATUS_COLUMN_IDENTIFIER".equals(this.sortingColumn.getIdentifier())) {
/* 51 */       return ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() * o1.getStatus().compareTo(o2.getStatus());
/*    */     }
/*    */ 
/* 54 */     return 0;
/*    */   }
/*    */ 
/*    */   public void setSortingColumn(TableColumn column) {
/* 58 */     this.sortingColumn = column;
/* 59 */     this.sortingMap.put(column, Integer.valueOf(1 == ((Integer)this.sortingMap.get(this.sortingColumn)).intValue() ? -1 : 1));
/*    */   }
/*    */ 
/*    */   public void initSortingMap(TableColumnModel columnModel) {
/* 63 */     this.sortingMap.put(columnModel.getColumn(1), Integer.valueOf(1));
/* 64 */     this.sortingMap.put(columnModel.getColumn(2), Integer.valueOf(1));
/* 65 */     this.sortingMap.put(columnModel.getColumn(3), Integer.valueOf(1));
/* 66 */     this.sortingMap.put(columnModel.getColumn(4), Integer.valueOf(1));
/* 67 */     this.sortingMap.put(columnModel.getColumn(5), Integer.valueOf(1));
/* 68 */     this.sortingMap.put(columnModel.getColumn(6), Integer.valueOf(1));
/* 69 */     this.sortingMap.put(columnModel.getColumn(7), Integer.valueOf(1));
/* 70 */     this.sortingMap.put(columnModel.getColumn(8), Integer.valueOf(1));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting.StrategyTableComparator
 * JD-Core Version:    0.6.0
 */