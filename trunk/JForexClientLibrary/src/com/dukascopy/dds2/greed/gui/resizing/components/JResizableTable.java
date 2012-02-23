/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.Resizable;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public class JResizableTable extends JTable
/*    */   implements Resizable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int SMALL_ROW_HEIGHT = 16;
/*    */   private static final int MEDIUM_ROW_HEIGHT = 20;
/*    */   private static final int LARGE_ROW_HEIGHT = 24;
/*    */ 
/*    */   public JResizableTable()
/*    */   {
/* 27 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public JResizableTable(TableModel tableModel)
/*    */   {
/* 32 */     super(tableModel);
/* 33 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public JResizableTable(int numRows, int numColumns) {
/* 37 */     super(numRows, numColumns);
/* 38 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 43 */     return Integer.valueOf(getTableHeader().getFont().getSize());
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object size)
/*    */   {
/* 48 */     setFont(getFont().deriveFont(((Float)size).floatValue()));
/* 49 */     getTableHeader().setFont(getTableHeader().getFont().deriveFont(((Float)size).floatValue()));
/* 50 */     apllySizeMode();
/*    */   }
/*    */ 
/*    */   private void apllySizeMode() {
/* 54 */     if (ResizingManager.isLargeMode())
/* 55 */       setRowHeight(24);
/* 56 */     else if (ResizingManager.isMediumMode())
/* 57 */       setRowHeight(20);
/*    */     else
/* 59 */       setRowHeight(16);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.JResizableTable
 * JD-Core Version:    0.6.0
 */