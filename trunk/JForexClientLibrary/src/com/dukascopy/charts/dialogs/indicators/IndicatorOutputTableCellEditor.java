/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.util.EventObject;
/*     */ import java.util.Vector;
/*     */ import javax.swing.AbstractCellEditor;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ abstract class IndicatorOutputTableCellEditor extends AbstractCellEditor
/*     */   implements TableCellEditor, TableCellRenderer
/*     */ {
/*  24 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorOutputTableCellEditor.class);
/*  25 */   private static final JLabel STUB = new JLabel("");
/*     */   private final IndicatorOutputsTableModel tableModel;
/*     */   private final int column;
/*  29 */   private final Vector<Component> editors = new Vector();
/*     */ 
/*  31 */   private int editingRow = -1;
/*  32 */   private Object currentValue = null;
/*     */ 
/*     */   public IndicatorOutputTableCellEditor(IndicatorOutputsTableModel tableModel, int column) {
/*  35 */     this.tableModel = tableModel;
/*  36 */     this.column = column;
/*     */   }
/*     */ 
/*     */   public void build() {
/*  40 */     if (LOGGER.isDebugEnabled()) {
/*  41 */       LOGGER.debug("Building");
/*     */     }
/*     */ 
/*  44 */     this.editingRow = -1;
/*  45 */     this.currentValue = null;
/*     */ 
/*  47 */     this.editors.clear();
/*  48 */     this.editors.setSize(this.tableModel.getRowCount());
/*     */ 
/*  50 */     stopCellEditing();
/*     */ 
/*  52 */     for (int row = 0; row < this.tableModel.getRowCount(); row++)
/*  53 */       if (this.tableModel.isCellEditable(row, this.column)) {
/*  54 */         this.editingRow = row;
/*  55 */         this.editors.set(row, createEditor(this.tableModel.getValueAt(row, this.column)));
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void setCurrentValue(Object value)
/*     */   {
/*  61 */     if (LOGGER.isDebugEnabled()) {
/*  62 */       LOGGER.debug("Set current value : " + value);
/*     */     }
/*  64 */     this.currentValue = value;
/*  65 */     this.tableModel.setValueAt(value, this.editingRow, this.column);
/*     */   }
/*     */ 
/*     */   protected abstract Component createEditor(Object paramObject);
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  77 */     return getEditor(row);
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/*  87 */     if (LOGGER.isDebugEnabled()) {
/*  88 */       LOGGER.debug("Get table cell editor component [" + row + " : " + column + "]");
/*     */     }
/*     */ 
/*  91 */     this.editingRow = row;
/*     */ 
/*  93 */     Component editor = getEditor(row);
/*     */ 
/*  95 */     if ((editor instanceof JComboBox)) {
/*  96 */       LOGGER.debug("ComboBox editor : " + editor);
/*  97 */       this.currentValue = ((JComboBox)editor).getSelectedItem();
/*     */     }
/*  99 */     else if ((editor instanceof JSpinner)) {
/* 100 */       LOGGER.debug("Spinner editor : " + editor);
/* 101 */       this.currentValue = ((JSpinner)editor).getValue();
/*     */     }
/*     */ 
/* 104 */     return editor;
/*     */   }
/*     */ 
/*     */   private Component getEditor(int row) {
/* 108 */     Component editor = (Component)this.editors.get(row);
/* 109 */     return editor == null ? STUB : editor;
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/* 114 */     if (LOGGER.isDebugEnabled()) {
/* 115 */       LOGGER.debug("Cell editor value : " + this.currentValue);
/*     */     }
/*     */ 
/* 118 */     return this.currentValue;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject evt)
/*     */   {
/* 123 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorOutputTableCellEditor
 * JD-Core Version:    0.6.0
 */