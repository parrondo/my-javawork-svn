/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Insets;
/*    */ import javax.swing.DefaultCellEditor;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableCellEditor;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public class InstrumentSelectionTable extends JTable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public InstrumentSelectionTable(InstrumentSelectionTableModel instrumentSelectionTableModel, boolean visualModeEnabled)
/*    */   {
/* 19 */     super(instrumentSelectionTableModel);
/*    */ 
/* 21 */     this.tableHeader.setReorderingAllowed(false);
/* 22 */     setRowSelectionAllowed(false);
/* 23 */     setCellSelectionEnabled(false);
/* 24 */     setFocusable(false);
/* 25 */     setRowHeight(getRowHeight() + 5);
/* 26 */     setRowHeight(getRowHeight());
/* 27 */     setFillsViewportHeight(true);
/*    */ 
/* 30 */     setupCheckBoxColumn(0, 0, true);
/*    */ 
/* 33 */     setupLabelColumn(1);
/*    */   }
/*    */ 
/*    */   private void setupCheckBoxColumn(int columnIndex, int orientation, boolean enabled) {
/* 37 */     CheckBoxCellEditor selectInstrumentCellEditor = new CheckBoxCellEditor(orientation, enabled);
/* 38 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 39 */     column.setCellEditor(selectInstrumentCellEditor);
/* 40 */     column.setCellRenderer(selectInstrumentCellEditor);
/*    */ 
/* 42 */     if (columnIndex == 0)
/* 43 */       column.setMaxWidth(selectInstrumentCellEditor.getPreferredSize().width + 40);
/*    */     else
/* 45 */       column.setMaxWidth(selectInstrumentCellEditor.getPreferredSize().width + 100);
/*    */   }
/*    */ 
/*    */   private void setupLabelColumn(int columnIndex)
/*    */   {
/* 50 */     if (columnIndex == 1) {
/* 51 */       setColumnMaxWidth(columnIndex, 150);
/* 52 */       setColumnPreferredWidth(columnIndex, 100);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void setColumnMaxWidth(int columnIndex, int width) {
/* 57 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 58 */     column.setMaxWidth(width);
/*    */   }
/*    */ 
/*    */   private void setColumnPreferredWidth(int columnIndex, int width) {
/* 62 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 63 */     column.setPreferredWidth(width);
/*    */   }
/*    */ 
/*    */   private static class CheckBoxCellEditor extends DefaultCellEditor
/*    */     implements TableCellRenderer, TableCellEditor
/*    */   {
/*    */     private static final long serialVersionUID = 1L;
/* 69 */     private JCheckBox rendererComponent = new JCheckBox();
/*    */ 
/*    */     public CheckBoxCellEditor(int align, boolean enabled) {
/* 72 */       super();
/*    */ 
/* 74 */       Insets margin = new Insets(0, 0, 0, 0);
/* 75 */       ((JCheckBox)this.editorComponent).setHorizontalAlignment(align);
/* 76 */       ((JCheckBox)this.editorComponent).setMargin(margin);
/* 77 */       ((JCheckBox)this.editorComponent).setEnabled(enabled);
/*    */ 
/* 79 */       this.rendererComponent.setBackground(Color.WHITE);
/* 80 */       this.rendererComponent.setHorizontalAlignment(align);
/* 81 */       this.rendererComponent.setMargin(margin);
/*    */     }
/*    */ 
/*    */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */     {
/* 86 */       this.rendererComponent.setFont(table.getFont());
/*    */ 
/* 88 */       if ((value instanceof Boolean))
/* 89 */         this.rendererComponent.setSelected(((Boolean)value).booleanValue());
/*    */       else {
/* 91 */         this.rendererComponent.setSelected(false);
/*    */       }
/*    */ 
/* 94 */       return this.rendererComponent;
/*    */     }
/*    */ 
/*    */     public Dimension getPreferredSize() {
/* 98 */       return this.editorComponent.getPreferredSize();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.InstrumentSelectionTable
 * JD-Core Version:    0.6.0
 */