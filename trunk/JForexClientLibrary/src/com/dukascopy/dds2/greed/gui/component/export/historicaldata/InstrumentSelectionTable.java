/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.DataField;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class InstrumentSelectionTable extends JTable
/*     */   implements Localizable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  30 */   private ExportProcessControl exportProcessControl = null;
/*     */ 
/*  32 */   private int widthOfInstrumentSelectionColumn = 40;
/*  33 */   private int widthOfInstrumentNameColumn = 855;
/*     */ 
/*  38 */   private int widthOfExportFormatColumn = 90;
/*     */ 
/*     */   public InstrumentSelectionTable(InstrumentSelectionTableModel instrumentSelectionTableModel, ExportProcessControl exportProcessControl) {
/*  41 */     super(instrumentSelectionTableModel);
/*     */ 
/*  43 */     this.exportProcessControl = exportProcessControl;
/*     */ 
/*  48 */     this.tableHeader.setReorderingAllowed(false);
/*  49 */     setRowSelectionAllowed(false);
/*     */ 
/*  51 */     setCellSelectionEnabled(false);
/*  52 */     setFocusable(false);
/*     */ 
/*  55 */     int rowHeightCorrection = 5;
/*  56 */     if (PlatformSpecific.LINUX) {
/*  57 */       rowHeightCorrection = 8;
/*     */     }
/*  59 */     setRowHeight(getRowHeight() + rowHeightCorrection);
/*     */ 
/*  61 */     setRowHeight(getRowHeight());
/*  62 */     setFillsViewportHeight(true);
/*     */ 
/*  65 */     setupCheckBoxColumn(0, 0);
/*     */ 
/*  68 */     setupInstrumentNameColumn(1);
/*     */ 
/*  92 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   private void setListeners(InstrumentSelectionTableModel instrumentSelectionTableModel) {
/*  96 */     List instruments = instrumentSelectionTableModel.getTradableInstruments();
/*     */ 
/*  99 */     getModel().addTableModelListener(new TableModelListener(instruments, instrumentSelectionTableModel)
/*     */     {
/*     */       public void tableChanged(TableModelEvent e) {
/* 102 */         Instrument instrument = null;
/* 103 */         int rowIndex = e.getFirstRow();
/* 104 */         if ((this.val$instruments != null) && (this.val$instruments.size() > 0))
/*     */           try {
/* 106 */             instrument = (Instrument)this.val$instruments.get(rowIndex);
/*     */           }
/*     */           catch (IndexOutOfBoundsException e2)
/*     */           {
/*     */           }
/* 111 */         if ((!this.val$instrumentSelectionTableModel.isInstrumentSelected(rowIndex)) && (instrument != null)) {
/* 112 */           InstrumentSelectionTable.this.exportProcessControl.onValidated(DataField.INSTRUMENTS_TABLE_CEll, false, instrument, 5, "");
/*     */ 
/* 119 */           InstrumentSelectionTable.this.exportProcessControl.onValidated(DataField.INSTRUMENTS_TABLE_CEll, false, instrument, 6, "");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void setupDateColumn(int columnIndex, InstrumentSelectionTableModel instrumentSelectionTableModel)
/*     */   {
/* 132 */     DateChooserTableCellEditor dateOutputTableCellEditor = new DateChooserTableCellEditor(instrumentSelectionTableModel, this.exportProcessControl, columnIndex);
/* 133 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 134 */     column.setCellEditor(dateOutputTableCellEditor);
/* 135 */     column.setCellRenderer(dateOutputTableCellEditor);
/*     */ 
/* 139 */     getModel().addTableModelListener(new TableModelListener(dateOutputTableCellEditor)
/*     */     {
/*     */       public void tableChanged(TableModelEvent e) {
/* 142 */         this.val$dateOutputTableCellEditor.build();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void setupColumn(int columnIndex, InstrumentSelectionTableModel instrumentSelectionTableModel) {
/* 149 */     MultipurposeTableCellEditor multipurposeTableCellEditor = new MultipurposeTableCellEditor(instrumentSelectionTableModel, columnIndex, this.exportProcessControl);
/* 150 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 151 */     column.setCellEditor(multipurposeTableCellEditor);
/* 152 */     column.setCellRenderer(multipurposeTableCellEditor);
/*     */ 
/* 154 */     switch (columnIndex) {
/*     */     case 2:
/* 156 */       column.setMinWidth(150);
/* 157 */       break;
/*     */     case 3:
/* 162 */       int columnWidth = getPeriodColumnWidth();
/* 163 */       column.setPreferredWidth(columnWidth);
/* 164 */       column.setMinWidth(columnWidth);
/* 165 */       column.setMaxWidth(columnWidth);
/*     */ 
/* 167 */       break;
/*     */     case 4:
/* 169 */       break;
/*     */     case 7:
/* 172 */       column.setPreferredWidth(this.widthOfExportFormatColumn);
/* 173 */       column.setMinWidth(this.widthOfExportFormatColumn);
/* 174 */       column.setMaxWidth(this.widthOfExportFormatColumn);
/* 175 */       break;
/*     */     case 5:
/*     */     case 6:
/*     */     default:
/* 176 */       throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */     }
/*     */ 
/* 179 */     getModel().addTableModelListener(new TableModelListener(multipurposeTableCellEditor)
/*     */     {
/*     */       public void tableChanged(TableModelEvent e) {
/* 182 */         this.val$multipurposeTableCellEditor.build();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 194 */     for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
/* 195 */       getTableHeader().getColumnModel().getColumn(i).setHeaderValue(getModel().getColumnName(i));
/*     */     }
/*     */ 
/* 198 */     getTableHeader().revalidate();
/* 199 */     getTableHeader().repaint();
/*     */   }
/*     */ 
/*     */   private void changeColumnsWidth() {
/* 203 */     TableColumn column = getColumnModel().getColumn(3);
/*     */ 
/* 205 */     int currentMinWidth = column.getMinWidth();
/* 206 */     int columnWidth = getPeriodColumnWidth();
/*     */ 
/* 208 */     if (currentMinWidth != columnWidth) {
/* 209 */       column.setMinWidth(columnWidth);
/* 210 */       column.setPreferredWidth(columnWidth);
/* 211 */       column.setMaxWidth(columnWidth);
/*     */ 
/* 213 */       revalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getPeriodColumnWidth() {
/* 218 */     int columnWidth = 0;
/*     */ 
/* 220 */     columnWidth += JSpinnerHint.getSpinnerHintWidth(LocalizationManager.getText("label.caption.box.size.in.pips") + JSpinnerHint.getTailString());
/* 221 */     columnWidth += JSpinnerHint.getSpinnerHintWidth(LocalizationManager.getText("label.caption.reversal.amount") + JSpinnerHint.getTailString());
/*     */ 
/* 223 */     return columnWidth;
/*     */   }
/*     */ 
/*     */   private void setupCheckBoxColumn(int columnIndex, int orientation) {
/* 227 */     CheckBoxCellEditor selectInstrumentCellEditor = new CheckBoxCellEditor(orientation);
/* 228 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 229 */     column.setCellEditor(selectInstrumentCellEditor);
/* 230 */     column.setCellRenderer(selectInstrumentCellEditor);
/*     */ 
/* 232 */     column.setMaxWidth(this.widthOfInstrumentSelectionColumn);
/* 233 */     column.setPreferredWidth(this.widthOfInstrumentSelectionColumn);
/* 234 */     column.setMinWidth(this.widthOfInstrumentSelectionColumn);
/*     */ 
/* 236 */     column.setHeaderRenderer(new CheckBoxHeader(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 241 */         boolean selected = ((JCheckBox)e.getSource()).isSelected();
/*     */ 
/* 243 */         InstrumentSelectionTableModel tableModel = (InstrumentSelectionTableModel)InstrumentSelectionTable.this.getModel();
/* 244 */         tableModel.setSelectionForAll(selected);
/* 245 */         tableModel.fireTableDataChanged();
/*     */       }
/*     */     }
/*     */     , this));
/*     */   }
/*     */ 
/*     */   private void setupInstrumentNameColumn(int columnIndex)
/*     */   {
/* 253 */     TableColumn column = getColumnModel().getColumn(columnIndex);
/* 254 */     column.setPreferredWidth(this.widthOfInstrumentNameColumn);
/* 255 */     column.setMinWidth(this.widthOfInstrumentNameColumn);
/* 256 */     column.setMaxWidth(this.widthOfInstrumentNameColumn);
/*     */   }
/*     */ 
/*     */   public static CellPanel createCellPanel(int columnIndex, int rowIndex, InstrumentSelectionTableModel tableModel, ExportProcessControl exportProcessControl, Component component)
/*     */   {
/* 276 */     CellPanel cellPanel = new CellPanel(columnIndex, rowIndex, exportProcessControl, tableModel);
/* 277 */     cellPanel.setBackground(Color.WHITE);
/* 278 */     cellPanel.setLayout(new GridBagLayout());
/* 279 */     GridBagConstraints gbc = new GridBagConstraints();
/* 280 */     gbc.fill = 1;
/* 281 */     gbc.anchor = 21;
/* 282 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 5, 1, 1, 1, gbc, cellPanel, component);
/*     */ 
/* 284 */     return cellPanel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.InstrumentSelectionTable
 * JD-Core Version:    0.6.0
 */