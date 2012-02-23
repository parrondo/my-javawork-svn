/*     */ package com.dukascopy.dds2.greed.gui.component.positions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellEditor;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeaderItemListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.PIP;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.PipCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.PriceRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.StopPriceRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.CommonWorkspaceHellper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTableModelListener;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.SystemColor;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PositionsTable extends JLocalizableTable
/*     */ {
/*  54 */   private static final Logger LOGGER = LoggerFactory.getLogger(PositionsTable.class);
/*     */   private PositionsTableModel model;
/*  59 */   public static final Color COLOR_LONG = new Color(0, 200, 0);
/*  60 */   public static final Color COLOR_SHORT = new Color(230, 0, 0);
/*     */ 
/*  62 */   private static Font boldFont = null;
/*  63 */   private static Font defaultFont = null;
/*     */ 
/*     */   public PositionsTable(TableModel dm)
/*     */   {
/*  67 */     super(dm);
/*     */ 
/*  69 */     this.model = ((PositionsTableModel)dm);
/*     */ 
/*  71 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*  72 */     setDefaultRenderer(Money.class, new MoneyCellRenderer());
/*  73 */     setDefaultRenderer(PIP.class, new PipCellRenderer());
/*     */ 
/*  75 */     getColumnModel().getColumn(0).setPreferredWidth(40);
/*  76 */     getColumnModel().getColumn(0).setMaxWidth(40);
/*  77 */     getColumnModel().getColumn(0).setMinWidth(40);
/*  78 */     getColumnModel().getColumn(0).setResizable(false);
/*  79 */     getColumnModel().getColumn(0).setCellRenderer(new CheckBoxCellRenderer());
/*  80 */     getColumnModel().getColumn(0).setCellEditor(new CheckBoxCellEditor());
/*  81 */     getColumnModel().getColumn(PositionsTableModel.COLUMN_ID).setPreferredWidth(50);
/*  82 */     getColumnModel().getColumn(PositionsTableModel.COLUMN_STOP_LOSS).setCellRenderer(new StopPriceRenderer());
/*  83 */     getColumnModel().getColumn(PositionsTableModel.COLUMN_TAKE_PROFIT).setCellRenderer(new StopPriceRenderer());
/*  84 */     getColumnModel().getColumn(PositionsTableModel.COLUMN_PRICE_OPEN).setCellRenderer(new PriceRenderer());
/*  85 */     getColumnModel().getColumn(PositionsTableModel.COLUMN_PRICE_CURRENT).setCellRenderer(new PriceRenderer());
/*  86 */     getSelectionModel().setSelectionMode(0);
/*     */ 
/*  88 */     getTableHeader().setReorderingAllowed(true);
/*     */ 
/*  90 */     getTableHeader().getColumnModel().getColumn(0).setHeaderValue("#");
/*  91 */     getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener(this), this));
/*     */ 
/*  93 */     translate();
/*     */ 
/*  95 */     setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  96 */     setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  97 */     setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/*     */ 
/*  99 */     getModel().addTableModelListener(new JLocalizableTableModelListener(1, CommonWorkspaceHellper.getPositionsLabelKey()));
/*     */   }
/*     */ 
/*     */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
/*     */   {
/* 104 */     Component cell = super.prepareRenderer(renderer, row, column);
/*     */ 
/* 106 */     boolean isSelected = row == getSelectedRow();
/*     */ 
/* 109 */     Position position = this.model.getPosition(getRowSorter().convertRowIndexToModel(row));
/*     */ 
/* 111 */     if (position != null) {
/* 112 */       int modelColumn = convertColumnIndexToModel(column);
/*     */ 
/* 114 */       if ((isSelected) && (modelColumn != PositionsTableModel.COLUMN_INSTRUMENT)) {
/* 115 */         resetToDefault(cell, isSelected);
/* 116 */         return cell;
/*     */       }
/* 118 */       if ((modelColumn != PositionsTableModel.COLUMN_PROFIT_LOSS) && (modelColumn != PositionsTableModel.COLUMN_PROFIT_LOSS_PIP)) {
/* 119 */         resetToDefault(cell, isSelected);
/*     */       }
/*     */ 
/* 123 */       if (PositionsTableModel.COLUMN_INSTRUMENT == modelColumn) {
/* 124 */         if (boldFont == null) {
/* 125 */           Font currentFont = cell.getFont();
/* 126 */           boldFont = new Font(currentFont.getFamily(), 1, currentFont.getSize());
/*     */         }
/* 128 */         cell.setFont(boldFont);
/* 129 */         setRightAlignment(cell);
/* 130 */         return cell;
/*     */       }
/*     */ 
/* 133 */       if (PositionsTableModel.COLUMN_DIRECTION == modelColumn) {
/* 134 */         if (position.getPositionSide() == PositionSide.LONG)
/* 135 */           cell.setForeground(COLOR_LONG);
/*     */         else {
/* 137 */           cell.setForeground(COLOR_SHORT);
/*     */         }
/* 139 */         setRightAlignment(cell);
/* 140 */         return cell;
/*     */       }
/*     */ 
/* 143 */       if (PositionsTableModel.COLUMN_PRICE_CURRENT == modelColumn) {
/* 144 */         setRightAlignment(cell);
/* 145 */         return cell;
/*     */       }
/*     */ 
/* 148 */       if (PositionsTableModel.COLUMN_AMOUNT == modelColumn) {
/* 149 */         setRightAlignment(cell);
/* 150 */         return cell;
/*     */       }
/*     */ 
/* 153 */       if (PositionsTableModel.COLUMN_PRICE_OPEN == modelColumn) {
/* 154 */         setRightAlignment(cell);
/* 155 */         return cell;
/*     */       }
/*     */ 
/* 160 */       if (PositionsTableModel.COLUMN_PROFIT_LOSS == modelColumn) {
/* 161 */         return cell;
/*     */       }
/*     */ 
/* 164 */       if (PositionsTableModel.COLUMN_PROFIT_LOSS_PIP == modelColumn) {
/* 165 */         return cell;
/*     */       }
/*     */ 
/* 168 */       if (PositionsTableModel.COLUMN_ID == modelColumn) {
/* 169 */         setRightAlignment(cell);
/* 170 */         cell.setForeground(SystemColor.textText);
/* 171 */         return cell;
/*     */       }
/*     */ 
/* 174 */       if ((1 == modelColumn) && (GreedContext.isStrategyAllowed())) {
/* 175 */         setRightAlignment(cell);
/* 176 */         return cell;
/*     */       }
/*     */ 
/* 180 */       cell.setForeground(SystemColor.textText);
/* 181 */       return cell;
/*     */     }
/*     */ 
/* 187 */     LOGGER.warn("Displaying null position: (" + row + "," + column + ")");
/* 188 */     return cell;
/*     */   }
/*     */ 
/*     */   private void resetToDefault(Component cell, boolean isSelected)
/*     */   {
/* 193 */     if (defaultFont == null) {
/* 194 */       defaultFont = new Font(cell.getFont().getFamily(), 0, cell.getFont().getSize());
/*     */     }
/* 196 */     cell.setFont(defaultFont);
/* 197 */     if (isSelected)
/* 198 */       cell.setForeground(getSelectionForeground());
/*     */     else
/* 200 */       cell.setForeground(getForeground());
/*     */   }
/*     */ 
/*     */   private void setRightAlignment(Component cell)
/*     */   {
/* 205 */     JLabel internalComponent = (JLabel)cell;
/* 206 */     internalComponent.setHorizontalAlignment(4);
/*     */   }
/*     */ 
/*     */   public int getRowIndexFromModel()
/*     */   {
/* 212 */     int rowIndexFromGui = getSelectedRow();
/*     */ 
/* 214 */     if ((rowIndexFromGui < 0) || (rowIndexFromGui >= getRowCount()))
/* 215 */       return -1;
/* 216 */     return getRowSorter().convertRowIndexToModel(rowIndexFromGui);
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 222 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 224 */     if (!GreedContext.isStrategyAllowed()) {
/* 225 */       model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_ID)).setHeaderValue(LocalizationManager.getText("column.position"));
/*     */     } else {
/* 227 */       model.getColumn(convertColumnIndexToView(1)).setHeaderValue(LocalizationManager.getText("column.ext.id"));
/* 228 */       model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_ID)).setHeaderValue(LocalizationManager.getText("column.position"));
/*     */     }
/*     */ 
/* 231 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_INSTRUMENT)).setHeaderValue(LocalizationManager.getText("column.instrument"));
/* 232 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_DIRECTION)).setHeaderValue(LocalizationManager.getText("column.direction"));
/* 233 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_AMOUNT)).setHeaderValue(LocalizationManager.getText("column.req.amount"));
/* 234 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_PROFIT_LOSS_PIP)).setHeaderValue(LocalizationManager.getText("column.pl.pips"));
/* 235 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_PROFIT_LOSS)).setHeaderValue(LocalizationManager.getText("column.pl"));
/* 236 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_PRICE_OPEN)).setHeaderValue(LocalizationManager.getText("column.price"));
/* 237 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_PRICE_CURRENT)).setHeaderValue(LocalizationManager.getText("column.current"));
/* 238 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_STOP_LOSS)).setHeaderValue(LocalizationManager.getText("column.stop.loss"));
/* 239 */     model.getColumn(convertColumnIndexToView(PositionsTableModel.COLUMN_TAKE_PROFIT)).setHeaderValue(LocalizationManager.getText("column.take.profit"));
/*     */   }
/*     */ 
/*     */   public PositionsTableModel getPositionsModel() {
/* 243 */     return this.model;
/*     */   }
/*     */ 
/*     */   public String getTableId()
/*     */   {
/* 248 */     String id = super.getTableId();
/*     */ 
/* 250 */     if (GreedContext.isStrategyAllowed()) {
/* 251 */       id = id + "JClient";
/*     */     }
/*     */     else {
/* 254 */       id = id + "JForex";
/*     */     }
/*     */ 
/* 257 */     return id;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.positions.PositionsTable
 * JD-Core Version:    0.6.0
 */