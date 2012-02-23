/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class InstrumentSelectionTableModel extends AbstractTableModel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static final int INSTRUMENT_SELECTION_COLUMN_IDX = 0;
/*     */   static final int INSTRUMENT_NAME_COLUMN_IDX = 1;
/*  31 */   private boolean[] instrumentsSign = null;
/*     */ 
/*  34 */   private Set<Instrument> selectedInstruments = null;
/*  35 */   private List<Instrument> tradableInstruments = new ArrayList();
/*     */ 
/*     */   public InstrumentSelectionTableModel(Set<Instrument> selectedInstruments) {
/*  38 */     this.selectedInstruments = selectedInstruments;
/*  39 */     if (this.selectedInstruments == null) {
/*  40 */       this.selectedInstruments = new LinkedHashSet();
/*     */     }
/*     */ 
/*  43 */     initTradableInstruments();
/*     */   }
/*     */ 
/*     */   private void initTradableInstruments() {
/*  47 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*  48 */     for (Instrument instrument : Instrument.values()) {
/*  49 */       if (feedDataProvider.getTimeOfFirstCandle(instrument, Period.TICK) != 9223372036854775807L) {
/*  50 */         this.tradableInstruments.add(instrument);
/*     */       }
/*     */     }
/*     */ 
/*  54 */     Collections.sort(this.tradableInstruments, new Comparator()
/*     */     {
/*     */       public int compare(Instrument o1, Instrument o2) {
/*  57 */         return o1.toString().compareTo(o2.toString());
/*     */       }
/*     */     });
/*  61 */     this.instrumentsSign = new boolean[this.tradableInstruments.size()];
/*     */ 
/*  64 */     initDataModel();
/*     */   }
/*     */ 
/*     */   private void initDataModel() {
/*  68 */     for (Instrument instrument : this.selectedInstruments) {
/*  69 */       int index = this.tradableInstruments.indexOf(instrument);
/*  70 */       if (index != -1)
/*  71 */         this.instrumentsSign[index] = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  78 */     return 2;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/*  83 */     switch (column) { case 0:
/*  84 */       return LocalizationManager.getText("tester.parameters.table.column.select");
/*     */     case 1:
/*  85 */       return LocalizationManager.getText("tester.parameters.table.column.instrument");
/*     */     }
/*  87 */     throw new IllegalArgumentException("Incorrect column index : " + column);
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/*  93 */     switch (columnIndex) { case 0:
/*  94 */       return Boolean.class;
/*     */     case 1:
/*  95 */       return Object.class;
/*     */     }
/*  97 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 103 */     if ((this.tradableInstruments == null) || (this.tradableInstruments.size() == 0)) {
/* 104 */       return 0;
/*     */     }
/*     */ 
/* 107 */     return this.tradableInstruments.size();
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object value, int rowIndex, int columnIndex)
/*     */   {
/* 112 */     if (value == null) {
/* 113 */       return;
/*     */     }
/*     */ 
/* 116 */     switch (columnIndex) {
/*     */     case 0:
/* 118 */       if ((value instanceof Boolean)) {
/* 119 */         this.instrumentsSign[rowIndex] = ((Boolean)value).booleanValue();
/*     */       }
/*     */       else
/*     */       {
/* 123 */         this.instrumentsSign[rowIndex] = false;
/*     */       }
/*     */ 
/* 126 */       fireTableDataChanged();
/* 127 */       break;
/*     */     case 1:
/* 130 */       break;
/*     */     default:
/* 140 */       throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/* 146 */     switch (columnIndex) {
/*     */     case 0:
/* 148 */       return Boolean.valueOf(this.instrumentsSign[rowIndex]);
/*     */     case 1:
/* 150 */       return this.tradableInstruments.get(rowIndex);
/*     */     }
/*     */ 
/* 154 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 160 */     switch (columnIndex) {
/*     */     case 0:
/* 162 */       return true;
/*     */     case 1:
/* 164 */       return false;
/*     */     }
/*     */ 
/* 168 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSelectedInstruments()
/*     */   {
/* 173 */     this.selectedInstruments.clear();
/*     */ 
/* 175 */     for (int i = 0; i < this.instrumentsSign.length; i++) {
/* 176 */       if (this.instrumentsSign[i] != 0) {
/* 177 */         this.selectedInstruments.add(this.tradableInstruments.get(i));
/*     */       }
/*     */     }
/*     */ 
/* 181 */     return this.selectedInstruments;
/*     */   }
/*     */ 
/*     */   public void selectAllInstruments() {
/* 185 */     setInstrumentsSelection(true);
/*     */   }
/*     */ 
/*     */   public void resetAllInstruments() {
/* 189 */     setInstrumentsSelection(false);
/*     */   }
/*     */ 
/*     */   private void setInstrumentsSelection(boolean selection) {
/* 193 */     for (int i = 0; i < this.instrumentsSign.length; i++) {
/* 194 */       this.instrumentsSign[i] = selection;
/*     */     }
/* 196 */     fireTableDataChanged();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.InstrumentSelectionTableModel
 * JD-Core Version:    0.6.0
 */