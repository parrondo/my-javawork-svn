/*    */ package com.dukascopy.charts.tablebuilder.component.table;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.StringSelection;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.KeyStroke;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public abstract class DataTablePresentationAbstractJTable<ColumnBean extends Enum<ColumnBean>, T extends Data> extends JLocalizableAnnotatedTable<ColumnBean, T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DataTablePresentationAbstractJTable(JLocalizableAnnotatedTableModel<ColumnBean, T> tableModel)
/*    */   {
/* 28 */     super(tableModel);
/*    */ 
/* 30 */     getTableHeader().setReorderingAllowed(true);
/*    */ 
/* 32 */     initialize();
/*    */   }
/*    */ 
/*    */   private void initialize() {
/* 36 */     KeyStroke copy = KeyStroke.getKeyStroke(67, 2, false);
/* 37 */     registerKeyboardAction(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 40 */         if ((e.getSource() instanceof JTable)) {
/* 41 */           JTable table = (JTable)e.getSource();
/* 42 */           int selectedColumn = table.getSelectedColumn();
/* 43 */           int selectedRow = table.getSelectedRow();
/* 44 */           if ((selectedColumn > -1) && (selectedRow > -1)) {
/* 45 */             Object value = table.getModel().getValueAt(selectedRow, selectedColumn);
/* 46 */             String strValue = String.valueOf(value);
/* 47 */             StringSelection stringSelection = new StringSelection(strValue);
/* 48 */             Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     , "Copy", copy, 0);
/*    */   }
/*    */ 
/*    */   public abstract AbstractDataTableBackgroundRenderer getRenderer();
/*    */ 
/*    */   public String getPattern()
/*    */   {
/* 58 */     return getRenderer().getPattern();
/*    */   }
/*    */ 
/*    */   public void setPattern(String pattern) {
/* 62 */     getRenderer().setPattern(pattern);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable
 * JD-Core Version:    0.6.0
 */