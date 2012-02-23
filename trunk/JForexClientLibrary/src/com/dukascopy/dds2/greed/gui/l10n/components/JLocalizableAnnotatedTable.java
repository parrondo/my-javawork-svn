/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.table.Table;
/*    */ import java.awt.Font;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public class JLocalizableAnnotatedTable<ColumnBean extends Enum<ColumnBean>, Info> extends Table<ColumnBean, Info>
/*    */   implements Localizable
/*    */ {
/*    */   public JLocalizableAnnotatedTable(JLocalizableAnnotatedTableModel<ColumnBean, Info> tableModel)
/*    */   {
/* 14 */     super(tableModel);
/* 15 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 20 */     getTableHeader().setFont(LocalizationManager.getDefaultFont(getTableHeader().getFont().getSize()));
/* 21 */     for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
/* 22 */       getTableHeader().getColumnModel().getColumn(i).setHeaderValue(getModel().getColumnName(i));
/*    */     }
/*    */ 
/* 25 */     getTableHeader().revalidate();
/* 26 */     getTableHeader().repaint();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTable
 * JD-Core Version:    0.6.0
 */