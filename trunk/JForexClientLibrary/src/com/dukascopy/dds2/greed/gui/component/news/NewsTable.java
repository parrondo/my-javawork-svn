/*    */ package com.dukascopy.dds2.greed.gui.component.news;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public class NewsTable extends JLocalizableTable
/*    */ {
/*    */   public NewsTable(TableModel model)
/*    */   {
/* 20 */     super(model);
/* 21 */     getColumnModel().getColumn(0).setMaxWidth(195);
/* 22 */     getColumnModel().getColumn(0).setPreferredWidth(145);
/* 23 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*    */ 
/* 25 */     translate();
/*    */   }
/*    */ 
/*    */   public void translate()
/*    */   {
/* 30 */     getTableHeader().getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("column.date.time"));
/* 31 */     getTableHeader().getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("column.headline"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsTable
 * JD-Core Version:    0.6.0
 */