/*    */ package com.dukascopy.dds2.greed.gui.component.javadoc;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocSrchResult;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.table.AbstractTableModel;
/*    */ 
/*    */ public class JDocSrchTableModel extends AbstractTableModel
/*    */ {
/*    */   private static final int COLUMNS = 2;
/*    */   public static final int COL_MATCH_FILE = 0;
/*    */   public static final int COL_MATCH_TEXT = 1;
/* 20 */   private List<JDocSrchResult> srchResultList = new ArrayList();
/*    */ 
/*    */   public int getColumnCount() {
/* 23 */     return 2;
/*    */   }
/*    */ 
/*    */   public int getRowCount() {
/* 27 */     return this.srchResultList.size();
/*    */   }
/*    */ 
/*    */   public Object getValueAt(int row, int column) {
/* 31 */     if ((row < 0) || (row >= getRowCount())) return null;
/* 32 */     if ((column < 0) || (column >= getColumnCount())) return null;
/*    */ 
/* 34 */     JDocSrchResult srchResult = (JDocSrchResult)this.srchResultList.get(row);
/* 35 */     switch (column) {
/*    */     case 1:
/* 37 */       return "<html>" + srchResult.getText();
/*    */     case 0:
/* 39 */       return srchResult.getFileName();
/*    */     }
/* 41 */     return null;
/*    */   }
/*    */   public void clear() {
/* 44 */     this.srchResultList.clear();
/*    */   }
/*    */   public void addSrchResult(List<JDocSrchResult> srchResults) {
/* 47 */     this.srchResultList.clear();
/* 48 */     for (JDocSrchResult srchResult : srchResults)
/* 49 */       this.srchResultList.add(srchResult);
/*    */   }
/*    */ 
/*    */   public JDocSrchResult getSrchResult(int index)
/*    */   {
/* 54 */     if ((index < 0) || (index >= getRowCount())) return null;
/* 55 */     return (JDocSrchResult)this.srchResultList.get(index);
/*    */   }
/*    */ 
/*    */   public String getFilePath(int row) {
/* 59 */     return ((JDocSrchResult)this.srchResultList.get(row)).getFilePath();
/*    */   }
/*    */ 
/*    */   public String getQuery(int row) {
/* 63 */     return ((JDocSrchResult)this.srchResultList.get(row)).getQuery();
/*    */   }
/*    */ 
/*    */   public JDocSrchResult getRow(int row) {
/* 67 */     return (JDocSrchResult)this.srchResultList.get(row);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.javadoc.JDocSrchTableModel
 * JD-Core Version:    0.6.0
 */