/*    */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.util.EventObject;
/*    */ import javax.swing.AbstractCellEditor;
/*    */ import javax.swing.JComboBox;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.ListCellRenderer;
/*    */ import javax.swing.table.TableCellEditor;
/*    */ 
/*    */ public class ComboBoxEditor extends AbstractCellEditor
/*    */   implements TableCellEditor
/*    */ {
/*    */   private JComboBox comboBox;
/*    */ 
/*    */   public ComboBoxEditor(Object[] options, AlerterTable alerterTable)
/*    */   {
/* 21 */     this(options, alerterTable, null);
/*    */   }
/*    */ 
/*    */   public ComboBoxEditor(Object[] options, AlerterTable alerterTable, ListCellRenderer renderer) {
/* 25 */     this.comboBox = new JComboBox(options);
/*    */ 
/* 27 */     AlerterTable table = alerterTable;
/* 28 */     this.comboBox.addActionListener(new ActionListener(table)
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 31 */         this.val$table.stopCellEditing();
/*    */       }
/*    */     });
/* 35 */     if (renderer != null)
/* 36 */       this.comboBox.setRenderer(renderer);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(EventObject anEvent)
/*    */   {
/* 41 */     if ((anEvent instanceof MouseEvent)) {
/* 42 */       return ((MouseEvent)anEvent).getClickCount() >= 2;
/*    */     }
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
/* 48 */     return this.comboBox;
/*    */   }
/*    */ 
/*    */   public Object getCellEditorValue() {
/* 52 */     return this.comboBox.getSelectedItem();
/*    */   }
/*    */ 
/*    */   public JComboBox getComboBox() {
/* 56 */     return this.comboBox;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.ComboBoxEditor
 * JD-Core Version:    0.6.0
 */