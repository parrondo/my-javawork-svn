/*     */ package com.dukascopy.charts.listeners.datachange;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataOperationManager;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.DataChangeListenerAdapter;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class MainDataChangeListener extends DataChangeListenerAdapter
/*     */ {
/*     */   private Instrument instrument;
/*     */   private final ChartState chartState;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private final IDataOperationManager dataOperationManager;
/*     */   private final ProgressController progressController;
/*     */ 
/*     */   public MainDataChangeListener(Instrument instrument, ChartState chartState, GuiRefresher guiRefresher, IDataOperationManager dataOperationManager, ProgressController progressController)
/*     */   {
/*  32 */     this.instrument = instrument;
/*  33 */     this.chartState = chartState;
/*  34 */     this.guiRefresher = guiRefresher;
/*  35 */     this.dataOperationManager = dataOperationManager;
/*  36 */     this.progressController = progressController;
/*     */   }
/*     */ 
/*     */   public void dataChanged(long from, long to, Period period, OfferSide offerSide)
/*     */   {
/*  41 */     if (this.chartState.getPeriod() != period) {
/*  42 */       return;
/*     */     }
/*  44 */     if ((this.chartState.getOfferSide() != null) && (this.chartState.getOfferSide() != offerSide)) {
/*  45 */       return;
/*     */     }
/*     */ 
/*  48 */     SwingUtilities.invokeLater(new Runnable(from, to) {
/*     */       public void run() {
/*  50 */         boolean shouldBeRefreshed = MainDataChangeListener.this.dataOperationManager.dataChanged(this.val$from, this.val$to);
/*  51 */         if (shouldBeRefreshed)
/*  52 */           MainDataChangeListener.this.guiRefresher.refreshAllContent();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(Period period, int id)
/*     */   {
/*  60 */     SwingUtilities.invokeLater(new Runnable(id) {
/*     */       public void run() {
/*  62 */         if (MainDataChangeListener.this.guiRefresher.isIndicatorShownOnSubWindow(this.val$id)) {
/*  63 */           MainDataChangeListener.this.dataOperationManager.subIndicatorAdded(this.val$id);
/*  64 */           MainDataChangeListener.this.guiRefresher.refreshSubContentByIndicatorId(Integer.valueOf(this.val$id));
/*     */         } else {
/*  66 */           MainDataChangeListener.this.dataOperationManager.indicatorAdded(this.val$id);
/*  67 */           MainDataChangeListener.this.guiRefresher.refreshMainContent();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(Period period, int id) {
/*  75 */     SwingUtilities.invokeLater(new Runnable(id) {
/*     */       public void run() {
/*  77 */         if (MainDataChangeListener.this.guiRefresher.isIndicatorShownOnSubWindow(this.val$id)) {
/*  78 */           MainDataChangeListener.this.dataOperationManager.subIndicatorEdited(this.val$id);
/*  79 */           MainDataChangeListener.this.guiRefresher.refreshSubContentByIndicatorId(Integer.valueOf(this.val$id));
/*     */         } else {
/*  81 */           MainDataChangeListener.this.dataOperationManager.indicatorEdited(this.val$id);
/*  82 */           MainDataChangeListener.this.guiRefresher.refreshMainContent();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(Period period, int id) {
/*  90 */     SwingUtilities.invokeLater(new Runnable(id) {
/*     */       public void run() {
/*  92 */         if (!MainDataChangeListener.this.guiRefresher.isIndicatorShownOnSubWindow(this.val$id))
/*  93 */           MainDataChangeListener.this.guiRefresher.refreshMainContent();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void indicatorsRemoved(Period period, int[] ids)
/*     */   {
/* 101 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 103 */         MainDataChangeListener.this.guiRefresher.refreshMainContent();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void loadingStarted(Period period, OfferSide offerSide) {
/* 110 */     this.progressController.loadingStarted(period, offerSide);
/*     */   }
/*     */ 
/*     */   public void loadingFinished(Period period, OfferSide offerSide)
/*     */   {
/* 115 */     this.progressController.loadingFinished(period, offerSide);
/*     */   }
/*     */ 
/*     */   public void dataChanged(Instrument instrument, long from, long to)
/*     */   {
/* 120 */     if (this.instrument != instrument) {
/* 121 */       return;
/*     */     }
/* 123 */     this.dataOperationManager.ordersChanged(from, to);
/* 124 */     this.guiRefresher.refreshMainContent();
/*     */   }
/*     */ 
/*     */   public void loadingStarted(Instrument instrument)
/*     */   {
/* 129 */     if (this.instrument != instrument) {
/* 130 */       return;
/*     */     }
/* 132 */     this.progressController.loadingOrdersStarted();
/*     */   }
/*     */ 
/*     */   public void loadingFinished(Instrument instrument)
/*     */   {
/* 137 */     if (this.instrument != instrument) {
/* 138 */       return;
/*     */     }
/* 140 */     this.progressController.loadingOrdersFinished();
/*     */   }
/*     */ 
/*     */   public void addProgressListener(ProgressListener progressListener) {
/* 144 */     this.progressController.registerListener(progressListener);
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/* 148 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument) {
/* 152 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public void lastKnownDataChanged(Data data)
/*     */   {
/* 157 */     if (ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING))
/*     */     {
/* 159 */       this.guiRefresher.refreshMainContent();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.datachange.MainDataChangeListener
 * JD-Core Version:    0.6.0
 */