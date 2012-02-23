/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CSVWriter;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod.Type;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.DataField;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportDataParameters;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportFormat;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportInstrumentParameter;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.HSTWriter;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.IFileWriter;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.PeriodType;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.TesterDisclaimDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ExportHistoricalDataAction extends AppActionEvent
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  60 */   private static final Logger LOGGER = LoggerFactory.getLogger(ExportHistoricalDataAction.class);
/*     */ 
/*  62 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
/*     */   private ExportProcessControl exportProcessControl;
/*     */   private ExportDataParameters exportDataParameters;
/*     */   private String progressBarMessage;
/*  74 */   private boolean ioExceptionOccured = false;
/*     */   private boolean loadData;
/*  77 */   private final long ONE_HOUR = 3600000L;
/*  78 */   private int currentInstrumentIndex = 0;
/*  79 */   private int instrumentCount = 0;
/*  80 */   private long halfAnHourCount = 0L;
/*  81 */   private long daysBetween = 0L;
/*  82 */   private boolean newInstrument = true;
/*  83 */   private long tempTime = 0L;
/*  84 */   private int currentStep = 0;
/*     */ 
/*  86 */   private DataType dataType = null;
/*     */ 
/*     */   public ExportHistoricalDataAction(Object source, JPanel mainPanel, ExportProcessControl exportProcessControl, ExportDataParameters exportDataParameters)
/*     */   {
/*  94 */     super(source, true, true);
/*  95 */     this.exportProcessControl = exportProcessControl;
/*  96 */     this.exportDataParameters = exportDataParameters;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/* 102 */     this.loadData = true;
/*     */     try
/*     */     {
/* 105 */       SwingUtilities.invokeAndWait(new Runnable() {
/*     */         public void run() {
/* 107 */           if (!TesterDisclaimDialog.isAcceptState()) {
/* 108 */             TesterDisclaimDialog disclaimer = TesterDisclaimDialog.getInstance();
/* 109 */             disclaimer.showDialog();
/* 110 */             if (!disclaimer.isAccepted())
/* 111 */               ExportHistoricalDataAction.access$002(ExportHistoricalDataAction.this, false);
/*     */           }
/*     */         } } );
/*     */     } catch (Exception ex) {
/* 116 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */ 
/* 119 */     if (!this.loadData) {
/* 120 */       cancel();
/* 121 */       return;
/*     */     }
/*     */ 
/* 124 */     this.currentInstrumentIndex = 0;
/* 125 */     this.instrumentCount = 0;
/* 126 */     this.halfAnHourCount = 0L;
/* 127 */     this.currentStep = 0;
/* 128 */     this.tempTime = 0L;
/*     */ 
/* 130 */     this.progressBarMessage = LocalizationManager.getText("hdm.export.progress");
/*     */ 
/* 132 */     setTimeZone();
/* 133 */     validateExportParameters();
/* 134 */     if (this.exportProcessControl.isCanceled()) {
/* 135 */       return;
/*     */     }
/*     */ 
/* 138 */     List exportInstrumentParameters = this.exportDataParameters.getExportInstrumentParameters();
/* 139 */     resetErrors(exportInstrumentParameters);
/*     */ 
/* 141 */     this.instrumentCount = exportInstrumentParameters.size();
/*     */ 
/* 143 */     for (ExportInstrumentParameter parameter : exportInstrumentParameters) {
/* 144 */       this.daysBetween += daysBetween(parameter.getDateTo().longValue(), parameter.getDateFrom().longValue());
/*     */     }
/* 146 */     this.halfAnHourCount = (this.daysBetween * 48L);
/*     */ 
/* 148 */     for (ExportInstrumentParameter parameter : exportInstrumentParameters) {
/* 149 */       if ((this.exportProcessControl.isCanceled()) || (this.ioExceptionOccured)) {
/* 150 */         return; } 
/*     */ this.currentInstrumentIndex += 1;
/* 154 */       this.newInstrument = true;
/*     */ 
/* 156 */       detectDataType(parameter);
/* 157 */       String fullFileName = getFullFileName(parameter);
/*     */       IFileWriter fileWriter;
/*     */       try { switch (10.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$ExportFormat[parameter.getExportFormat().ordinal()]) {
/*     */         case 1:
/* 164 */           fileWriter = new CSVWriter(this.exportDataParameters, fullFileName, this.dataType);
/* 165 */           break;
/*     */         case 2:
/* 167 */           fileWriter = new HSTWriter(fullFileName, parameter.getInstrument(), parameter.getCompositePeriod().getPeriod());
/* 168 */           break;
/*     */         default:
/* 169 */           throw new IllegalArgumentException(new StringBuilder().append("Incorrect export format : ").append(parameter.getExportFormat()).toString());
/*     */         }
/*     */       } catch (Exception e) {
/* 172 */         LOGGER.error(e.getMessage(), e);
/* 173 */         cancel();
/* 174 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 178 */         fileWriter.writeHeader();
/*     */       } catch (IOException e) {
/* 180 */         LOGGER.error(e.getMessage(), e);
/* 181 */         SwingUtilities.invokeLater(new Runnable(e) {
/*     */           public void run() {
/* 183 */             JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { this.val$e.getMessage() }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */           }
/*     */         });
/* 191 */         cancel();
/* 192 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 196 */         IFeedDataProvider feedDataProvider = (IFeedDataProvider)GreedContext.get("feedDataProvider");
/* 197 */         LiveFeedListener liveFeedListener = new LiveFeedListener(fileWriter)
/*     */         {
/*     */           public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */           {
/*     */             try
/*     */             {
/* 208 */               this.val$fileWriter.writeTickRateInfo(new TickData(time, ask, bid, askVol, bidVol));
/* 209 */               if (ExportHistoricalDataAction.this.newInstrument) {
/* 210 */                 ExportHistoricalDataAction.access$102(ExportHistoricalDataAction.this, false);
/* 211 */                 ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, time + 1800000L);
/*     */               }
/*     */ 
/* 214 */               if (ExportHistoricalDataAction.this.tempTime <= time) {
/* 215 */                 ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, time + 1800000L);
/* 216 */                 ExportHistoricalDataAction.access$308(ExportHistoricalDataAction.this);
/* 217 */                 ExportHistoricalDataAction.this.updateProgressBar(instrument);
/*     */               }
/*     */             } catch (IOException e) {
/* 220 */               ExportHistoricalDataAction.LOGGER.error(e.getMessage(), e);
/* 221 */               ExportHistoricalDataAction.access$602(ExportHistoricalDataAction.this, true);
/*     */             }
/*     */           }
/*     */ 
/*     */           public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */           {
/*     */             try
/*     */             {
/* 238 */               this.val$fileWriter.writeCandleRateInfo(new CandleData(time, open, close, low, high, vol));
/*     */ 
/* 240 */               if (ExportHistoricalDataAction.this.newInstrument) {
/* 241 */                 ExportHistoricalDataAction.access$102(ExportHistoricalDataAction.this, false);
/* 242 */                 ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, time + 1800000L);
/*     */               }
/*     */ 
/* 245 */               if (ExportHistoricalDataAction.this.tempTime <= time) {
/* 246 */                 ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, time + 1800000L);
/* 247 */                 ExportHistoricalDataAction.access$308(ExportHistoricalDataAction.this);
/* 248 */                 ExportHistoricalDataAction.this.updateProgressBar(instrument);
/*     */               }
/*     */             } catch (IOException e) {
/* 251 */               ExportHistoricalDataAction.LOGGER.error(e.getMessage(), e);
/* 252 */               ExportHistoricalDataAction.access$602(ExportHistoricalDataAction.this, true);
/*     */             }
/*     */           }
/*     */         };
/* 256 */         LoadingProgressListener loadingProgressListener = new LoadingProgressListener()
/*     */         {
/*     */           public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */           {
/*     */           }
/*     */ 
/*     */           public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex)
/*     */           {
/* 272 */             if ((!allDataLoaded) && (ex != null))
/* 273 */               ExportHistoricalDataAction.LOGGER.error(ex.getMessage(), ex);
/*     */           }
/*     */ 
/*     */           public boolean stopJob()
/*     */           {
/* 278 */             return (ExportHistoricalDataAction.this.exportProcessControl.isCanceled()) || (ExportHistoricalDataAction.this.ioExceptionOccured);
/*     */           } } ;
/*     */         try {
/* 282 */           if (this.dataType == DataType.PRICE_RANGE_AGGREGATION) {
/* 283 */             IPriceRangeLiveFeedListener priceRangeLiveFeedListener = new IPriceRangeLiveFeedListener(fileWriter, parameter)
/*     */             {
/*     */               public void newPriceData(PriceRangeData priceRange)
/*     */               {
/*     */                 try
/*     */                 {
/* 289 */                   this.val$fileWriter.writePriceRangeInfo(priceRange);
/*     */ 
/* 291 */                   if (ExportHistoricalDataAction.this.newInstrument) {
/* 292 */                     ExportHistoricalDataAction.access$102(ExportHistoricalDataAction.this, false);
/* 293 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, priceRange.getTime() + 1800000L);
/*     */                   }
/*     */ 
/* 296 */                   if (ExportHistoricalDataAction.this.tempTime <= priceRange.getTime())
/*     */                   {
/* 298 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, priceRange.getTime() + 1800000L);
/* 299 */                     ExportHistoricalDataAction.access$308(ExportHistoricalDataAction.this);
/* 300 */                     ExportHistoricalDataAction.this.updateProgressBar(this.val$parameter.getInstrument());
/*     */                   }
/*     */                 }
/*     */                 catch (IOException e) {
/* 304 */                   ExportHistoricalDataAction.LOGGER.error(e.getMessage(), e);
/* 305 */                   ExportHistoricalDataAction.access$602(ExportHistoricalDataAction.this, true);
/*     */                 }
/*     */               }
/*     */             };
/* 311 */             feedDataProvider.getPriceAggregationDataProvider().loadPriceRangeTimeIntervalSynched(parameter.getInstrument(), parameter.getOfferSide(), parameter.getPriceRange(), parameter.getDateFrom().longValue(), parameter.getDateTo().longValue(), priceRangeLiveFeedListener, loadingProgressListener, true);
/*     */           }
/* 322 */           else if (this.dataType == DataType.TICKS) {
/* 323 */             feedDataProvider.loadTicksDataSynched(parameter.getInstrument(), parameter.getDateFrom().longValue(), parameter.getDateTo().longValue(), liveFeedListener, loadingProgressListener);
/*     */           }
/* 330 */           else if (this.dataType == DataType.TIME_PERIOD_AGGREGATION) {
/* 331 */             feedDataProvider.loadCandlesFromToSynched(parameter.getInstrument(), parameter.getCompositePeriod().getPeriod(), parameter.getOfferSide(), parameter.getFilter(), parameter.getDateFrom().longValue(), parameter.getDateTo().longValue(), liveFeedListener, loadingProgressListener);
/*     */           }
/* 342 */           else if (this.dataType == DataType.TICK_BAR)
/*     */           {
/* 344 */             ITickBarLiveFeedListener tickBarLiveFeedListener = new ITickBarLiveFeedListener(fileWriter, parameter)
/*     */             {
/*     */               public void newPriceData(TickBarData tickBar) {
/*     */                 try {
/* 348 */                   this.val$fileWriter.writeTickBarInfo(tickBar);
/*     */ 
/* 350 */                   if (ExportHistoricalDataAction.this.newInstrument) {
/* 351 */                     ExportHistoricalDataAction.access$102(ExportHistoricalDataAction.this, false);
/* 352 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, tickBar.getTime() + 1800000L);
/*     */                   }
/*     */ 
/* 355 */                   if (ExportHistoricalDataAction.this.tempTime <= tickBar.getTime()) {
/* 356 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, tickBar.getTime() + 1800000L);
/* 357 */                     ExportHistoricalDataAction.access$308(ExportHistoricalDataAction.this);
/* 358 */                     ExportHistoricalDataAction.this.updateProgressBar(this.val$parameter.getInstrument());
/*     */                   }
/*     */                 }
/*     */                 catch (IOException ex) {
/* 362 */                   ExportHistoricalDataAction.LOGGER.error(ex.getMessage(), ex);
/* 363 */                   ExportHistoricalDataAction.access$602(ExportHistoricalDataAction.this, true);
/*     */                 }
/*     */               }
/*     */             };
/* 369 */             feedDataProvider.getPriceAggregationDataProvider().loadTickBarTimeIntervalSynched(parameter.getInstrument(), parameter.getOfferSide(), parameter.getCompositePeriod().getTickBarSize(), parameter.getDateFrom().longValue(), parameter.getDateTo().longValue(), tickBarLiveFeedListener, loadingProgressListener, true);
/*     */           }
/* 380 */           else if (this.dataType == DataType.POINT_AND_FIGURE) {
/* 381 */             IPointAndFigureLiveFeedListener pointAndFigureLiveFeedListener = new IPointAndFigureLiveFeedListener(fileWriter, parameter)
/*     */             {
/*     */               public void newPriceData(PointAndFigureData pointAndFigure)
/*     */               {
/*     */                 try {
/* 386 */                   this.val$fileWriter.writePointAndFigureInfo(pointAndFigure);
/*     */ 
/* 388 */                   if (ExportHistoricalDataAction.this.newInstrument) {
/* 389 */                     ExportHistoricalDataAction.access$102(ExportHistoricalDataAction.this, false);
/* 390 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, pointAndFigure.getTime() + 1800000L);
/*     */                   }
/*     */ 
/* 393 */                   if (ExportHistoricalDataAction.this.tempTime <= pointAndFigure.getTime()) {
/* 394 */                     ExportHistoricalDataAction.access$202(ExportHistoricalDataAction.this, pointAndFigure.getTime() + 1800000L);
/* 395 */                     ExportHistoricalDataAction.access$308(ExportHistoricalDataAction.this);
/* 396 */                     ExportHistoricalDataAction.this.updateProgressBar(this.val$parameter.getInstrument());
/*     */                   }
/*     */                 }
/*     */                 catch (IOException ex) {
/* 400 */                   ExportHistoricalDataAction.LOGGER.error(ex.getMessage(), ex);
/* 401 */                   ExportHistoricalDataAction.access$602(ExportHistoricalDataAction.this, true);
/*     */                 }
/*     */               }
/*     */             };
/* 407 */             feedDataProvider.getPriceAggregationDataProvider().loadPointAndFigureTimeIntervalSynched(parameter.getInstrument(), parameter.getOfferSide(), parameter.getPointAndFigure().getPriceRange(), parameter.getPointAndFigure().getReversalAmount(), parameter.getDateFrom().longValue(), parameter.getDateTo().longValue(), pointAndFigureLiveFeedListener, loadingProgressListener, true);
/*     */           }
/*     */ 
/* 420 */           if (this.ioExceptionOccured) {
/* 421 */             SwingUtilities.invokeLater(new Runnable() {
/*     */               public void run() {
/* 423 */                 JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { "system error" }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */               }
/*     */ 
/*     */             });
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 433 */           LOGGER.error(e.getMessage(), e);
/* 434 */           SwingUtilities.invokeLater(new Runnable(e) {
/*     */             public void run() {
/* 436 */               JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { this.val$e.getMessage() }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */             }
/*     */           });
/* 444 */           cancel();
/*     */         }
/*     */       } finally {
/*     */         try {
/* 448 */           fileWriter.close();
/*     */         } catch (IOException e) {
/* 450 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void detectDataType(ExportInstrumentParameter parameter) {
/* 457 */     this.dataType = null;
/* 458 */     boolean compositePeriodValid = false;
/* 459 */     if ((parameter.getCompositePeriod() != null) && (parameter.getCompositePeriod().isValid()))
/*     */     {
/* 462 */       compositePeriodValid = true;
/*     */     }
/*     */ 
/* 465 */     if ((compositePeriodValid) && (parameter.getCompositePeriod().getType() == CompositePeriod.Type.PERIOD) && (parameter.getCompositePeriod().getPeriod() == Period.TICK))
/*     */     {
/* 469 */       this.dataType = DataType.TICKS;
/*     */     }
/* 471 */     else if ((compositePeriodValid) && (parameter.getPeriodType() == PeriodType.Ticks) && (parameter.getCompositePeriod().getType() == CompositePeriod.Type.TICKBARSIZE))
/*     */     {
/* 475 */       this.dataType = DataType.TICK_BAR;
/*     */     }
/* 477 */     else if (parameter.getPeriodType() == PeriodType.Range)
/*     */     {
/* 479 */       this.dataType = DataType.PRICE_RANGE_AGGREGATION;
/*     */     }
/* 481 */     else if (parameter.getPeriodType() == PeriodType.PF)
/*     */     {
/* 483 */       this.dataType = DataType.POINT_AND_FIGURE;
/*     */     }
/*     */     else {
/* 486 */       this.dataType = DataType.TIME_PERIOD_AGGREGATION;
/*     */     }
/*     */ 
/* 489 */     if (this.dataType == null) throw new IllegalStateException("The aggregation type is not detected"); 
/*     */   }
/*     */ 
/*     */   private void validateExportParameters()
/*     */   {
/* 493 */     boolean dataIsValid = true;
/*     */ 
/* 495 */     List exportInstrumentParameters = this.exportDataParameters.getExportInstrumentParameters();
/*     */ 
/* 497 */     if (exportInstrumentParameters.size() == 0)
/*     */     {
/* 504 */       dataIsValid = false;
/*     */     }
/*     */ 
/* 507 */     for (ExportInstrumentParameter exportInstrumentParameter : exportInstrumentParameters)
/*     */     {
/* 509 */       if (exportInstrumentParameter.getPeriodType() == null) {
/* 510 */         this.exportProcessControl.onValidated(DataField.EXPORT_PERIOD_TYPE, true, exportInstrumentParameter.getInstrument(), 2, "");
/*     */ 
/* 517 */         dataIsValid = false;
/* 518 */         continue;
/*     */       }
/*     */ 
/* 521 */       detectDataType(exportInstrumentParameter);
/*     */ 
/* 523 */       if (!validateDates(exportInstrumentParameter)) {
/* 524 */         dataIsValid = false;
/*     */       }
/*     */ 
/* 528 */       if (((this.dataType == DataType.TICKS) || (this.dataType == DataType.TICK_BAR) || (this.dataType == DataType.TIME_PERIOD_AGGREGATION)) && 
/* 529 */         (exportInstrumentParameter.getCompositePeriod() == null)) {
/* 530 */         this.exportProcessControl.onValidated(DataField.EXPORT_PERIOD, true, exportInstrumentParameter.getInstrument(), 3, "");
/*     */ 
/* 537 */         dataIsValid = false;
/*     */       }
/*     */ 
/* 541 */       if ((this.dataType == DataType.PRICE_RANGE_AGGREGATION) && 
/* 542 */         (exportInstrumentParameter.getPriceRange() == null)) {
/* 543 */         this.exportProcessControl.onValidated(DataField.EXPORT_PERIOD, true, exportInstrumentParameter.getInstrument(), 3, "");
/*     */ 
/* 550 */         dataIsValid = false;
/*     */       }
/*     */ 
/* 554 */       if (this.dataType == DataType.POINT_AND_FIGURE) {
/* 555 */         JForexPeriod pointAndFigure = exportInstrumentParameter.getPointAndFigure();
/* 556 */         if (pointAndFigure == null) {
/* 557 */           dataIsValid = false;
/* 558 */           throw new IllegalArgumentException("Box size(pips) and Reversal are null");
/*     */         }
/* 560 */         if (pointAndFigure.getPriceRange() == null) {
/* 561 */           dataIsValid = false;
/* 562 */           throw new IllegalArgumentException("Box size(pips) is null");
/*     */         }
/* 564 */         if (pointAndFigure.getReversalAmount() == null) {
/* 565 */           dataIsValid = false;
/* 566 */           throw new IllegalArgumentException("Reversal amount is null");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 571 */       if ((this.dataType == DataType.TICK_BAR) || (this.dataType == DataType.TIME_PERIOD_AGGREGATION) || (this.dataType == DataType.PRICE_RANGE_AGGREGATION) || (this.dataType == DataType.POINT_AND_FIGURE))
/*     */       {
/* 576 */         if (exportInstrumentParameter.getOfferSide() == null) {
/* 577 */           this.exportProcessControl.onValidated(DataField.EXPORT_OFFERSIDE, true, exportInstrumentParameter.getInstrument(), 4, "");
/*     */ 
/* 584 */           dataIsValid = false;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 590 */     boolean exportDirectoryValid = true;
/* 591 */     if ((this.exportDataParameters.getOutputDirectory() == null) || (this.exportDataParameters.getOutputDirectory().length() == 0)) {
/* 592 */       this.exportProcessControl.onValidated(DataField.EXPORT_DIRECTORY, true, null, -1, "");
/*     */ 
/* 599 */       dataIsValid = false;
/* 600 */       exportDirectoryValid = false;
/*     */     }
/*     */ 
/* 603 */     if (!new File(this.exportDataParameters.getOutputDirectory()).exists()) {
/* 604 */       this.exportProcessControl.onValidated(DataField.EXPORT_DIRECTORY, true, null, -1, "");
/*     */ 
/* 611 */       dataIsValid = false;
/* 612 */       exportDirectoryValid = false;
/*     */     }
/*     */ 
/* 615 */     if (exportDirectoryValid) {
/* 616 */       this.exportProcessControl.onValidated(DataField.EXPORT_DIRECTORY, false, null, -1, "");
/*     */     }
/*     */ 
/* 626 */     if (!dataIsValid)
/* 627 */       cancel();
/*     */   }
/*     */ 
/*     */   private boolean validateDates(ExportInstrumentParameter exportInstrumentParameter)
/*     */   {
/* 632 */     boolean dataIsValid = true;
/*     */ 
/* 634 */     Long from = exportInstrumentParameter.getDateFrom();
/* 635 */     Long to = exportInstrumentParameter.getDateTo();
/*     */ 
/* 637 */     if ((from == null) || (from.longValue() == -9223372036854775808L)) {
/* 638 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, true, exportInstrumentParameter.getInstrument(), 5, "");
/*     */ 
/* 645 */       dataIsValid = false;
/*     */     }
/* 647 */     if ((to == null) || (to.longValue() == -9223372036854775808L)) {
/* 648 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, true, exportInstrumentParameter.getInstrument(), 6, "");
/*     */ 
/* 655 */       dataIsValid = false;
/*     */     }
/*     */ 
/* 658 */     if ((from != null) && (to != null) && (from.longValue() == to.longValue()))
/*     */     {
/* 662 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, true, exportInstrumentParameter.getInstrument(), 5, "");
/*     */ 
/* 669 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, true, exportInstrumentParameter.getInstrument(), 6, "");
/*     */ 
/* 676 */       dataIsValid = false;
/*     */     }
/*     */ 
/* 679 */     if ((from != null) && (to != null) && (from.longValue() > to.longValue())) {
/* 680 */       String fromStr = DATE_FORMAT.format(new Long(from.longValue()));
/* 681 */       String toStr = DATE_FORMAT.format(new Long(to.longValue()));
/* 682 */       String errorMessage = new StringBuilder().append("Requested time interval from ").append(fromStr).append(" to ").append(toStr).append(" GMT is not valid").toString();
/*     */ 
/* 684 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, true, exportInstrumentParameter.getInstrument(), 5, errorMessage);
/*     */ 
/* 691 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, true, exportInstrumentParameter.getInstrument(), 6, errorMessage);
/*     */ 
/* 698 */       dataIsValid = false;
/*     */     }
/*     */ 
/* 702 */     if (!dataIsValid) {
/* 703 */       return dataIsValid;
/*     */     }
/*     */ 
/* 706 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 707 */     IIntraperiodBarsGenerator barsGenerator = feedDataProvider.getIntraperiodBarsGenerator();
/*     */ 
/* 709 */     Period period = exportInstrumentParameter.getCompositePeriod() == null ? null : exportInstrumentParameter.getCompositePeriod().getPeriod();
/* 710 */     Instrument instrument = exportInstrumentParameter.getInstrument();
/*     */ 
/* 713 */     long firstKnownTime = from.longValue();
/*     */ 
/* 715 */     if (this.dataType == DataType.TICKS) {
/* 716 */       firstKnownTime = feedDataProvider.getTimeOfFirstTick(instrument);
/*     */     }
/* 718 */     else if (this.dataType == DataType.TICK_BAR) {
/* 719 */       firstKnownTime = feedDataProvider.getTimeOfFirstBar(instrument, exportInstrumentParameter.getCompositePeriod().getTickBarSize());
/*     */     }
/* 721 */     else if (this.dataType == DataType.TIME_PERIOD_AGGREGATION) {
/* 722 */       firstKnownTime = feedDataProvider.getTimeOfFirstCandle(instrument, period);
/*     */     }
/* 724 */     else if (this.dataType == DataType.POINT_AND_FIGURE) {
/* 725 */       firstKnownTime = feedDataProvider.getTimeOfFirstBar(instrument, exportInstrumentParameter.getPointAndFigure().getPriceRange(), exportInstrumentParameter.getPointAndFigure().getReversalAmount());
/*     */     }
/* 729 */     else if (this.dataType == DataType.PRICE_RANGE_AGGREGATION) {
/* 730 */       firstKnownTime = feedDataProvider.getTimeOfFirstBar(instrument, exportInstrumentParameter.getPriceRange());
/*     */     }
/*     */ 
/* 733 */     if (from.longValue() < firstKnownTime) {
/* 734 */       from = Long.valueOf(firstKnownTime);
/*     */     }
/*     */ 
/* 737 */     long lastKnownTime = feedDataProvider.getLastTickTime(instrument);
/*     */ 
/* 739 */     if (to.longValue() > lastKnownTime) {
/* 740 */       to = Long.valueOf(lastKnownTime);
/*     */     }
/*     */ 
/* 743 */     if (this.dataType == DataType.TIME_PERIOD_AGGREGATION)
/*     */     {
/* 746 */       long firstCandleTime = DataCacheUtils.getCandleStartFast(period, from.longValue());
/* 747 */       if (firstCandleTime != from.longValue()) {
/* 748 */         from = Long.valueOf(firstCandleTime);
/*     */       }
/*     */ 
/* 752 */       long toCandleStart = DataCacheUtils.getCandleStartFast(period, to.longValue());
/* 753 */       long inProgressCandleStart = DataCacheUtils.getCandleStartFast(period, lastKnownTime);
/*     */ 
/* 755 */       if (toCandleStart >= inProgressCandleStart) {
/* 756 */         to = Long.valueOf(DataCacheUtils.getPreviousCandleStartFast(period, inProgressCandleStart));
/*     */       }
/*     */       else {
/* 759 */         to = Long.valueOf(toCandleStart);
/*     */       }
/*     */     }
/* 762 */     else if (this.dataType == DataType.TICK_BAR)
/*     */     {
/* 764 */       TickBarData tickBarData = barsGenerator.getOrLoadInProgressTickBar(instrument, exportInstrumentParameter.getOfferSide(), exportInstrumentParameter.getCompositePeriod().getTickBarSize());
/*     */ 
/* 767 */       if (to.longValue() >= tickBarData.getTime()) {
/* 768 */         to = Long.valueOf(DataCacheUtils.getPreviousPriceAggregationBarStart(tickBarData.getTime()));
/*     */       }
/*     */ 
/*     */     }
/* 772 */     else if (this.dataType == DataType.PRICE_RANGE_AGGREGATION) {
/* 773 */       PriceRangeData priceRangeData = barsGenerator.getOrLoadInProgressPriceRange(instrument, exportInstrumentParameter.getOfferSide(), exportInstrumentParameter.getPriceRange());
/*     */ 
/* 776 */       if (to.longValue() >= priceRangeData.getTime()) {
/* 777 */         to = Long.valueOf(DataCacheUtils.getPreviousPriceAggregationBarStart(priceRangeData.getTime()));
/*     */       }
/*     */     }
/* 780 */     else if (this.dataType == DataType.POINT_AND_FIGURE) {
/* 781 */       PointAndFigureData pafData = barsGenerator.getOrLoadInProgressPointAndFigure(instrument, exportInstrumentParameter.getOfferSide(), exportInstrumentParameter.getPointAndFigure().getPriceRange(), exportInstrumentParameter.getPointAndFigure().getReversalAmount());
/*     */ 
/* 785 */       if (to.longValue() >= pafData.getTime()) {
/* 786 */         to = Long.valueOf(DataCacheUtils.getPreviousPriceAggregationBarStart(pafData.getTime()));
/*     */       }
/*     */     }
/*     */ 
/* 790 */     exportInstrumentParameter.setDateFrom(from);
/* 791 */     exportInstrumentParameter.setDateTo(to);
/*     */     try
/*     */     {
/* 794 */       if ((this.dataType == DataType.TIME_PERIOD_AGGREGATION) && 
/* 795 */         (from != null) && (to != null) && (exportInstrumentParameter.getCompositePeriod() != null) && (exportInstrumentParameter.getCompositePeriod().getPeriod() != null) && (!DataCacheUtils.isIntervalValid(exportInstrumentParameter.getCompositePeriod().getPeriod(), from.longValue(), to.longValue())))
/*     */       {
/* 801 */         this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, true, exportInstrumentParameter.getInstrument(), 5, "");
/*     */ 
/* 808 */         this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, true, exportInstrumentParameter.getInstrument(), 6, "");
/*     */ 
/* 815 */         dataIsValid = false;
/*     */       }
/*     */     }
/*     */     catch (DataCacheException e) {
/* 819 */       LOGGER.error(e.getMessage(), e);
/* 820 */       dataIsValid = false;
/*     */     }
/*     */ 
/* 823 */     if (dataIsValid) {
/* 824 */       resetDateError(exportInstrumentParameter);
/*     */     }
/*     */ 
/* 827 */     return dataIsValid;
/*     */   }
/*     */ 
/*     */   private String getFileName(ExportInstrumentParameter parameter)
/*     */   {
/* 834 */     SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
/* 835 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 837 */     StringBuilder result = new StringBuilder();
/* 838 */     if (parameter.getInstrument() != null) {
/* 839 */       result.append(parameter.getInstrument().name());
/*     */     }
/*     */ 
/* 842 */     result.append("_");
/* 843 */     if (this.dataType == DataType.PRICE_RANGE_AGGREGATION) {
/* 844 */       result.append(new StringBuilder().append("Range_").append(parameter.getPriceRange().toString()).toString());
/* 845 */     } else if ((this.dataType == DataType.TICKS) || (this.dataType == DataType.TIME_PERIOD_AGGREGATION)) {
/* 846 */       result.append(parameter.getCompositePeriod().getPeriod());
/* 847 */     } else if (this.dataType == DataType.TICK_BAR)
/*     */     {
/* 849 */       result.append(new StringBuilder().append("TickBar_").append(parameter.getCompositePeriod().getTickBarSize().toString()).toString());
/* 850 */     } else if (this.dataType == DataType.POINT_AND_FIGURE)
/*     */     {
/* 852 */       JForexPeriod pointAndFigure = parameter.getPointAndFigure();
/* 853 */       result.append(new StringBuilder().append("PF_").append(pointAndFigure.getPriceRange()).append("_").append(pointAndFigure.getReversalAmount()).toString());
/*     */     }
/*     */ 
/* 856 */     if (parameter.getOfferSide() != null) {
/* 857 */       result.append("_");
/* 858 */       result.append(parameter.getOfferSide().toString());
/*     */     }
/*     */ 
/* 861 */     result.append("_");
/* 862 */     result.append(format.format(parameter.getDateFrom()));
/* 863 */     result.append("_");
/* 864 */     result.append(format.format(parameter.getDateTo()));
/*     */ 
/* 866 */     if (parameter.getExportFormat() == ExportFormat.CSV) {
/* 867 */       result.append(".csv");
/*     */     }
/* 869 */     if (parameter.getExportFormat() == ExportFormat.HST) {
/* 870 */       result.append(".hst");
/*     */     }
/*     */ 
/* 873 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore()
/*     */   {
/* 878 */     this.exportProcessControl.onStart();
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 883 */     if (this.exportProcessControl.isCanceled())
/* 884 */       this.exportProcessControl.onCanceled();
/*     */     else
/* 886 */       this.exportProcessControl.onFinished();
/*     */   }
/*     */ 
/*     */   private void cancel()
/*     */   {
/* 891 */     this.exportProcessControl.cancel();
/*     */   }
/*     */ 
/*     */   private void setTimeZone() {
/* 895 */     List exportInstrumentParameters = this.exportDataParameters.getExportInstrumentParameters();
/* 896 */     for (ExportInstrumentParameter exportInstrumentParameter : exportInstrumentParameters) {
/* 897 */       Long from = exportInstrumentParameter.getDateFrom();
/* 898 */       Long to = exportInstrumentParameter.getDateTo();
/* 899 */       if ((from != null) && (to != null) && (from.longValue() != -9223372036854775808L) && (to.longValue() != -9223372036854775808L)) {
/* 900 */         Calendar lCal = Calendar.getInstance();
/* 901 */         lCal.setTimeInMillis(from.longValue());
/* 902 */         Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 903 */         gmtCalendar.set(14, 0);
/* 904 */         gmtCalendar.set(lCal.get(1), lCal.get(2), lCal.get(5), 0, 0, 0);
/* 905 */         from = Long.valueOf(gmtCalendar.getTimeInMillis());
/* 906 */         lCal.setTimeInMillis(to.longValue());
/* 907 */         gmtCalendar.set(lCal.get(1), lCal.get(2), lCal.get(5), 0, 0, 0);
/* 908 */         to = Long.valueOf(gmtCalendar.getTimeInMillis());
/*     */ 
/* 910 */         exportInstrumentParameter.setDateFrom(from);
/* 911 */         exportInstrumentParameter.setDateTo(to);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private long daysBetween(long from, long to) {
/* 917 */     return (from - to) / 86400000L;
/*     */   }
/*     */ 
/*     */   private void updateProgressBar(Instrument instrument) {
/* 921 */     String message = MessageFormat.format(this.progressBarMessage, new Object[] { instrument, new StringBuilder().append(this.currentInstrumentIndex).append("").toString(), new StringBuilder().append(this.instrumentCount).append("").toString() });
/*     */ 
/* 927 */     int step = (int)(this.currentStep / this.halfAnHourCount * 100.0D);
/* 928 */     this.exportProcessControl.onProgressChanged(step, message);
/*     */   }
/*     */ 
/*     */   private void resetErrors(List<ExportInstrumentParameter> exportInstrumentParameters) {
/* 932 */     for (ExportInstrumentParameter exportInstrumentParameter : exportInstrumentParameters) {
/* 933 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, false, exportInstrumentParameter.getInstrument(), 5, "");
/*     */ 
/* 940 */       this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, false, exportInstrumentParameter.getInstrument(), 6, "");
/*     */     }
/*     */ 
/* 948 */     this.exportProcessControl.onValidated(DataField.EXPORT_DIRECTORY, false, null, -1, "");
/*     */   }
/*     */ 
/*     */   private void resetDateError(ExportInstrumentParameter exportInstrumentParameter)
/*     */   {
/* 958 */     this.exportProcessControl.onValidated(DataField.EXPORT_DATE_FROM, false, exportInstrumentParameter.getInstrument(), 5, "");
/*     */ 
/* 965 */     this.exportProcessControl.onValidated(DataField.EXPORT_DATE_TO, false, exportInstrumentParameter.getInstrument(), 6, "");
/*     */   }
/*     */ 
/*     */   private String getFullFileName(ExportInstrumentParameter parameter)
/*     */   {
/* 975 */     String tempPath = this.exportDataParameters.getOutputDirectory();
/* 976 */     String fullFileName = getFileName(parameter);
/*     */ 
/* 978 */     if (!tempPath.endsWith(File.separator)) {
/* 979 */       tempPath = new StringBuilder().append(tempPath).append(File.separator).toString();
/*     */     }
/* 981 */     fullFileName = new StringBuilder().append(tempPath).append(fullFileName).toString();
/*     */ 
/* 983 */     return fullFileName;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  65 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ExportHistoricalDataAction
 * JD-Core Version:    0.6.0
 */