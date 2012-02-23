/*    */ package com.dukascopy.dds2.greed.gui.component.marketdepth;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*    */ import java.awt.Component;
/*    */ import java.awt.Font;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ import javax.swing.table.TableColumn;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public class MarketDepthTable extends JLocalizableTable
/*    */ {
/* 45 */   private static Map<String, Font> fontCache = new HashMap();
/*    */ 
/*    */   public MarketDepthTable(int numRows, int numColumns)
/*    */   {
/* 29 */     super(numRows, numColumns);
/* 30 */     getColumnModel().getColumn(1).setPreferredWidth(70);
/* 31 */     getColumnModel().getColumn(1).setMinWidth(60);
/* 32 */     getColumnModel().getColumn(2).setPreferredWidth(70);
/* 33 */     getColumnModel().getColumn(2).setMinWidth(60);
/*    */ 
/* 35 */     setFocusable(false);
/* 36 */     setCellSelectionEnabled(false);
/* 37 */     setRowSelectionAllowed(false);
/* 38 */     setColumnSelectionAllowed(false);
/*    */ 
/* 40 */     translate();
/*    */ 
/* 42 */     getTableHeader().setReorderingAllowed(true);
/*    */   }
/*    */ 
/*    */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
/*    */   {
/* 48 */     Component cell = super.prepareRenderer(renderer, row, column);
/* 49 */     if (column < getColumnCount()) {
/* 50 */       int modelColumn = convertColumnIndexToModel(column);
/* 51 */       if ((modelColumn == 1) || (modelColumn == 2)) {
/* 52 */         Font currentFont = cell.getFont();
/*    */ 
/* 54 */         Font boldFont = null;
/* 55 */         String fontKey = currentFont.getFamily().toString() + "_" + currentFont.getSize();
/* 56 */         if (fontCache.containsKey(fontKey)) {
/* 57 */           boldFont = (Font)fontCache.get(fontKey);
/*    */         } else {
/* 59 */           boldFont = new Font(currentFont.getFamily(), 1, currentFont.getSize());
/* 60 */           fontCache.put(fontKey, boldFont);
/*    */         }
/* 62 */         cell.setFont(boldFont);
/*    */       }
/* 64 */       if (modelColumn == 3)
/* 65 */         ((JLabel)cell).setHorizontalAlignment(4);
/*    */       else {
/* 67 */         ((JLabel)cell).setHorizontalAlignment(2);
/*    */       }
/*    */     }
/* 70 */     return cell;
/*    */   }
/*    */ 
/*    */   public void translate()
/*    */   {
/* 76 */     boolean isCommodity = (Instrument.XAUUSD.equals(LotAmountChanger.getSelectedInstrument())) || (Instrument.XAGUSD.equals(LotAmountChanger.getSelectedInstrument()));
/*    */ 
/* 79 */     getTableHeader().getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText(isCommodity ? "column.commodity.vol" : "column.vol"));
/* 80 */     getTableHeader().getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("column.bid"));
/* 81 */     getTableHeader().getColumnModel().getColumn(2).setHeaderValue(LocalizationManager.getText("column.ask"));
/* 82 */     getTableHeader().getColumnModel().getColumn(3).setHeaderValue(LocalizationManager.getText(isCommodity ? "column.commodity.vol" : "column.vol"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthTable
 * JD-Core Version:    0.6.0
 */