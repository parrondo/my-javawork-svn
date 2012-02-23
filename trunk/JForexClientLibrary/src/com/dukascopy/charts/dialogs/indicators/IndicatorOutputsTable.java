/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.JColorComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import javax.swing.DefaultCellEditor;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ class IndicatorOutputsTable extends JTable
/*     */ {
/*     */   public IndicatorOutputsTable(IndicatorOutputsTableModel tableModel)
/*     */   {
/*  35 */     super(tableModel);
/*  36 */     this.tableHeader.setReorderingAllowed(false);
/*  37 */     setRowSelectionAllowed(false);
/*  38 */     setColumnSelectionAllowed(false);
/*  39 */     setCellSelectionEnabled(false);
/*  40 */     setFocusable(false);
/*  41 */     setShowGrid(false);
/*  42 */     setRowHeight(getRowHeight() + 5);
/*  43 */     setFillsViewportHeight(true);
/*     */ 
/*  45 */     ColorCellEditor colorEditor = new ColorCellEditor(tableModel, 2);
/*  46 */     setupColumn(2, colorEditor, 100);
/*     */ 
/*  48 */     ColorCellEditor colorEditor2 = new ColorCellEditor(tableModel, 3);
/*  49 */     setupColumn(3, colorEditor2, 100);
/*  50 */     TableColumn trendDownColumn = getColumnModel().getColumn(3);
/*  51 */     trendDownColumn.setPreferredWidth(100);
/*     */ 
/*  56 */     IndicatorOutputTableCellEditor drawingStyleEditor = new IndicatorOutputTableCellEditor(tableModel, 4)
/*     */     {
/*     */       protected synchronized Component createEditor(Object value)
/*     */       {
/*  60 */         JComboBox styleEditor = EditIndicatorHelper.createDrawingStyleEditor(value);
/*  61 */         if (styleEditor != null) {
/*  62 */           styleEditor.addItemListener(new ItemListener(styleEditor)
/*     */           {
/*     */             public void itemStateChanged(ItemEvent e) {
/*  65 */               IndicatorOutputsTable.1.this.setCurrentValue(this.val$styleEditor.getSelectedItem());
/*     */             }
/*     */           });
/*     */         }
/*  70 */         return styleEditor;
/*     */       }
/*     */     };
/*  73 */     setupColumn(4, drawingStyleEditor, 75);
/*     */ 
/*  77 */     IndicatorOutputTableCellEditor lineWidthEditor = new IndicatorOutputTableCellEditor(tableModel, 5)
/*     */     {
/*     */       protected synchronized Component createEditor(Object value)
/*     */       {
/*  81 */         JSpinner widthEditor = EditIndicatorHelper.createWidthEditor(value);
/*  82 */         if (widthEditor != null)
/*  83 */           widthEditor.addChangeListener(new ChangeListener(widthEditor)
/*     */           {
/*     */             public void stateChanged(ChangeEvent e) {
/*  86 */               IndicatorOutputsTable.2.this.setCurrentValue(this.val$widthEditor.getValue());
/*     */             }
/*     */           });
/*  90 */         return widthEditor;
/*     */       }
/*     */     };
/*  93 */     setupColumn(5, lineWidthEditor, 50);
/*     */ 
/*  95 */     IndicatorOutputTableCellEditor transparencyEditor = new IndicatorOutputTableCellEditor(tableModel, 6)
/*     */     {
/*     */       protected synchronized Component createEditor(Object value)
/*     */       {
/*  99 */         JComboBox alphaEditor = EditIndicatorHelper.createTransparencyEditor(value);
/* 100 */         if (alphaEditor != null)
/* 101 */           alphaEditor.addItemListener(new ItemListener(alphaEditor)
/*     */           {
/*     */             public void itemStateChanged(ItemEvent e) {
/* 104 */               IndicatorOutputsTable.3.this.setCurrentValue(this.val$alphaEditor.getSelectedItem());
/*     */             }
/*     */           });
/* 108 */         return alphaEditor;
/*     */       }
/*     */     };
/* 111 */     setupColumn(6, transparencyEditor, 100);
/* 112 */     TableColumn transparencyColumn = getColumnModel().getColumn(6);
/* 113 */     transparencyColumn.setMinWidth(90);
/*     */ 
/* 115 */     IndicatorOutputTableCellEditor outputShiftEditor = new IndicatorOutputTableCellEditor(tableModel, 7)
/*     */     {
/*     */       protected synchronized Component createEditor(Object value)
/*     */       {
/* 119 */         JSpinner shiftEditor = EditIndicatorHelper.createShiftEditor(value);
/* 120 */         if (shiftEditor != null)
/* 121 */           shiftEditor.addChangeListener(new ChangeListener(shiftEditor)
/*     */           {
/*     */             public void stateChanged(ChangeEvent e) {
/* 124 */               IndicatorOutputsTable.4.this.setCurrentValue(this.val$shiftEditor.getValue());
/*     */             }
/*     */           });
/* 128 */         return shiftEditor;
/*     */       }
/*     */     };
/* 132 */     setupColumn(7, outputShiftEditor, 50);
/*     */ 
/* 137 */     CheckBoxCellEditor editor = new CheckBoxCellEditor(0);
/* 138 */     editor.setToolTipText(LocalizationManager.getText("tooltip.show.value.on.chart"));
/* 139 */     TableColumn column = getColumnModel().getColumn(8);
/* 140 */     column.setCellEditor(editor);
/* 141 */     column.setCellRenderer(editor);
/* 142 */     column.setMaxWidth(editor.getPreferredSize().width + 35);
/*     */ 
/* 144 */     CheckBoxCellEditor showOutputEditor = new CheckBoxCellEditor(0);
/* 145 */     showOutputEditor.setToolTipText(LocalizationManager.getText("tooltip.show.output"));
/* 146 */     TableColumn showOutputColumn = getColumnModel().getColumn(0);
/* 147 */     showOutputColumn.setCellEditor(showOutputEditor);
/* 148 */     showOutputColumn.setCellRenderer(showOutputEditor);
/* 149 */     showOutputColumn.setMaxWidth(showOutputEditor.getPreferredSize().width + 35);
/*     */ 
/* 151 */     tableModel.addTableModelListener(new TableModelListener(colorEditor, colorEditor2, drawingStyleEditor, lineWidthEditor, outputShiftEditor, transparencyEditor)
/*     */     {
/*     */       public void tableChanged(TableModelEvent e) {
/* 154 */         this.val$colorEditor.build();
/* 155 */         this.val$colorEditor2.build();
/* 156 */         this.val$drawingStyleEditor.build();
/* 157 */         this.val$lineWidthEditor.build();
/* 158 */         this.val$outputShiftEditor.build();
/* 159 */         this.val$transparencyEditor.build();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void setIndicator(IndicatorWrapper indicatorWrapper) {
/* 165 */     ((IndicatorOutputsTableModel)this.dataModel).setIndicator(indicatorWrapper);
/* 166 */     ((IndicatorOutputsTableModel)this.dataModel).setIgnoreChanges(false);
/*     */   }
/*     */ 
/*     */   private void setupColumn(int columnIndex, IndicatorOutputTableCellEditor indicatorOutputTableCellEditor, int maxWidth) {
/* 170 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 171 */     if (indicatorOutputTableCellEditor != null) {
/* 172 */       column.setCellEditor(indicatorOutputTableCellEditor);
/* 173 */       column.setCellRenderer(indicatorOutputTableCellEditor);
/*     */     }
/*     */ 
/* 176 */     if (maxWidth > 0)
/* 177 */       column.setMaxWidth(maxWidth);
/*     */   }
/*     */ 
/*     */   private static class ColorCellEditor extends IndicatorOutputTableCellEditor
/*     */   {
/* 248 */     private int colorIndex = 0;
/*     */ 
/*     */     public ColorCellEditor(IndicatorOutputsTableModel tableModel, int column)
/*     */     {
/* 245 */       super(column);
/*     */     }
/*     */ 
/*     */     protected synchronized Component createEditor(Object value)
/*     */     {
/* 252 */       return new JColorComboBox(value)
/*     */       {
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CheckBoxCellEditor extends DefaultCellEditor
/*     */     implements TableCellRenderer, TableCellEditor
/*     */   {
/* 188 */     private JCheckBox rendererComponent = new JCheckBox();
/*     */ 
/*     */     public CheckBoxCellEditor(int align) {
/* 191 */       super();
/* 192 */       Insets margin = new Insets(0, 0, 0, 0);
/* 193 */       ((JCheckBox)this.editorComponent).setHorizontalAlignment(align);
/* 194 */       ((JCheckBox)this.editorComponent).setMargin(margin);
/*     */ 
/* 196 */       this.rendererComponent.setHorizontalAlignment(align);
/* 197 */       this.rendererComponent.setMargin(margin);
/*     */     }
/*     */ 
/*     */     public void setToolTipText(String text) {
/* 201 */       this.editorComponent.setToolTipText(text);
/* 202 */       this.rendererComponent.setToolTipText(text);
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize() {
/* 206 */       return this.editorComponent.getPreferredSize();
/*     */     }
/*     */ 
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */     {
/* 211 */       this.rendererComponent.setFont(table.getFont());
/* 212 */       if (isSelected)
/* 213 */         this.rendererComponent.setBackground(table.getSelectionBackground());
/*     */       else {
/* 215 */         this.rendererComponent.setBackground(table.getBackground());
/*     */       }
/*     */ 
/* 218 */       if (table.isCellEditable(row, column))
/* 219 */         this.rendererComponent.setEnabled(true);
/*     */       else {
/* 221 */         this.rendererComponent.setEnabled(false);
/*     */       }
/*     */ 
/* 224 */       if ((value instanceof Boolean))
/* 225 */         this.rendererComponent.setSelected(((Boolean)value).booleanValue());
/*     */       else {
/* 227 */         this.rendererComponent.setSelected(false);
/*     */       }
/* 229 */       return this.rendererComponent;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorOutputsTable
 * JD-Core Version:    0.6.0
 */