/*    */ package com.dukascopy.charts.dialogs.indicators;
/*    */ 
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.event.TableModelEvent;
/*    */ import javax.swing.event.TableModelListener;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ class IndicatorParametersTable extends JTable
/*    */ {
/*    */   public IndicatorParametersTable(IndicatorParametersTableModel tableModel, boolean isTicks)
/*    */   {
/* 17 */     super(tableModel);
/* 18 */     this.tableHeader.setReorderingAllowed(false);
/* 19 */     setRowSelectionAllowed(false);
/* 20 */     setColumnSelectionAllowed(false);
/* 21 */     setCellSelectionEnabled(false);
/* 22 */     setFocusable(false);
/* 23 */     setShowGrid(false);
/* 24 */     setRowHeight(getRowHeight() + 5);
/* 25 */     setFillsViewportHeight(true);
/*    */ 
/* 29 */     IndicatorParameterTableCellEditor editor = new IndicatorParameterTableCellEditor(tableModel, isTicks);
/* 30 */     getColumnModel().getColumn(1).setCellEditor(editor);
/* 31 */     getColumnModel().getColumn(1).setCellRenderer(editor);
/*    */ 
/* 33 */     tableModel.addTableModelListener(new TableModelListener(editor)
/*    */     {
/*    */       public void tableChanged(TableModelEvent e) {
/* 36 */         this.val$editor.build();
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorParametersTable
 * JD-Core Version:    0.6.0
 */