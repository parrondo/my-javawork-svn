/*     */ package com.dukascopy.dds2.greed.gui.component.exposure;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellEditor;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeaderItemListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ExposureHolderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.PriceRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.CommonWorkspaceHellper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTableModelListener;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.SystemColor;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class ExposureTable extends JLocalizableTable
/*     */ {
/*     */   private Font boldFont;
/*  45 */   private static Font defaultFont = null;
/*     */   private ExposureTableModel exposureTableModel;
/*     */ 
/*     */   public ExposureTable(TableModel model)
/*     */   {
/*  49 */     super(model);
/*     */ 
/*  51 */     this.exposureTableModel = ((ExposureTableModel)model);
/*     */ 
/*  53 */     getSelectionModel().setSelectionMode(0);
/*  54 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*  55 */     setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  56 */     setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  57 */     setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/*     */ 
/*  59 */     setDefaultRenderer(ExposureTableModel.ExposureHolder.class, new ExposureHolderRenderer());
/*  60 */     setDefaultRenderer(Money.class, new MoneyCellRenderer());
/*     */ 
/*  63 */     getColumnModel().getColumn(0).setHeaderValue("#");
/*  64 */     getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener(this), this));
/*     */ 
/*  66 */     translate();
/*     */ 
/*  68 */     getColumnModel().getColumn(0).setPreferredWidth(40);
/*  69 */     getColumnModel().getColumn(0).setMaxWidth(40);
/*  70 */     getColumnModel().getColumn(0).setMinWidth(40);
/*  71 */     getColumnModel().getColumn(0).setResizable(false);
/*  72 */     getColumnModel().getColumn(0).setCellRenderer(new CheckBoxCellRenderer());
/*  73 */     getColumnModel().getColumn(0).setCellEditor(new CheckBoxCellEditor());
/*     */ 
/*  75 */     getColumnModel().getColumn(1).setMinWidth(70);
/*     */ 
/*  77 */     getColumnModel().getColumn(2).setMinWidth(50);
/*  78 */     getColumnModel().getColumn(3).setMinWidth(150);
/*  79 */     getColumnModel().getColumn(5).setCellRenderer(new PriceRenderer());
/*  80 */     getTableHeader().setReorderingAllowed(true);
/*     */ 
/*  82 */     getModel().addTableModelListener(new JLocalizableTableModelListener(0, CommonWorkspaceHellper.getPositionsSummaryLabelKey()));
/*     */ 
/*  84 */     if (GreedContext.IS_FXDD_LABEL) {
/*  85 */       hideColumn(3);
/*  86 */       hideColumn(6);
/*  87 */       hideColumn(5);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
/*     */   {
/*  93 */     Component cell = super.prepareRenderer(renderer, row, column);
/*     */ 
/*  95 */     column = convertColumnIndexToModel(column);
/*  96 */     boolean isSelected = row == getSelectedRow();
/*     */ 
/*  98 */     if (isSelected) {
/*  99 */       resetToDefault(cell, isSelected, column);
/* 100 */       return cell;
/*     */     }
/*     */ 
/* 103 */     switch (column) {
/*     */     case 1:
/* 105 */       if (this.boldFont == null) {
/* 106 */         Font currentFont = cell.getFont();
/* 107 */         this.boldFont = new Font(currentFont.getFamily(), 1, currentFont.getSize());
/*     */       }
/* 109 */       cell.setFont(this.boldFont);
/* 110 */       ((JLabel)cell).setHorizontalAlignment(4);
/* 111 */       cell.setForeground(SystemColor.textText);
/* 112 */       return cell;
/*     */     case 2:
/* 114 */       String text = ((JLabel)cell).getText();
/*     */ 
/* 116 */       if (text.equalsIgnoreCase("LONG")) cell.setForeground(PositionsTable.COLOR_LONG);
/* 117 */       else if (text.equalsIgnoreCase("SHORT")) cell.setForeground(PositionsTable.COLOR_SHORT); else
/* 118 */         cell.setForeground(SystemColor.textText);
/* 119 */       return cell;
/*     */     case 4:
/* 121 */       String amount = ((JLabel)cell).getText();
/* 122 */       if (amount.indexOf("-") >= 0) cell.setForeground(MoneyCellRenderer.COLOR_NEGATIVE); else
/* 123 */         cell.setForeground(MoneyCellRenderer.COLOR_POSITIVE);
/* 124 */       return cell;
/*     */     case 6:
/* 126 */       return cell;
/*     */     case 3:
/* 128 */     case 5: } cell.setForeground(SystemColor.textText);
/* 129 */     return cell;
/*     */   }
/*     */ 
/*     */   private void resetToDefault(Component cell, boolean isSelected, int column)
/*     */   {
/* 134 */     if (defaultFont == null) {
/* 135 */       defaultFont = new Font(cell.getFont().getFamily(), 0, cell.getFont().getSize());
/*     */     }
/* 137 */     if (column == 3) {
/* 138 */       String plainText = getPlainTextFromHtml(((JLabel)cell).getText());
/* 139 */       ((JLabel)cell).setText(plainText);
/*     */     }
/*     */ 
/* 142 */     cell.setFont(defaultFont);
/* 143 */     cell.setForeground(getSelectionForeground());
/*     */   }
/*     */ 
/*     */   private String getPlainTextFromHtml(String html)
/*     */   {
/* 148 */     StringBuffer buffer = new StringBuffer(html);
/* 149 */     StringBuffer result = new StringBuffer();
/* 150 */     boolean canAppend = false;
/* 151 */     char i = '1';
/* 152 */     for (int p = 0; p < buffer.length(); p++) {
/* 153 */       i = buffer.charAt(p);
/* 154 */       if (i == '<') canAppend = false;
/* 155 */       if (i == '>') canAppend = true;
/*     */ 
/* 157 */       if ((canAppend) && (i != '>')) {
/* 158 */         result.append(i);
/*     */       }
/*     */     }
/* 161 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public void tableChanged(TableModelEvent e)
/*     */   {
/* 166 */     int selectedRow = getSelectedRow();
/* 167 */     super.tableChanged(e);
/* 168 */     if ((selectedRow > 0) && (selectedRow < getModel().getRowCount())) setRowSelectionInterval(selectedRow, selectedRow);
/*     */   }
/*     */ 
/*     */   public int getRowIndexFromModel()
/*     */   {
/* 173 */     int rowIndexFromGui = getSelectedRow();
/*     */ 
/* 175 */     if ((rowIndexFromGui < 0) || (rowIndexFromGui >= getRowCount()))
/* 176 */       return -1;
/* 177 */     return getRowSorter().convertRowIndexToModel(rowIndexFromGui);
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 183 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 185 */     model.getColumn(convertColumnIndexToView(1)).setHeaderValue(LocalizationManager.getText("column.instrument"));
/* 186 */     model.getColumn(convertColumnIndexToView(2)).setHeaderValue(LocalizationManager.getText("column.direction"));
/* 187 */     model.getColumn(convertColumnIndexToView(3)).setHeaderValue(LocalizationManager.getText("column.long.short"));
/* 188 */     model.getColumn(convertColumnIndexToView(4)).setHeaderValue(LocalizationManager.getText("column.req.amount"));
/* 189 */     model.getColumn(convertColumnIndexToView(5)).setHeaderValue(LocalizationManager.getText("column.price"));
/* 190 */     model.getColumn(convertColumnIndexToView(6)).setHeaderValue(LocalizationManager.getText("column.pl"));
/*     */   }
/*     */ 
/*     */   public ExposureTableModel getExposureTableModel() {
/* 194 */     return this.exposureTableModel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable
 * JD-Core Version:    0.6.0
 */