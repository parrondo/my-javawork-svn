/*    */ package com.dukascopy.dds2.greed.gui.component.marketdepth;
/*    */ 
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ 
/*    */ public class MarketDepthTableModel extends DefaultTableModel
/*    */ {
/*    */   public MarketDepthTableModel(int rowCount)
/*    */   {
/* 13 */     setRowCount(rowCount);
/* 14 */     setColumnCount(4);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(int row, int column) {
/* 18 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthTableModel
 * JD-Core Version:    0.6.0
 */