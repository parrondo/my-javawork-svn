/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod.Type;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportFormat;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportOfferSide;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.PeriodType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class InstrumentSelectionTableModel extends AbstractTableModel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int INSTRUMENT_SELECTION_COLUMN_IDX = 0;
/*     */   public static final int INSTRUMENT_NAME_COLUMN_IDX = 1;
/*     */   public static final int PERIOD_TYPE_COLUMN_IDX = 2;
/*     */   public static final int PERIOD_COLUMN_IDX = 3;
/*     */   public static final int OFFERSIDE_COLUMN_IDX = 4;
/*     */   public static final int DATEFROM_COLUMN_IDX = 5;
/*     */   public static final int DATETO_COLUMN_IDX = 6;
/*     */   public static final int EXPORTFORMAT_COLUMN_IDX = 7;
/*  51 */   private boolean[] selectedInstruments = null;
/*  52 */   private PeriodType[] selectedPeriodTypes = null;
/*  53 */   private CompositePeriod[] selectedPeriods = null;
/*  54 */   private PriceRange[] selectedPriceRanges = null;
/*  55 */   private JForexPeriod[] selectedPointAndFigures = null;
/*  56 */   private ExportOfferSide[] selectedOfferSides = null;
/*  57 */   private Long[] selectedDatesFrom = null;
/*  58 */   private Long[] selectedDatesTo = null;
/*  59 */   private ExportFormat[] selectedExportFormats = null;
/*     */ 
/*  61 */   private List<Instrument> tradableInstruments = new ArrayList();
/*     */ 
/*  63 */   private Map<ExportFormat, Map<PeriodType, List<CompositePeriod>>> periodsByExport = new HashMap();
/*  64 */   private Map<ExportFormat, List<PeriodType>> periodsTypesByExport = new HashMap();
/*     */ 
/*  66 */   private List<PriceRange> priceRanges = null;
/*     */ 
/*     */   public InstrumentSelectionTableModel() {
/*  69 */     initTableModelData();
/*  70 */     initExportFormats();
/*  71 */     initPointAndFigures();
/*  72 */     initPeriodTypes();
/*  73 */     initPeriods();
/*     */   }
/*     */ 
/*     */   public void initTradableInstruments() {
/*  77 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*  78 */     for (Instrument instrument : feedDataProvider.getInstrumentsSupportedByFileCacheGenerator()) {
/*  79 */       this.tradableInstruments.add(instrument);
/*     */     }
/*     */ 
/*  82 */     Collections.sort(this.tradableInstruments, new Comparator()
/*     */     {
/*     */       public int compare(Instrument o1, Instrument o2) {
/*  85 */         return o1.toString().compareTo(o2.toString());
/*     */       }
/*     */     });
/*  89 */     initTableModelData();
/*  90 */     initExportFormats();
/*  91 */     initPointAndFigures();
/*     */ 
/*  93 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private void initPeriodTypes() {
/*  97 */     initPeriodTypesForCSVExport();
/*  98 */     initPeriodTypesForHSTExport();
/*     */   }
/*     */ 
/*     */   private void initPeriodTypesForCSVExport() {
/* 102 */     List periodTypes = new ArrayList();
/*     */ 
/* 104 */     for (PeriodType periodType : PeriodType.values()) {
/* 105 */       periodTypes.add(periodType);
/*     */     }
/*     */ 
/* 108 */     this.periodsTypesByExport.put(ExportFormat.CSV, periodTypes);
/*     */   }
/*     */ 
/*     */   private void initPeriodTypesForHSTExport() {
/* 112 */     List periodTypes = new ArrayList();
/*     */ 
/* 114 */     periodTypes.add(PeriodType.Minutes);
/* 115 */     periodTypes.add(PeriodType.Hours);
/* 116 */     periodTypes.add(PeriodType.Days);
/* 117 */     periodTypes.add(PeriodType.Weeks);
/* 118 */     periodTypes.add(PeriodType.Months);
/*     */ 
/* 120 */     this.periodsTypesByExport.put(ExportFormat.HST, periodTypes);
/*     */   }
/*     */ 
/*     */   private void initPeriodsForCSVExport() {
/* 124 */     Map periodsMap = new HashMap();
/*     */ 
/* 126 */     for (PeriodType periodType : PeriodType.values()) {
/* 127 */       switch (2.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$PeriodType[periodType.ordinal()])
/*     */       {
/*     */       case 1:
/* 130 */         break;
/*     */       case 2:
/* 133 */         List ticks = new ArrayList();
/* 134 */         ticks.addAll(getCompositePeriodTickBars());
/* 135 */         periodsMap.put(PeriodType.Ticks, ticks);
/* 136 */         break;
/*     */       case 3:
/* 138 */         periodsMap.put(PeriodType.Seconds, getCompositePeriodPeriods(Unit.Second, 1, 59));
/* 139 */         break;
/*     */       case 4:
/* 141 */         periodsMap.put(PeriodType.Minutes, getCompositePeriodPeriods(Unit.Minute, 59));
/* 142 */         break;
/*     */       case 5:
/* 144 */         periodsMap.put(PeriodType.Hours, getCompositePeriodPeriods(Unit.Hour, 12));
/* 145 */         break;
/*     */       case 6:
/* 147 */         periodsMap.put(PeriodType.Days, getCompositePeriodPeriods(Unit.Day, 6));
/* 148 */         break;
/*     */       case 7:
/* 150 */         periodsMap.put(PeriodType.Weeks, getCompositePeriodPeriods(Unit.Week, 4));
/* 151 */         break;
/*     */       case 8:
/* 153 */         periodsMap.put(PeriodType.Months, getCompositePeriodPeriods(Unit.Month, 11));
/* 154 */         break;
/*     */       case 9:
/* 156 */         this.priceRanges = PriceRange.createJForexPriceRanges();
/* 157 */         break;
/*     */       case 10:
/* 160 */         break;
/*     */       default:
/* 161 */         throw new IllegalArgumentException("Incorrect periodType : " + periodType);
/*     */       }
/*     */     }
/*     */ 
/* 165 */     this.periodsByExport.put(ExportFormat.CSV, periodsMap);
/*     */   }
/*     */ 
/*     */   private void initPeriodsForHSTExport() {
/* 169 */     Map periodsMap = new HashMap();
/*     */ 
/* 171 */     Map periodsByCVS = (Map)this.periodsByExport.get(ExportFormat.CSV);
/* 172 */     for (Map.Entry entry : periodsByCVS.entrySet()) {
/* 173 */       PeriodType periodType = (PeriodType)entry.getKey();
/* 174 */       List compositePeriods = (List)entry.getValue();
/*     */ 
/* 176 */       switch (2.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$PeriodType[periodType.ordinal()])
/*     */       {
/*     */       case 1:
/* 179 */         break;
/*     */       case 2:
/* 182 */         break;
/*     */       case 3:
/* 185 */         break;
/*     */       case 4:
/* 187 */         List minutes = new ArrayList();
/* 188 */         for (CompositePeriod compositePeriod : compositePeriods) {
/* 189 */           if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 190 */             Period period = compositePeriod.getPeriod();
/* 191 */             if ((period == Period.ONE_MIN) || (period == Period.FIVE_MINS) || (period == Period.FIFTEEN_MINS) || (period == Period.THIRTY_MINS)) {
/* 192 */               minutes.add(compositePeriod);
/*     */             }
/*     */           }
/*     */         }
/* 196 */         periodsMap.put(PeriodType.Minutes, minutes);
/* 197 */         break;
/*     */       case 5:
/* 199 */         List hours = new ArrayList();
/* 200 */         for (CompositePeriod compositePeriod : compositePeriods) {
/* 201 */           if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 202 */             Period period = compositePeriod.getPeriod();
/* 203 */             if ((period == Period.ONE_HOUR) || (period == Period.FOUR_HOURS)) {
/* 204 */               hours.add(compositePeriod);
/*     */             }
/*     */           }
/*     */         }
/* 208 */         periodsMap.put(PeriodType.Hours, hours);
/* 209 */         break;
/*     */       case 6:
/* 211 */         List days = new ArrayList();
/* 212 */         for (CompositePeriod compositePeriod : compositePeriods) {
/* 213 */           if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 214 */             Period period = compositePeriod.getPeriod();
/* 215 */             if (period == Period.DAILY) {
/* 216 */               days.add(compositePeriod);
/*     */             }
/*     */           }
/*     */         }
/* 220 */         periodsMap.put(PeriodType.Days, days);
/* 221 */         break;
/*     */       case 7:
/* 223 */         List weeks = new ArrayList();
/* 224 */         for (CompositePeriod compositePeriod : compositePeriods) {
/* 225 */           if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 226 */             Period period = compositePeriod.getPeriod();
/* 227 */             if (period == Period.WEEKLY) {
/* 228 */               weeks.add(compositePeriod);
/*     */             }
/*     */           }
/*     */         }
/* 232 */         periodsMap.put(PeriodType.Weeks, weeks);
/* 233 */         break;
/*     */       case 8:
/* 235 */         List months = new ArrayList();
/*     */ 
/* 237 */         for (CompositePeriod compositePeriod : compositePeriods) {
/* 238 */           if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 239 */             Period period = compositePeriod.getPeriod();
/* 240 */             if (period == Period.MONTHLY) {
/* 241 */               months.add(compositePeriod);
/*     */             }
/*     */           }
/*     */         }
/* 245 */         periodsMap.put(PeriodType.Months, months);
/* 246 */         break;
/*     */       case 9:
/* 249 */         break;
/*     */       case 10:
/* 252 */         break;
/*     */       default:
/* 253 */         throw new IllegalArgumentException("Incorrect periodType : " + periodType);
/*     */       }
/*     */     }
/*     */ 
/* 257 */     this.periodsByExport.put(ExportFormat.HST, periodsMap);
/*     */   }
/*     */ 
/*     */   private void initPeriods() {
/* 261 */     initPeriodsForCSVExport();
/* 262 */     initPeriodsForHSTExport();
/*     */   }
/*     */ 
/*     */   private void initExportFormats() {
/* 266 */     int size = this.tradableInstruments.size();
/* 267 */     for (int i = 0; i < size; i++)
/* 268 */       this.selectedExportFormats[i] = ExportFormat.CSV;
/*     */   }
/*     */ 
/*     */   private void initPointAndFigures()
/*     */   {
/* 273 */     int size = this.tradableInstruments.size();
/*     */ 
/* 275 */     for (int i = 0; i < size; i++) {
/* 276 */       JForexPeriod jForexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE);
/* 277 */       jForexPeriod.setPriceRange(PriceRange.valueOf(1));
/* 278 */       jForexPeriod.setReversalAmount(ReversalAmount.valueOf(1));
/* 279 */       this.selectedPointAndFigures[i] = jForexPeriod;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initTableModelData() {
/* 284 */     int size = this.tradableInstruments.size();
/*     */ 
/* 286 */     this.selectedInstruments = new boolean[size];
/* 287 */     this.selectedPeriodTypes = new PeriodType[size];
/* 288 */     this.selectedPeriods = new CompositePeriod[size];
/* 289 */     this.selectedPriceRanges = new PriceRange[size];
/* 290 */     this.selectedPointAndFigures = new JForexPeriod[size];
/* 291 */     this.selectedOfferSides = new ExportOfferSide[size];
/* 292 */     this.selectedDatesFrom = new Long[size];
/* 293 */     this.selectedDatesTo = new Long[size];
/* 294 */     this.selectedExportFormats = new ExportFormat[size];
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/* 299 */     return 2;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/* 304 */     switch (column) { case 0:
/* 305 */       return LocalizationManager.getText("tester.parameters.table.column.select");
/*     */     case 1:
/* 306 */       return LocalizationManager.getText("tester.parameters.table.column.instrument");
/*     */     case 2:
/* 307 */       return LocalizationManager.getText("hdm.data.type");
/*     */     case 3:
/* 308 */       return LocalizationManager.getText("hdm.value");
/*     */     case 4:
/* 309 */       return LocalizationManager.getText("hdm.offer.side");
/*     */     case 5:
/* 310 */       return LocalizationManager.getText("hdm.date.from");
/*     */     case 6:
/* 311 */       return LocalizationManager.getText("hdm.date.to");
/*     */     case 7:
/* 312 */       return LocalizationManager.getText("hdm.export.format"); }
/* 313 */     throw new IllegalArgumentException("Incorrect column index : " + column);
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/* 319 */     switch (columnIndex) { case 0:
/* 320 */       return Boolean.class;
/*     */     case 1:
/* 321 */       return Object.class;
/*     */     case 2:
/* 322 */       return Object.class;
/*     */     case 3:
/* 323 */       return Object.class;
/*     */     case 4:
/* 324 */       return Object.class;
/*     */     case 5:
/* 325 */       return Object.class;
/*     */     case 6:
/* 326 */       return Object.class;
/*     */     case 7:
/* 327 */       return Object.class; }
/* 328 */     throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 334 */     if ((this.tradableInstruments == null) || (this.tradableInstruments.size() == 0)) {
/* 335 */       return 0;
/*     */     }
/* 337 */     return this.tradableInstruments.size();
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object value, int rowIndex, int columnIndex)
/*     */   {
/* 342 */     if ((value == null) && (columnIndex != 5) && (columnIndex != 6)) {
/* 343 */       return;
/*     */     }
/*     */ 
/* 346 */     switch (columnIndex) {
/*     */     case 0:
/* 348 */       if ((value instanceof Boolean)) {
/* 349 */         boolean selected = ((Boolean)value).booleanValue();
/* 350 */         this.selectedInstruments[rowIndex] = ((Boolean)value).booleanValue();
/* 351 */         if (!selected) {
/* 352 */           this.selectedPeriodTypes[rowIndex] = null;
/* 353 */           this.selectedDatesFrom[rowIndex] = null;
/* 354 */           this.selectedDatesTo[rowIndex] = null;
/* 355 */           this.selectedOfferSides[rowIndex] = null;
/* 356 */           this.selectedExportFormats[rowIndex] = ExportFormat.CSV;
/*     */         }
/*     */       } else {
/* 359 */         this.selectedInstruments[rowIndex] = false;
/* 360 */         this.selectedPeriodTypes[rowIndex] = null;
/* 361 */         this.selectedDatesFrom[rowIndex] = null;
/* 362 */         this.selectedDatesTo[rowIndex] = null;
/* 363 */         this.selectedOfferSides[rowIndex] = null;
/* 364 */         this.selectedExportFormats[rowIndex] = ExportFormat.CSV;
/*     */       }
/*     */ 
/* 367 */       fireTableRowsUpdated(rowIndex, rowIndex);
/* 368 */       break;
/*     */     case 1:
/* 371 */       break;
/*     */     case 2:
/* 373 */       if (this.selectedPeriodTypes[rowIndex] == value) break;
/* 374 */       this.selectedPeriodTypes[rowIndex] = ((PeriodType)value);
/* 375 */       if (this.selectedPeriodTypes[rowIndex] == PeriodType.Range) {
/* 376 */         this.selectedPeriods[rowIndex] = null;
/* 377 */         this.selectedPriceRanges[rowIndex] = null;
/* 378 */         this.selectedPointAndFigures[rowIndex] = null;
/*     */       }
/* 380 */       else if (this.selectedPeriodTypes[rowIndex] == PeriodType.PF) {
/* 381 */         this.selectedPeriods[rowIndex] = null;
/* 382 */         this.selectedPriceRanges[rowIndex] = null;
/* 383 */         this.selectedPointAndFigures[rowIndex] = null;
/*     */       }
/* 385 */       else if (value == PeriodType.Tick)
/*     */       {
/* 387 */         CompositePeriod tickPeriod = new CompositePeriod();
/* 388 */         tickPeriod.setType(CompositePeriod.Type.PERIOD);
/* 389 */         tickPeriod.setPeriod(Period.TICK);
/*     */ 
/* 391 */         this.selectedPeriods[rowIndex] = tickPeriod;
/* 392 */         this.selectedOfferSides[rowIndex] = null;
/* 393 */         this.selectedPriceRanges[rowIndex] = null;
/* 394 */         this.selectedPointAndFigures[rowIndex] = null;
/*     */       }
/* 396 */       else if (value == PeriodType.Ticks) {
/* 397 */         this.selectedPeriods[rowIndex] = null;
/* 398 */         this.selectedPriceRanges[rowIndex] = null;
/* 399 */         this.selectedPointAndFigures[rowIndex] = null;
/*     */       }
/*     */       else {
/* 402 */         this.selectedPeriods[rowIndex] = null;
/* 403 */         this.selectedPriceRanges[rowIndex] = null;
/* 404 */         this.selectedPointAndFigures[rowIndex] = null; } break;
/*     */     case 3:
/* 409 */       if (this.selectedPeriodTypes[rowIndex] == PeriodType.Range)
/* 410 */         this.selectedPriceRanges[rowIndex] = ((PriceRange)value);
/* 411 */       else if (this.selectedPeriodTypes[rowIndex] == PeriodType.PF) {
/* 412 */         this.selectedPointAndFigures[rowIndex] = ((JForexPeriod)value);
/*     */       }
/*     */       else {
/* 415 */         this.selectedPeriods[rowIndex] = ((CompositePeriod)value);
/*     */       }
/* 417 */       break;
/*     */     case 4:
/* 419 */       this.selectedOfferSides[rowIndex] = ((ExportOfferSide)value);
/* 420 */       break;
/*     */     case 5:
/* 422 */       this.selectedDatesFrom[rowIndex] = ((Long)value);
/* 423 */       break;
/*     */     case 6:
/* 425 */       this.selectedDatesTo[rowIndex] = ((Long)value);
/* 426 */       break;
/*     */     case 7:
/* 428 */       ExportFormat exportFormat = (ExportFormat)value;
/* 429 */       this.selectedExportFormats[rowIndex] = exportFormat;
/* 430 */       switch (exportFormat) {
/*     */       case CSV:
/* 432 */         fireTableRowsUpdated(rowIndex, rowIndex);
/* 433 */         break;
/*     */       case HST:
/* 435 */         PeriodType currentPeriodType = this.selectedPeriodTypes[rowIndex];
/* 436 */         if ((currentPeriodType != null) && (!currentPeriodType.isHSTCompatible())) {
/* 437 */           this.selectedPeriodTypes[rowIndex] = null;
/*     */         }
/*     */ 
/* 440 */         CompositePeriod compositePeriod = this.selectedPeriods[rowIndex];
/* 441 */         if ((compositePeriod != null) && (!compositePeriod.isHSTCompatible())) {
/* 442 */           this.selectedPeriods[rowIndex] = null;
/*     */         }
/* 444 */         this.selectedPriceRanges[rowIndex] = null;
/* 445 */         this.selectedPointAndFigures[rowIndex] = null;
/*     */ 
/* 447 */         fireTableRowsUpdated(rowIndex, rowIndex);
/* 448 */         break;
/*     */       default:
/* 449 */         throw new IllegalArgumentException("Incorrect export format : " + exportFormat);
/*     */       }
/* 451 */       break;
/*     */     default:
/* 452 */       throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/* 458 */     switch (columnIndex) {
/*     */     case 0:
/* 460 */       return Boolean.valueOf(this.selectedInstruments[rowIndex]);
/*     */     case 1:
/* 462 */       return this.tradableInstruments.get(rowIndex);
/*     */     case 2:
/* 464 */       return this.selectedPeriodTypes[rowIndex];
/*     */     case 3:
/* 466 */       if (this.selectedPeriodTypes[rowIndex] == PeriodType.Range)
/* 467 */         return this.selectedPriceRanges[rowIndex];
/* 468 */       if (this.selectedPeriodTypes[rowIndex] == PeriodType.PF) {
/* 469 */         return this.selectedPointAndFigures[rowIndex];
/*     */       }
/*     */ 
/* 472 */       return this.selectedPeriods[rowIndex];
/*     */     case 4:
/* 475 */       return this.selectedOfferSides[rowIndex];
/*     */     case 5:
/* 477 */       return this.selectedDatesFrom[rowIndex];
/*     */     case 6:
/* 479 */       return this.selectedDatesTo[rowIndex];
/*     */     case 7:
/* 481 */       return this.selectedExportFormats[rowIndex];
/* 482 */     }throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 488 */     switch (columnIndex) {
/*     */     case 0:
/* 490 */       return true;
/*     */     case 1:
/* 492 */       return false;
/*     */     case 2:
/* 494 */       return isInstrumentSelected(rowIndex);
/*     */     case 3:
/* 496 */       return isPeriodEditable(rowIndex);
/*     */     case 4:
/* 498 */       return isOfferSideEditable(rowIndex);
/*     */     case 5:
/* 500 */       return isInstrumentSelected(rowIndex);
/*     */     case 6:
/* 502 */       return isInstrumentSelected(rowIndex);
/*     */     case 7:
/* 504 */       return isInstrumentSelected(rowIndex);
/* 505 */     }throw new IllegalArgumentException("Incorrect column index : " + columnIndex);
/*     */   }
/*     */ 
/*     */   public boolean isInstrumentSelected(int rowIndex)
/*     */   {
/* 510 */     return this.selectedInstruments[rowIndex];
/*     */   }
/*     */ 
/*     */   private boolean isPeriodEditable(int rowIndex) {
/* 514 */     return (isPeriodTypeSelected(rowIndex)) && (isInstrumentSelected(rowIndex)) && (!isTickSelected(rowIndex));
/*     */   }
/*     */ 
/*     */   private boolean isPeriodSelected(int rowIndex)
/*     */   {
/* 519 */     if (this.selectedPeriodTypes[rowIndex] == PeriodType.Range) {
/* 520 */       return this.selectedPriceRanges[rowIndex] != null;
/*     */     }
/* 522 */     return this.selectedPeriods[rowIndex] != null;
/*     */   }
/*     */ 
/*     */   private boolean isTickSelected(int rowIndex)
/*     */   {
/* 527 */     boolean tickSelected = false;
/*     */ 
/* 529 */     if ((this.selectedPeriods[rowIndex] != null) && (this.selectedPeriods[rowIndex].getType() == CompositePeriod.Type.PERIOD) && (this.selectedPeriods[rowIndex].getPeriod() == Period.TICK))
/*     */     {
/* 533 */       tickSelected = true;
/*     */     }
/*     */ 
/* 536 */     return tickSelected;
/*     */   }
/*     */ 
/*     */   private boolean isPeriodTypeSelected(int rowIndex) {
/* 540 */     return this.selectedPeriodTypes[rowIndex] != null;
/*     */   }
/*     */ 
/*     */   private boolean isOfferSideEditable(int rowIndex) {
/* 544 */     return isPeriodEditable(rowIndex);
/*     */   }
/*     */ 
/*     */   public List<CompositePeriod> getPeriodsList(int rowIndex)
/*     */   {
/* 562 */     PeriodType periodType = this.selectedPeriodTypes[rowIndex];
/* 563 */     if (periodType == null) {
/* 564 */       return new ArrayList();
/*     */     }
/* 566 */     ExportFormat exportFormat = this.selectedExportFormats[rowIndex];
/* 567 */     if (exportFormat != null) {
/* 568 */       Map periodsMap = (Map)this.periodsByExport.get(exportFormat);
/* 569 */       List periods = (List)periodsMap.get(periodType);
/* 570 */       if (periods == null) {
/* 571 */         return new ArrayList();
/*     */       }
/* 573 */       return periods;
/*     */     }
/*     */ 
/* 576 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public List<PeriodType> getPeriodTypes(int rowIndex)
/*     */   {
/* 582 */     List periodTypes = null;
/* 583 */     ExportFormat exportFormat = this.selectedExportFormats[rowIndex];
/* 584 */     if (exportFormat != null) {
/* 585 */       periodTypes = (List)this.periodsTypesByExport.get(exportFormat);
/* 586 */       if (periodTypes == null)
/* 587 */         throw new IllegalStateException("periodTypes for " + exportFormat + " is null");
/*     */     }
/*     */     else {
/* 590 */       throw new IllegalStateException("exportFormat is null for rowIndex=" + rowIndex);
/*     */     }
/*     */ 
/* 593 */     return periodTypes;
/*     */   }
/*     */ 
/*     */   public PeriodType getPeriodType(int rowIndex) {
/* 597 */     return this.selectedPeriodTypes[rowIndex];
/*     */   }
/*     */ 
/*     */   public JForexPeriod getPointAndFigure(int rowIndex) {
/* 601 */     return this.selectedPointAndFigures[rowIndex];
/*     */   }
/*     */ 
/*     */   public List<PriceRange> getPriceRanges() {
/* 605 */     return this.priceRanges;
/*     */   }
/*     */ 
/*     */   private List<Period> getPeriods(Unit unit, int startUnitCount, int maxUnitCount) {
/* 609 */     List periods = Period.generateCompliantPeriods(unit, startUnitCount, maxUnitCount);
/* 610 */     return periods;
/*     */   }
/*     */ 
/*     */   private List<CompositePeriod> getCompositePeriodPeriods(Unit unit, int startUnitCount, int maxUnitCount) {
/* 614 */     List periods = getPeriods(unit, startUnitCount, maxUnitCount);
/* 615 */     return createCompositePeriods(periods);
/*     */   }
/*     */ 
/*     */   private List<Period> getPeriods(Unit unit, int maxUnitCount) {
/* 619 */     List periods = getPeriods(unit, 1, maxUnitCount);
/* 620 */     return periods;
/*     */   }
/*     */ 
/*     */   private List<CompositePeriod> getCompositePeriodPeriods(Unit unit, int maxUnitCount) {
/* 624 */     List periods = getPeriods(unit, maxUnitCount);
/* 625 */     return createCompositePeriods(periods);
/*     */   }
/*     */ 
/*     */   private List<CompositePeriod> createCompositePeriods(List<Period> periods) {
/* 629 */     List compositePeriods = new ArrayList();
/* 630 */     for (Period period : periods) {
/* 631 */       CompositePeriod compositePeriod = new CompositePeriod();
/* 632 */       compositePeriod.setType(CompositePeriod.Type.PERIOD);
/* 633 */       compositePeriod.setPeriod(period);
/* 634 */       compositePeriods.add(compositePeriod);
/*     */     }
/*     */ 
/* 637 */     return compositePeriods;
/*     */   }
/*     */ 
/*     */   private List<CompositePeriod> getCompositePeriodTickBars() {
/* 641 */     List compositePeriods = new ArrayList();
/* 642 */     List tickBars = TickBarSize.createJForexTickBarSizes();
/* 643 */     for (TickBarSize tickBarSize : tickBars) {
/* 644 */       CompositePeriod compositePeriod = new CompositePeriod();
/* 645 */       compositePeriod.setType(CompositePeriod.Type.TICKBARSIZE);
/* 646 */       compositePeriod.setTickBarSize(tickBarSize);
/* 647 */       compositePeriods.add(compositePeriod);
/*     */     }
/*     */ 
/* 650 */     return compositePeriods;
/*     */   }
/*     */ 
/*     */   public int indexOf(Instrument instrument)
/*     */   {
/* 682 */     return this.tradableInstruments.indexOf(instrument);
/*     */   }
/*     */ 
/*     */   public int getSelectedInstrumentsCount() {
/* 686 */     int count = 0;
/* 687 */     for (int i = 0; i < this.tradableInstruments.size(); i++) {
/* 688 */       if (this.selectedInstruments[i] != 0) {
/* 689 */         count++;
/*     */       }
/*     */     }
/* 692 */     return count;
/*     */   }
/*     */ 
/*     */   public List<Instrument> getTradableInstruments() {
/* 696 */     return this.tradableInstruments;
/*     */   }
/*     */ 
/*     */   public List<Instrument> getSelectedInstruments() {
/* 700 */     List instruments = new ArrayList();
/* 701 */     for (int i = 0; i < this.tradableInstruments.size(); i++) {
/* 702 */       if (this.selectedInstruments[i] != 0) {
/* 703 */         instruments.add(this.tradableInstruments.get(i));
/*     */       }
/*     */     }
/* 706 */     return instruments;
/*     */   }
/*     */ 
/*     */   public void setSelectionForAll(boolean selected) {
/* 710 */     for (int i = 0; i < this.selectedInstruments.length; i++)
/* 711 */       this.selectedInstruments[i] = selected;
/*     */   }
/*     */ 
/*     */   public Map<ExportFormat, Map<PeriodType, List<CompositePeriod>>> getPeriodsByExport()
/*     */   {
/* 716 */     return this.periodsByExport;
/*     */   }
/*     */ 
/*     */   public Map<ExportFormat, List<PeriodType>> getPeriodsTypesByExport() {
/* 720 */     return this.periodsTypesByExport;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.InstrumentSelectionTableModel
 * JD-Core Version:    0.6.0
 */