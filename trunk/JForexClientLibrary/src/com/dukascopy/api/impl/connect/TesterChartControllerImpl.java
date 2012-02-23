/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.api.system.tester.ITesterChartController;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.BalanceHistoricalTesterIndicator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.EquityHistoricalTesterIndicator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ProfLossHistoricalTesterIndicator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TesterChartControllerImpl
/*     */   implements ITesterChartController
/*     */ {
/*  22 */   private int chartPanelId = -1;
/*  23 */   private DDSChartsController ddsChartsController = null;
/*     */   private LinkedList<TesterIndicatorWrapper> indicatorsWrappers;
/*     */ 
/*     */   public TesterChartControllerImpl(DDSChartsController ddsChartsController, int chartPanelId, LinkedList<TesterIndicatorWrapper> indicatorsWrappers)
/*     */   {
/*  31 */     this.ddsChartsController = ddsChartsController;
/*  32 */     this.chartPanelId = chartPanelId;
/*  33 */     this.indicatorsWrappers = indicatorsWrappers;
/*     */   }
/*     */ 
/*     */   public void addIndicators()
/*     */   {
/*  38 */     if (isValid())
/*  39 */       this.ddsChartsController.createAddEditIndicatorsDialog(this.chartPanelId);
/*     */   }
/*     */ 
/*     */   public void changePeriod(DataType dataType, Period period)
/*     */   {
/*  45 */     if (isValid()) {
/*  46 */       JForexPeriod jForexPeriod = new JForexPeriod(dataType, period);
/*  47 */       this.ddsChartsController.changeJForexPeriod(Integer.valueOf(this.chartPanelId), jForexPeriod);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFeedDescriptor(IFeedDescriptor feedDescriptor)
/*     */   {
/*  53 */     if (feedDescriptor == null) {
/*  54 */       throw new IllegalArgumentException("feedDescriptor is null ");
/*     */     }
/*  56 */     if (isValid())
/*     */     {
/*  58 */       if (feedDescriptor.getDataType() == null)
/*  59 */         throw new IllegalArgumentException("DataType is null ");
/*     */       JForexPeriod jForexPeriod;
/*  61 */       switch (1.$SwitchMap$com$dukascopy$api$DataType[feedDescriptor.getDataType().ordinal()]) {
/*     */       case 1:
/*  63 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), Period.TICK);
/*     */ 
/*  67 */         break;
/*     */       case 2:
/*  69 */         if (feedDescriptor.getPeriod() == null) {
/*  70 */           throw new IllegalArgumentException("Period is null ");
/*     */         }
/*  72 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), feedDescriptor.getPeriod());
/*     */ 
/*  76 */         break;
/*     */       case 3:
/*  78 */         if (feedDescriptor.getPriceRange() == null) {
/*  79 */           throw new IllegalArgumentException("PriceRange is null ");
/*     */         }
/*  81 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), Period.TICK, feedDescriptor.getPriceRange());
/*     */ 
/*  86 */         break;
/*     */       case 4:
/*  88 */         if (feedDescriptor.getPriceRange() == null) {
/*  89 */           throw new IllegalArgumentException("PriceRange is null ");
/*     */         }
/*  91 */         if (feedDescriptor.getReversalAmount() == null) {
/*  92 */           throw new IllegalArgumentException("ReversalAmount is null ");
/*     */         }
/*  94 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), Period.TICK, feedDescriptor.getPriceRange(), feedDescriptor.getReversalAmount());
/*     */ 
/* 100 */         break;
/*     */       case 5:
/* 102 */         if (feedDescriptor.getTickBarSize() == null) {
/* 103 */           throw new IllegalArgumentException("TickBarSize is null ");
/*     */         }
/* 105 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), Period.TICK, feedDescriptor.getTickBarSize());
/*     */ 
/* 110 */         break;
/*     */       case 6:
/* 112 */         if (feedDescriptor.getPriceRange() == null) {
/* 113 */           throw new IllegalArgumentException("PriceRange is null ");
/*     */         }
/* 115 */         jForexPeriod = new JForexPeriod(feedDescriptor.getDataType(), Period.TICK, feedDescriptor.getPriceRange());
/*     */ 
/* 120 */         break;
/*     */       default:
/* 121 */         throw new IllegalArgumentException("Unsupported data type " + feedDescriptor.getDataType());
/*     */       }
/* 123 */       this.ddsChartsController.changeJForexPeriod(Integer.valueOf(this.chartPanelId), jForexPeriod);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void activatePriceMarker()
/*     */   {
/* 129 */     if (isValid())
/* 130 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.PRICEMARKER);
/*     */   }
/*     */ 
/*     */   public void activateTimeMarker()
/*     */   {
/* 136 */     if (isValid())
/* 137 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.TIMEMARKER);
/*     */   }
/*     */ 
/*     */   public void setChartAutoShift()
/*     */   {
/* 143 */     if (isValid())
/* 144 */       this.ddsChartsController.shiftChartToFront(Integer.valueOf(this.chartPanelId));
/*     */   }
/*     */ 
/*     */   public void activatePercentLines()
/*     */   {
/* 150 */     if (isValid())
/* 151 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.PERCENT);
/*     */   }
/*     */ 
/*     */   public void activateChannelLines()
/*     */   {
/* 157 */     if (isValid())
/* 158 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.CHANNEL);
/*     */   }
/*     */ 
/*     */   public void zoomIn()
/*     */   {
/* 164 */     if (isValid())
/* 165 */       this.ddsChartsController.zoomIn(Integer.valueOf(this.chartPanelId));
/*     */   }
/*     */ 
/*     */   public void zoomOut()
/*     */   {
/* 171 */     if (isValid())
/* 172 */       this.ddsChartsController.zoomOut(Integer.valueOf(this.chartPanelId));
/*     */   }
/*     */ 
/*     */   public void addOHLCInformer()
/*     */   {
/* 178 */     if (isValid())
/* 179 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.OHLC_INFORMER);
/*     */   }
/*     */ 
/*     */   public void switchOfferSide(OfferSide offerSide)
/*     */   {
/* 185 */     if (isValid())
/* 186 */       this.ddsChartsController.switchBidAskTo(Integer.valueOf(this.chartPanelId), offerSide);
/*     */   }
/*     */ 
/*     */   public void activatePolyLine()
/*     */   {
/* 192 */     if (isValid())
/* 193 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.POLY_LINE);
/*     */   }
/*     */ 
/*     */   public void activateShortLine()
/*     */   {
/* 199 */     if (isValid())
/* 200 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.SHORT_LINE);
/*     */   }
/*     */ 
/*     */   public void activateLongLine()
/*     */   {
/* 206 */     if (isValid())
/* 207 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.LONG_LINE);
/*     */   }
/*     */ 
/*     */   public void activateRayLine()
/*     */   {
/* 213 */     if (isValid())
/* 214 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.RAY_LINE);
/*     */   }
/*     */ 
/*     */   public void activateHorizontalLine()
/*     */   {
/* 220 */     if (isValid())
/* 221 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.HLINE);
/*     */   }
/*     */ 
/*     */   public void activateVerticalLine()
/*     */   {
/* 227 */     if (isValid())
/* 228 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.VLINE);
/*     */   }
/*     */ 
/*     */   public void activateTextMode()
/*     */   {
/* 234 */     if (isValid())
/* 235 */       this.ddsChartsController.startDrawing(Integer.valueOf(this.chartPanelId), IChart.Type.TEXT);
/*     */   }
/*     */ 
/*     */   public void showEquityIndicator(boolean show)
/*     */   {
/* 241 */     boolean notFound = true;
/* 242 */     for (TesterIndicatorWrapper testerIndicatorWrapper : this.indicatorsWrappers) {
/* 243 */       if ((testerIndicatorWrapper.getIndicator() instanceof EquityHistoricalTesterIndicator)) {
/* 244 */         notFound = false;
/* 245 */         showTesterIndicator(this.chartPanelId, testerIndicatorWrapper, show);
/* 246 */         break;
/*     */       }
/*     */     }
/* 249 */     if (notFound)
/* 250 */       throw new IllegalStateException("Equity indicator wasn't enabled, see ITesterIndicatorsParameters.");
/*     */   }
/*     */ 
/*     */   public void showProfitLossIndicator(boolean show)
/*     */   {
/* 256 */     boolean notFound = true;
/* 257 */     for (TesterIndicatorWrapper testerIndicatorWrapper : this.indicatorsWrappers) {
/* 258 */       if ((testerIndicatorWrapper.getIndicator() instanceof ProfLossHistoricalTesterIndicator)) {
/* 259 */         notFound = false;
/* 260 */         showTesterIndicator(this.chartPanelId, testerIndicatorWrapper, show);
/* 261 */         break;
/*     */       }
/*     */     }
/* 264 */     if (notFound)
/* 265 */       throw new IllegalStateException("ProfitLoss indicator wasn't enabled, see ITesterIndicatorsParameters.");
/*     */   }
/*     */ 
/*     */   public void showBalanceIndicator(boolean show)
/*     */   {
/* 271 */     boolean notFound = true;
/* 272 */     for (TesterIndicatorWrapper testerIndicatorWrapper : this.indicatorsWrappers) {
/* 273 */       if ((testerIndicatorWrapper.getIndicator() instanceof BalanceHistoricalTesterIndicator)) {
/* 274 */         notFound = false;
/* 275 */         showTesterIndicator(this.chartPanelId, testerIndicatorWrapper, show);
/* 276 */         break;
/*     */       }
/*     */     }
/* 279 */     if (notFound)
/* 280 */       throw new IllegalStateException("Balance indicator wasn't enabled, see ITesterIndicatorsParameters.");
/*     */   }
/*     */ 
/*     */   private void showTesterIndicator(int chartPanelId, TesterIndicatorWrapper testerIndicatorWrapper, boolean show)
/*     */   {
/* 285 */     List wrappers = this.ddsChartsController.getIndicators(Integer.valueOf(chartPanelId));
/* 286 */     if (show) {
/* 287 */       if ((wrappers == null) || (!wrappers.contains(testerIndicatorWrapper))) {
/* 288 */         this.ddsChartsController.addIndicator(Integer.valueOf(chartPanelId), testerIndicatorWrapper);
/*     */       }
/*     */     }
/* 291 */     else if ((wrappers != null) && (wrappers.contains(testerIndicatorWrapper)))
/* 292 */       this.ddsChartsController.deleteIndicator(Integer.valueOf(chartPanelId), testerIndicatorWrapper);
/*     */   }
/*     */ 
/*     */   public int getChartPanelId()
/*     */   {
/* 298 */     return this.chartPanelId;
/*     */   }
/*     */ 
/*     */   public void setChartPanelId(int chartPanelId) {
/* 302 */     this.chartPanelId = chartPanelId;
/*     */   }
/*     */ 
/*     */   private boolean isValid() {
/* 306 */     boolean valid = false;
/* 307 */     if ((this.ddsChartsController != null) && (this.chartPanelId != -1)) {
/* 308 */       valid = true;
/*     */     }
/* 310 */     return valid;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.TesterChartControllerImpl
 * JD-Core Version:    0.6.0
 */