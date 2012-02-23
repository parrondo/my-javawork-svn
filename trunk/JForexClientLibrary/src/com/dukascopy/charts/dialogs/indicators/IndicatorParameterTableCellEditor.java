/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.indicators.BooleanOptInputDescription;
/*     */ import com.dukascopy.api.indicators.DoubleListDescription;
/*     */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.IntegerListDescription;
/*     */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*     */ import com.dukascopy.api.indicators.OptInputDescription;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import com.dukascopy.charts.dialogs.indicators.component.BooleanOptParameterEditor;
/*     */ import com.dukascopy.charts.dialogs.indicators.component.spinner.DoubleOptParameterSpinner;
/*     */ import com.dukascopy.charts.dialogs.indicators.component.spinner.IntegerOptParameterSpinner;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.EventObject;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.swing.AbstractCellEditor;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IndicatorParameterTableCellEditor extends AbstractCellEditor
/*     */   implements TableCellEditor, TableCellRenderer, PropertyChangeListener
/*     */ {
/*  47 */   private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorParameterTableCellEditor.class);
/*  48 */   private static final JLabel STUB = new JLabel("");
/*  49 */   private static final IIndicators.AppliedPrice[] APPLIED_PRICES = { IIndicators.AppliedPrice.CLOSE, IIndicators.AppliedPrice.OPEN, IIndicators.AppliedPrice.HIGH, IIndicators.AppliedPrice.LOW, IIndicators.AppliedPrice.MEDIAN_PRICE, IIndicators.AppliedPrice.TYPICAL_PRICE, IIndicators.AppliedPrice.WEIGHTED_CLOSE };
/*     */   private final IndicatorParametersTableModel tableModel;
/*     */   private final boolean isTicks;
/*  61 */   private final Vector<Component> editors = new Vector();
/*     */ 
/*  63 */   private int editingRow = -1;
/*  64 */   private Object currentValue = null;
/*     */ 
/*     */   public IndicatorParameterTableCellEditor(IndicatorParametersTableModel tableModel, boolean isTicks)
/*     */   {
/*  70 */     this.tableModel = tableModel;
/*  71 */     this.isTicks = isTicks;
/*     */   }
/*     */ 
/*     */   public void setCurrentValue(Object value) {
/*  75 */     if (LOGGER.isDebugEnabled()) {
/*  76 */       LOGGER.debug("Set current value [" + value + "] : " + value.getClass());
/*     */     }
/*     */ 
/*  79 */     this.currentValue = value;
/*  80 */     this.tableModel.setValueAt(value, this.editingRow, 1);
/*     */   }
/*     */ 
/*     */   public void build() {
/*  84 */     if (LOGGER.isDebugEnabled()) {
/*  85 */       LOGGER.debug("Building");
/*     */     }
/*  87 */     this.editingRow = -1;
/*  88 */     this.currentValue = null;
/*  89 */     this.editors.clear();
/*  90 */     this.editors.setSize(this.tableModel.getRowCount());
/*     */ 
/*  92 */     for (int row = 0; row < this.tableModel.getRowCount(); row++) {
/*  93 */       Component editor = null;
/*     */ 
/*  95 */       if (this.tableModel.isCellEditable(row, 1)) {
/*  96 */         IndicatorParameter indicatorParameter = (IndicatorParameter)this.tableModel.getValueAt(row, 1);
/*     */ 
/*  99 */         if ((indicatorParameter.info instanceof InputParameterInfo)) {
/* 100 */           switch (1.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[((InputParameterInfo)indicatorParameter.info).getType().ordinal()]) {
/*     */           case 1:
/*     */           case 2:
/* 103 */             if (this.isTicks)
/* 104 */               editor = new InputParameterComboBox(indicatorParameter.value, OfferSide.values());
/*     */             else {
/* 106 */               editor = new InputParameterComboBox(indicatorParameter.value, APPLIED_PRICES);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/* 111 */         else if ((indicatorParameter.info instanceof OptInputParameterInfo)) {
/* 112 */           OptInputDescription description = ((OptInputParameterInfo)indicatorParameter.info).getDescription();
/*     */ 
/* 115 */           if ((description instanceof IntegerListDescription)) {
/* 116 */             editor = new OptParameterComboBox(indicatorParameter.value, (IntegerListDescription)description);
/*     */           }
/* 118 */           else if ((description instanceof IntegerRangeDescription)) {
/* 119 */             editor = new IntegerOptParameterSpinner((Integer)indicatorParameter.value, (IntegerRangeDescription)description, this);
/*     */           }
/* 122 */           else if ((description instanceof DoubleListDescription)) {
/* 123 */             editor = new OptParameterComboBox(indicatorParameter.value, (DoubleListDescription)description);
/*     */           }
/* 125 */           else if ((description instanceof DoubleRangeDescription)) {
/* 126 */             editor = new DoubleOptParameterSpinner((Double)indicatorParameter.value, (DoubleRangeDescription)description, this);
/*     */           }
/* 128 */           else if ((description instanceof BooleanOptInputDescription)) {
/* 129 */             editor = new BooleanOptParameterEditor((Boolean)indicatorParameter.value, (BooleanOptInputDescription)description, this);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 134 */       this.editors.set(row, editor);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/* 145 */     return getEditor(row);
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/* 158 */     if ((this.editingRow > -1) && (this.editingRow != row)) {
/* 159 */       Component currentEditor = getEditor(this.editingRow);
/* 160 */       if ((currentEditor instanceof JSpinner)) {
/*     */         try {
/* 162 */           ((JSpinner)currentEditor).commitEdit();
/*     */         }
/*     */         catch (ParseException ex)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 169 */     if (LOGGER.isDebugEnabled()) {
/* 170 */       LOGGER.debug("Get table cell editor [" + row + ":" + column + "]");
/*     */     }
/* 172 */     this.editingRow = row;
/*     */ 
/* 174 */     Component editor = getEditor(row);
/*     */ 
/* 176 */     if ((editor instanceof JComboBox)) {
/* 177 */       this.currentValue = ((IndicatorParameter)value).value;
/*     */     }
/* 179 */     else if ((editor instanceof JSpinner)) {
/* 180 */       this.currentValue = ((JSpinner)editor).getValue();
/*     */     }
/* 182 */     else if ((editor instanceof JCheckBox)) {
/* 183 */       this.currentValue = new Boolean(((JCheckBox)editor).isSelected());
/*     */     }
/*     */ 
/* 186 */     if (LOGGER.isDebugEnabled()) {
/* 187 */       LOGGER.debug("Row # " + row + " value : [" + this.currentValue + "]");
/*     */     }
/*     */ 
/* 190 */     stopCellEditing();
/* 191 */     return editor;
/*     */   }
/*     */ 
/*     */   private Component getEditor(int row) {
/* 195 */     Component editor = (Component)this.editors.get(row);
/* 196 */     return editor == null ? STUB : editor;
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/* 201 */     LOGGER.debug("Current editor value : " + this.currentValue);
/* 202 */     return this.currentValue;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject evt)
/*     */   {
/* 207 */     LOGGER.debug(evt.toString());
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent evt)
/*     */   {
/* 266 */     if ("value".equals(evt.getPropertyName()))
/* 267 */       setCurrentValue(evt.getNewValue());
/*     */   }
/*     */ 
/*     */   private class OptParameterComboBox extends JComboBox
/*     */   {
/*     */     final List<?> values;
/*     */ 
/*     */     public OptParameterComboBox(Object parameterValue, IntegerListDescription integerListDescription)
/*     */     {
/* 232 */       super();
/* 233 */       this.values = new ArrayList(IndicatorParameterTableCellEditor.this, integerListDescription)
/*     */       {
/*     */       };
/* 239 */       select(parameterValue);
/*     */     }
/*     */ 
/*     */     public OptParameterComboBox(Object parameterValue, DoubleListDescription doubleListDescription) {
/* 243 */       super();
/* 244 */       this.values = new ArrayList(IndicatorParameterTableCellEditor.this, doubleListDescription)
/*     */       {
/*     */       };
/* 250 */       select(parameterValue);
/*     */     }
/*     */ 
/*     */     private void select(Object parameterValue) {
/* 254 */       setSelectedIndex(this.values.indexOf(parameterValue));
/* 255 */       addItemListener(new ItemListener()
/*     */       {
/*     */         public void itemStateChanged(ItemEvent e) {
/* 258 */           IndicatorParameterTableCellEditor.this.setCurrentValue(IndicatorParameterTableCellEditor.OptParameterComboBox.this.values.get(IndicatorParameterTableCellEditor.OptParameterComboBox.this.getSelectedIndex()));
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InputParameterComboBox extends JComboBox
/*     */   {
/*     */     public InputParameterComboBox(Object value, Object[] values)
/*     */     {
/* 216 */       super();
/* 217 */       setSelectedIndex(Arrays.binarySearch(values, value));
/* 218 */       addItemListener(new ItemListener(IndicatorParameterTableCellEditor.this, values)
/*     */       {
/*     */         public void itemStateChanged(ItemEvent e) {
/* 221 */           IndicatorParameterTableCellEditor.this.setCurrentValue(this.val$values[IndicatorParameterTableCellEditor.InputParameterComboBox.this.getSelectedIndex()]);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorParameterTableCellEditor
 * JD-Core Version:    0.6.0
 */