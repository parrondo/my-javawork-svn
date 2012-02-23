/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting.StrategiesTableHeader;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import java.util.List;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ public class StrategiesTable extends JLocalizableTable
/*     */ {
/*     */   public StrategiesTable(StrategiesTableModel tableModel, StrategiesTableColumnModel columnModel)
/*     */   {
/*  34 */     setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  35 */     setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  36 */     setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/*     */ 
/*  38 */     setAutoCreateColumnsFromModel(false);
/*  39 */     setFocusable(false);
/*  40 */     setFillsViewportHeight(true);
/*  41 */     setSelectionMode(2);
/*     */ 
/*  43 */     setColumnModel(columnModel);
/*  44 */     setRowHeight(20);
/*     */ 
/*  46 */     tableModel.setColumnCount(columnModel.getColumnCount());
/*  47 */     tableModel.setRowCount(tableModel.getStrategies().size());
/*     */ 
/*  49 */     setModel(tableModel);
/*     */   }
/*     */ 
/*     */   public int addStrategy(StrategyNewBean strategy)
/*     */   {
/*  58 */     StrategiesTableModel model = (StrategiesTableModel)getModel();
/*     */ 
/*  60 */     Object[] rowData = new Object[this.columnModel.getColumnCount()];
/*  61 */     int rowCount = model.getRowCount();
/*     */ 
/*  63 */     model.getStrategies().add(rowCount, strategy);
/*  64 */     model.insertRow(rowCount, rowData);
/*  65 */     return rowCount;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  71 */     StrategiesTableModel tableModel = (StrategiesTableModel)getModel();
/*  72 */     StrategiesTableColumnModel columnModel = (StrategiesTableColumnModel)getColumnModel();
/*  73 */     List strategies = tableModel.getStrategies();
/*  74 */     TableColumn column = columnModel.getColumn(columnIndex);
/*  75 */     StrategyNewBean strategy = (StrategyNewBean)strategies.get(rowIndex);
/*     */ 
/*  77 */     Boolean isInProcess = Boolean.valueOf(!strategy.getStatus().equals(StrategyStatus.STOPPED));
/*     */ 
/*  79 */     TableCellRenderer cellRenderer = getCellRenderer(rowIndex, columnIndex);
/*     */ 
/*  81 */     if ((cellRenderer instanceof StringTableCellRenderer)) {
/*  82 */       ((StringTableCellRenderer)cellRenderer).setRunning(isInProcess.booleanValue());
/*     */     }
/*  84 */     if ((cellRenderer instanceof StatusTableCellRenderer)) {
/*  85 */       ((StatusTableCellRenderer)cellRenderer).setRunning(isInProcess.booleanValue());
/*     */     }
/*  87 */     if ((cellRenderer instanceof NameTableCellRenderer)) {
/*  88 */       ((NameTableCellRenderer)cellRenderer).setStrategy(strategy);
/*     */     }
/*     */ 
/*  91 */     if ("NAME_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/*  92 */       return strategy.getName();
/*     */     }
/*  94 */     if ("START_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/*  95 */       return strategy.getStartTime();
/*     */     }
/*  97 */     if ("TIME_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/*  98 */       return strategy.getDurationTime();
/*     */     }
/* 100 */     if ("END_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/* 101 */       return strategy.getEndTime();
/*     */     }
/* 103 */     if ("PRESET_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/* 104 */       if (isInProcess.booleanValue())
/* 105 */         return strategy.getRunningPresetName();
/* 106 */       if (strategy.getActivePreset() != null) {
/* 107 */         return strategy.getActivePreset();
/*     */       }
/* 109 */       return "--";
/*     */     }
/*     */ 
/* 112 */     if ("TYPE_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/* 113 */       return strategy.getType();
/*     */     }
/* 115 */     if ("STATUS_COLUMN_IDENTIFIER".equals(column.getIdentifier()))
/*     */     {
/* 117 */       return strategy.getStatus().toString();
/*     */     }
/*     */ 
/* 120 */     if ("COMMENTS_COLUMN_IDENTIFIER".equals(column.getIdentifier())) {
/* 121 */       return strategy.getComments() == null ? "" : strategy.getComments();
/*     */     }
/*     */ 
/* 124 */     return super.getValueAt(rowIndex, columnIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int row, int columnIndex)
/*     */   {
/* 131 */     StrategiesTableColumnModel columnModel = (StrategiesTableColumnModel)getColumnModel();
/* 132 */     TableColumn column = columnModel.getColumn(columnIndex);
/*     */ 
/* 134 */     return column.getIdentifier().equals("COMMENTS_COLUMN_IDENTIFIER");
/*     */   }
/*     */ 
/*     */   public TableCellEditor getCellEditor(int row, int columnIndex)
/*     */   {
/* 143 */     int rowIndex = convertRowIndexToView(row);
/*     */ 
/* 145 */     StrategiesTableColumnModel columnModel = (StrategiesTableColumnModel)getColumnModel();
/* 146 */     TableColumn column = columnModel.getColumn(columnIndex);
/* 147 */     if (column.getIdentifier().equals("COMMENTS_COLUMN_IDENTIFIER")) {
/* 148 */       return ((StrategiesTableModel)getModel()).getCommentsEditor(rowIndex);
/*     */     }
/*     */ 
/* 151 */     return super.getCellEditor(row, columnIndex);
/*     */   }
/*     */ 
/*     */   protected JTableHeader createDefaultTableHeader()
/*     */   {
/* 156 */     return new StrategiesTableHeader();
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 162 */     if (getColumnCount() > 0) {
/* 163 */       getColumn("NAME_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.name"));
/* 164 */       getColumn("START_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.start.date"));
/* 165 */       getColumn("END_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.end.date"));
/* 166 */       getColumn("TIME_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.duration"));
/* 167 */       getColumn("TYPE_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.type"));
/* 168 */       getColumn("PRESET_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.preset"));
/* 169 */       getColumn("STATUS_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.status"));
/* 170 */       getColumn("COMMENTS_COLUMN_IDENTIFIER").setHeaderValue(LocalizationManager.getText("strategies.column.comments"));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable
 * JD-Core Version:    0.6.0
 */