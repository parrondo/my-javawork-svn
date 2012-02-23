/*     */ package com.dukascopy.dds2.greed.gui.component.ticker;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ 
/*     */ public class TickerTableModel extends DefaultTableModel
/*     */ {
/*  25 */   private static final BigDecimal TWENTY = BigDecimal.TEN.multiply(new BigDecimal("2"));
/*     */   private List<InstrumentHolder> instruments;
/*     */   public static final int COL_INSTR = 0;
/*     */   public static final int COL_ASK = 2;
/*     */   public static final int COL_BID = 1;
/*     */   private final int COL_COUNT = 3;
/*     */   private int ROW_COUNT;
/*     */ 
/*     */   public TickerTableModel()
/*     */   {
/*  27 */     this.instruments = new ArrayList();
/*     */ 
/*  33 */     this.COL_COUNT = 3;
/*  34 */     this.ROW_COUNT = 0;
/*     */   }
/*     */   public boolean isCellEditable(int row, int column) {
/*  37 */     return false;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int column) {
/*  41 */     if ((row < 0) || (row >= getRowCount())) {
/*  42 */       return "";
/*     */     }
/*  44 */     InstrumentHolder ih = (InstrumentHolder)this.instruments.get(row);
/*     */     BigDecimal value;
/*     */     BigDecimal value;
/*  47 */     if (null != ih.priceAsk) {
/*  48 */       value = ih.priceAsk;
/*     */     }
/*     */     else
/*     */     {
/*     */       BigDecimal value;
/*  49 */       if (null != ih.priceBid)
/*  50 */         value = ih.priceBid;
/*     */       else
/*  52 */         value = TWENTY;
/*     */     }
/*  54 */     int scale = TWENTY.compareTo(value) >= 0 ? 5 : 3;
/*  55 */     switch (column) {
/*     */     case 0:
/*  57 */       return null != ih.name ? ih.name : "";
/*     */     case 2:
/*  60 */       return null != ih.priceAsk ? ih.priceAsk.setScale(scale, RoundingMode.HALF_EVEN).toPlainString() : "";
/*     */     case 1:
/*  63 */       return null != ih.priceBid ? ih.priceBid.setScale(scale, RoundingMode.HALF_EVEN).toPlainString() : "";
/*     */     }
/*     */ 
/*  66 */     return " ";
/*     */   }
/*     */ 
/*     */   public BigDecimal getPriceAt(int row, int column) {
/*  70 */     if ((row < 0) || (row > getRowCount())) {
/*  71 */       return null;
/*     */     }
/*  73 */     if ((column != 2) && (column != 1)) {
/*  74 */       return null;
/*     */     }
/*  76 */     InstrumentHolder ih = (InstrumentHolder)this.instruments.get(row);
/*  77 */     return 2 == column ? ih.priceAsk : ih.priceBid;
/*     */   }
/*     */ 
/*     */   public void addInstrument(String instrument) {
/*  81 */     if (getInstrumentIndex(instrument) == -1) {
/*  82 */       this.instruments.add(new InstrumentHolder(instrument, null, null));
/*  83 */       Collections.sort(this.instruments, new InstrumentHolderComparator(null));
/*  84 */       this.ROW_COUNT += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateInstrument(String instrument, OfferSide side, BigDecimal value) {
/*  89 */     for (InstrumentHolder ih : this.instruments)
/*  90 */       if (ih.name.equals(instrument)) {
/*  91 */         if (OfferSide.ASK == side) {
/*  92 */           ih.priceAsk = value; break;
/*     */         }
/*  94 */         ih.priceBid = value;
/*     */ 
/*  96 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public String getInstrumentName(int index) {
/* 101 */     if ((index < 0) || (index > getRowCount())) {
/* 102 */       return "";
/*     */     }
/* 104 */     return ((InstrumentHolder)this.instruments.get(index)).name;
/*     */   }
/*     */ 
/*     */   public void clearInstruments() {
/* 108 */     this.instruments.clear();
/* 109 */     this.ROW_COUNT = 0;
/*     */   }
/*     */ 
/*     */   public int getInstrumentIndex(String name) {
/* 113 */     int i = 0; for (int n = getRowCount(); i < n; i++) {
/* 114 */       InstrumentHolder ih = (InstrumentHolder)this.instruments.get(i);
/* 115 */       if (name.equals(ih.name)) {
/* 116 */         return i;
/*     */       }
/*     */     }
/* 119 */     return -1;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column) {
/* 123 */     switch (column) { case 0:
/* 124 */       return LocalizationManager.getText("column.ticker");
/*     */     case 2:
/* 125 */       return LocalizationManager.getText("column.ask");
/*     */     case 1:
/* 126 */       return LocalizationManager.getText("column.bid");
/*     */     }
/* 128 */     return "";
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/* 132 */     return 3;
/*     */   }
/*     */ 
/*     */   public int getRowCount() {
/* 136 */     return this.ROW_COUNT;
/*     */   }
/*     */ 
/*     */   public List<String> getInstrumentList() {
/* 140 */     List result = new ArrayList();
/* 141 */     for (InstrumentHolder instrument : this.instruments) {
/* 142 */       result.add(instrument.name);
/*     */     }
/* 144 */     return result;
/*     */   }
/*     */ 
/*     */   private class InstrumentHolderComparator
/*     */     implements Comparator<TickerTableModel.InstrumentHolder>
/*     */   {
/*     */     private InstrumentHolderComparator()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int compare(TickerTableModel.InstrumentHolder ih1, TickerTableModel.InstrumentHolder ih2)
/*     */     {
/* 161 */       return ih1.name.compareTo(ih2.name);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InstrumentHolder
/*     */   {
/*     */     String name;
/*     */     BigDecimal priceAsk;
/*     */     BigDecimal priceBid;
/*     */ 
/*     */     public InstrumentHolder(String name, BigDecimal priceAsk, BigDecimal priceBid)
/*     */     {
/* 152 */       this.name = name;
/* 153 */       this.priceAsk = priceAsk;
/* 154 */       this.priceBid = priceBid;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ticker.TickerTableModel
 * JD-Core Version:    0.6.0
 */