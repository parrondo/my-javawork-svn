/*    */ package com.dukascopy.dds2.greed.gui.component.javadoc;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public class JDocSrchTable extends JLocalizableTable
/*    */ {
/*    */   public JDocSrchTable(JDocSrchTableModel model)
/*    */   {
/* 24 */     super(model);
/* 25 */     getColumnModel().getColumn(0).setMaxWidth(195);
/* 26 */     getColumnModel().getColumn(0).setPreferredWidth(145);
/* 27 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*    */ 
/* 29 */     translate();
/*    */   }
/*    */ 
/*    */   public void translate()
/*    */   {
/* 34 */     getTableHeader().getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("column.jdoc.file"));
/* 35 */     getTableHeader().getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("column.jdoc.text"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.javadoc.JDocSrchTable
 * JD-Core Version:    0.6.0
 */