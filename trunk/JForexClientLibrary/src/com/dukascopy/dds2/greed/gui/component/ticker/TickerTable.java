/*     */ package com.dukascopy.dds2.greed.gui.component.ticker;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class TickerTable extends JLocalizableTable
/*     */ {
/*     */   private static final int INSTRUMENT_COLUMN = 0;
/*  29 */   private static Font boldFont = null;
/*     */ 
/*  35 */   private Map<Integer, Color> rowColors = new HashMap();
/*  36 */   private Map<Integer, Color> rowBgColors = new HashMap();
/*     */ 
/*  38 */   private Color defaultColor = Color.BLACK;
/*  39 */   private Color defaultBgColor = GreedContext.GLOBAL_BACKGROUND;
/*     */ 
/*     */   public TickerTable(TableModel dm)
/*     */   {
/*  32 */     super(dm);
/*     */   }
/*     */ 
/*     */   public void setRowColor(int row, Color color)
/*     */   {
/*  42 */     this.rowColors.put(Integer.valueOf(row), color);
/*     */   }
/*     */ 
/*     */   public void setRowBgColor(int row, Color color) {
/*  46 */     this.rowBgColors.put(Integer.valueOf(row), color);
/*     */   }
/*     */ 
/*     */   public Color getDefaultColor() {
/*  50 */     return this.defaultColor;
/*     */   }
/*     */ 
/*     */   public void setDefaultColor(Color defaultColor) {
/*  54 */     this.defaultColor = defaultColor;
/*     */   }
/*     */ 
/*     */   public Color getDefaultBgColor() {
/*  58 */     return this.defaultBgColor;
/*     */   }
/*     */ 
/*     */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
/*  62 */     Component cell = super.prepareRenderer(renderer, row, column);
/*     */ 
/*  64 */     if (boldFont == null) {
/*  65 */       Font currentFont = cell.getFont();
/*  66 */       boldFont = new Font(currentFont.getFamily(), 1, currentFont.getSize());
/*     */     }
/*  68 */     if (column == 0) {
/*  69 */       cell.setFont(boldFont);
/*     */     }
/*     */ 
/*  72 */     if ((getSelectedRow() == row) && (getSelectedColumn() == column)) {
/*  73 */       if ((this.rowColors.containsKey(Integer.valueOf(rowToModelRow(row)))) && (column != 0)) {
/*  74 */         Color cc = (Color)this.rowColors.get(Integer.valueOf(row));
/*  75 */         if (!cc.equals(Color.BLACK))
/*  76 */           cell.setForeground((Color)this.rowColors.get(Integer.valueOf(row)));
/*     */         else
/*  78 */           cell.setForeground(GreedContext.SELECTION_FG_COLOR);
/*     */       }
/*     */       else {
/*  81 */         cell.setForeground(GreedContext.SELECTION_FG_COLOR);
/*     */       }
/*     */     }
/*  84 */     else if (this.rowColors.containsKey(Integer.valueOf(rowToModelRow(row)))) {
/*  85 */       if (column != 0)
/*  86 */         cell.setForeground((Color)this.rowColors.get(Integer.valueOf(rowToModelRow(row))));
/*     */       else
/*  88 */         cell.setForeground(this.defaultColor);
/*     */     }
/*     */     else {
/*  91 */       cell.setForeground(this.defaultColor);
/*     */     }
/*     */ 
/*  95 */     if (this.rowBgColors.containsKey(Integer.valueOf(row)))
/*  96 */       cell.setBackground((Color)this.rowBgColors.get(Integer.valueOf(rowToModelRow(row))));
/*     */     else {
/*  98 */       cell.setBackground(this.defaultBgColor);
/*     */     }
/*     */ 
/* 101 */     if (inSelected(row)) {
/* 102 */       cell.setBackground(GreedContext.SELECTION_COLOR);
/* 103 */       cell.setFont(boldFont);
/* 104 */       cell.setForeground(GreedContext.SELECTION_FG_COLOR);
/*     */     }
/*     */ 
/* 107 */     return cell;
/*     */   }
/*     */ 
/*     */   public int rowToModelRow(int row) {
/* 111 */     return getRowSorter().convertRowIndexToModel(row);
/*     */   }
/*     */ 
/*     */   public boolean inSelected(int x) {
/* 115 */     for (int i : getSelectedRows()) {
/* 116 */       if (i == x) return true;
/*     */     }
/*     */ 
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   public int getRowIndexFromModel() {
/* 123 */     int rowIndexFromGui = getSelectedRow();
/*     */ 
/* 125 */     if ((rowIndexFromGui < 0) || (rowIndexFromGui >= getRowCount())) {
/* 126 */       return -1;
/*     */     }
/* 128 */     return getRowSorter().convertRowIndexToModel(rowIndexFromGui);
/*     */   }
/*     */ 
/*     */   public int getMouseSelectedRowIndexFromModel(MouseEvent e)
/*     */   {
/* 133 */     int rowSelected = rowAtPoint(e.getPoint());
/*     */ 
/* 135 */     if ((rowSelected < 0) || (rowSelected >= getRowCount())) {
/* 136 */       return -1;
/*     */     }
/* 138 */     return getRowSorter().convertRowIndexToModel(rowSelected);
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 144 */     getTableHeader().getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("column.ticker"));
/* 145 */     getTableHeader().getColumnModel().getColumn(2).setHeaderValue(LocalizationManager.getText("column.ask"));
/* 146 */     getTableHeader().getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("column.bid"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ticker.TickerTable
 * JD-Core Version:    0.6.0
 */