/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableTable;
/*    */ import java.awt.Font;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public abstract class JLocalizableTable extends JResizableTable
/*    */   implements Localizable
/*    */ {
/*    */   public JLocalizableTable(TableModel tableModel)
/*    */   {
/* 13 */     super(tableModel);
/* 14 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableTable()
/*    */   {
/* 19 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableTable(int numRows, int numColumns) {
/* 23 */     super(numRows, numColumns);
/* 24 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 29 */     if (getTableHeader() != null)
/* 30 */       getTableHeader().setFont(LocalizationManager.getDefaultFont(getTableHeader().getFont().getSize()));
/* 31 */     translate();
/*    */ 
/* 33 */     if (getTableHeader() == null) return;
/*    */ 
/* 35 */     getTableHeader().revalidate();
/* 36 */     getTableHeader().repaint();
/*    */   }
/*    */   public abstract void translate();
/*    */ 
/*    */   public String getTableId() {
/* 42 */     return getClass().getSimpleName();
/*    */   }
/*    */ 
/*    */   protected void hideColumn(int columnIndex) {
/* 46 */     getColumnModel().getColumn(columnIndex).setWidth(0);
/* 47 */     getColumnModel().getColumn(columnIndex).setMinWidth(0);
/* 48 */     getColumnModel().getColumn(columnIndex).setMaxWidth(0);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable
 * JD-Core Version:    0.6.0
 */