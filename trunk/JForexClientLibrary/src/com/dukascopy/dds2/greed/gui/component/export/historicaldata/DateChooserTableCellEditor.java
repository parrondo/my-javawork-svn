/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.toedter.calendar.DateUtil;
/*     */ import com.toedter.calendar.IDateEditor;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JTextFieldDateEditor;
/*     */ import java.awt.Component;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.EventObject;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractCellEditor;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.CaretEvent;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ 
/*     */ public class DateChooserTableCellEditor extends AbstractCellEditor
/*     */   implements TableCellEditor, TableCellRenderer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  27 */   private static final JLabel STUB = new JLabel("");
/*  28 */   int columnIndex = 0;
/*  29 */   private Object currentValue = null;
/*  30 */   private InstrumentSelectionTableModel tableModel = null;
/*     */   private ExportProcessControl exportProcessControl;
/*  32 */   private final List<Component> editors = new ArrayList();
/*     */ 
/*     */   public DateChooserTableCellEditor(InstrumentSelectionTableModel tableModel, ExportProcessControl exportProcessControl, int columnIndex) {
/*  35 */     this.tableModel = tableModel;
/*  36 */     this.exportProcessControl = exportProcessControl;
/*  37 */     this.columnIndex = columnIndex;
/*  38 */     init();
/*  39 */     build();
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  44 */     return this.currentValue;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  49 */     return getEditor(row);
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/*  54 */     Component editor = getEditor(row);
/*  55 */     this.currentValue = value;
/*  56 */     return editor;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject evt)
/*     */   {
/*  61 */     return true;
/*     */   }
/*     */ 
/*     */   public void build() {
/*  65 */     for (int row = 0; row < this.tableModel.getRowCount(); row++) {
/*  66 */       if ((!this.tableModel.isCellEditable(row, this.columnIndex)) || 
/*  67 */         (this.editors.get(row) != null)) continue;
/*  68 */       this.editors.set(row, createEditor(row));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  75 */     this.editors.clear();
/*  76 */     for (int row = 0; row < this.tableModel.getRowCount(); row++)
/*  77 */       this.editors.add(null);
/*     */   }
/*     */ 
/*     */   private Component getEditor(int row)
/*     */   {
/*  82 */     if (this.tableModel.isCellEditable(row, this.columnIndex)) {
/*  83 */       Component editor = (Component)this.editors.get(row);
/*  84 */       if ((editor instanceof CellPanel)) {
/*  85 */         Component component = ((CellPanel)editor).getComponent(0);
/*  86 */         if ((component instanceof JDateChooser)) {
/*  87 */           Object value = this.tableModel.getValueAt(row, this.columnIndex);
/*  88 */           if ((value == null) || (!(value instanceof Long)))
/*     */           {
/*  91 */             ((JDateChooser)component).setDate(null);
/*     */           }
/*     */         }
/*     */       }
/*  95 */       return editor == null ? STUB : editor;
/*     */     }
/*  97 */     return STUB;
/*     */   }
/*     */ 
/*     */   private Component createEditor(int rowIndex)
/*     */   {
/* 102 */     String dateFormat = "yyyy.MM.dd";
/*     */ 
/* 104 */     Object value = this.tableModel.getValueAt(rowIndex, this.columnIndex);
/* 105 */     Long from = (Long)value;
/* 106 */     JDateChooser dateChooser = new JDateChooser(null, null, "yyyy.MM.dd", new JExtendedTextFieldDateEditor("yyyy.MM.dd", "##/##/##", '_', rowIndex, this.columnIndex, this.tableModel));
/*     */ 
/* 120 */     IDateEditor dateEditor = dateChooser.getDateEditor();
/* 121 */     if ((dateEditor instanceof JTextFieldDateEditor)) {
/* 122 */       JTextFieldDateEditor textFieldDateEditor = (JTextFieldDateEditor)dateEditor;
/* 123 */       textFieldDateEditor.setBorder(BorderFactory.createEmptyBorder());
/*     */     }
/*     */ 
/* 126 */     if (value != null) {
/* 127 */       dateChooser.setDate(new Date(from.longValue()));
/*     */     }
/*     */ 
/* 130 */     dateChooser.addPropertyChangeListener("date", new PropertyChangeListener(rowIndex)
/*     */     {
/*     */       public void propertyChange(PropertyChangeEvent evt) {
/* 133 */         if (evt.getNewValue() != null) {
/* 134 */           Date newDate = (Date)evt.getNewValue();
/* 135 */           DateChooserTableCellEditor.access$002(DateChooserTableCellEditor.this, Long.valueOf(newDate.getTime()));
/*     */ 
/* 138 */           DateChooserTableCellEditor.this.tableModel.setValueAt(Long.valueOf(newDate.getTime()), this.val$rowIndex, DateChooserTableCellEditor.this.columnIndex);
/*     */         } else {
/* 140 */           DateChooserTableCellEditor.this.tableModel.setValueAt(null, this.val$rowIndex, DateChooserTableCellEditor.this.columnIndex);
/*     */         }
/*     */       }
/*     */     });
/* 145 */     CellPanel cellPanel = InstrumentSelectionTable.createCellPanel(this.columnIndex, rowIndex, this.tableModel, this.exportProcessControl, dateChooser);
/*     */ 
/* 152 */     dateChooser.addPropertyChangeListener("date", cellPanel);
/* 153 */     return cellPanel;
/*     */   }
/*     */ 
/*     */   private static class JExtendedTextFieldDateEditor extends JTextFieldDateEditor
/*     */   {
/* 158 */     private boolean dateValid = false;
/*     */     private final int columnIndex;
/*     */     private final int rowIndex;
/*     */     private final InstrumentSelectionTableModel tableModel;
/*     */ 
/*     */     public JExtendedTextFieldDateEditor(String datePattern, String maskPattern, char placeholder, int rowIndex, int columnIndex, InstrumentSelectionTableModel tableModel)
/*     */     {
/* 171 */       super(maskPattern, placeholder);
/* 172 */       this.rowIndex = rowIndex;
/* 173 */       this.columnIndex = columnIndex;
/* 174 */       this.tableModel = tableModel;
/*     */     }
/*     */ 
/*     */     public void caretUpdate(CaretEvent event)
/*     */     {
/* 179 */       super.caretUpdate(event);
/*     */       try
/*     */       {
/* 182 */         Date date = this.dateFormatter.parse(getText());
/* 183 */         if (this.dateUtil.checkDate(date)) {
/* 184 */           this.dateValid = true;
/* 185 */           updateDataInModel(Long.valueOf(date.getTime()));
/*     */         } else {
/* 187 */           this.dateValid = false;
/* 188 */           updateDataInModel(null);
/*     */         }
/*     */       } catch (Exception e) {
/* 191 */         this.dateValid = false;
/* 192 */         updateDataInModel(null);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void updateDataInModel(Object value) {
/* 197 */       this.tableModel.setValueAt(value, this.rowIndex, this.columnIndex);
/*     */     }
/*     */ 
/*     */     public boolean isDateValid()
/*     */     {
/* 202 */       return this.dateValid;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.DateChooserTableCellEditor
 * JD-Core Version:    0.6.0
 */