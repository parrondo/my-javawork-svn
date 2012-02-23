/*     */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*     */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TabsAndTreeDDSChartsActionListener extends DDSChartsActionAdapter
/*     */ {
/*     */   private WorkspaceTreeController workspaceController;
/*     */   private IChartTabsAndFramesController tabsAndFramesController;
/*     */   private int chartPanelId;
/*     */ 
/*     */   public TabsAndTreeDDSChartsActionListener(int chartPanelId, WorkspaceTreeController workspaceController, IChartTabsAndFramesController tabsAndFramesController)
/*     */   {
/*  28 */     this.workspaceController = workspaceController;
/*  29 */     this.tabsAndFramesController = tabsAndFramesController;
/*  30 */     this.chartPanelId = chartPanelId;
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  35 */     if (GreedContext.isStrategyAllowed())
/*  36 */       this.workspaceController.indicatorAdded(this.chartPanelId, subChartId, indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  42 */     if (GreedContext.isStrategyAllowed()) {
/*  43 */       this.workspaceController.indicatorChanged(Integer.valueOf(this.chartPanelId), indicatorWrapper);
/*     */     }
/*  45 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  46 */     LastUsedIndicatorBean indicatorBean = DDSChartsActionAdapter.convertToLastUsedIndicatorBean(indicatorWrapper);
/*  47 */     clientSettingsStorage.updateLastUsedIndicatorName(indicatorBean);
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(IndicatorWrapper indicatorWrapper)
/*     */   {
/*  52 */     if (GreedContext.isStrategyAllowed())
/*  53 */       this.workspaceController.indicatorRemoved(Integer.valueOf(this.chartPanelId), indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public void indicatorsRemoved(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  59 */     if (GreedContext.isStrategyAllowed())
/*     */     {
/*  61 */       for (IndicatorWrapper indicatorWrapper : indicatorWrappers)
/*  62 */         this.workspaceController.indicatorRemoved(Integer.valueOf(this.chartPanelId), indicatorWrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawingAdded(IChartObject drawing)
/*     */   {
/*  69 */     if (GreedContext.isStrategyAllowed())
/*  70 */       this.workspaceController.drawingAdded(this.chartPanelId, drawing);
/*     */   }
/*     */ 
/*     */   public void drawingAdded(int indicatorId, IChartObject drawing)
/*     */   {
/*  76 */     if (GreedContext.isStrategyAllowed())
/*  77 */       this.workspaceController.drawingAdded(this.chartPanelId, drawing);
/*     */   }
/*     */ 
/*     */   public void drawingChanged(IChartObject drawing)
/*     */   {
/*  83 */     if (GreedContext.isStrategyAllowed())
/*  84 */       this.workspaceController.drawingChanged(this.chartPanelId, drawing);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(IChartObject drawing)
/*     */   {
/*  90 */     if (GreedContext.isStrategyAllowed())
/*  91 */       this.workspaceController.drawingRemoved(this.chartPanelId, drawing);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(int indicatorId, IChartObject iChartObject)
/*     */   {
/*  97 */     if (GreedContext.isStrategyAllowed())
/*  98 */       this.workspaceController.drawingRemoved(this.chartPanelId, iChartObject);
/*     */   }
/*     */ 
/*     */   public void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/* 104 */     if (GreedContext.isStrategyAllowed())
/*     */     {
/* 106 */       for (IChartObject chartObject : chartObjects)
/* 107 */         this.workspaceController.drawingRemoved(this.chartPanelId, chartObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void periodChanged(Period newPeriod)
/*     */   {
/* 114 */     if (GreedContext.isStrategyAllowed()) {
/* 115 */       this.workspaceController.periodChanged(Integer.valueOf(this.chartPanelId), newPeriod);
/*     */     }
/* 117 */     this.tabsAndFramesController.updatePeriod(this.chartPanelId, newPeriod);
/*     */   }
/*     */ 
/*     */   public void instrumentChanged(Instrument instrument)
/*     */   {
/* 122 */     if (GreedContext.isStrategyAllowed()) {
/* 123 */       this.workspaceController.instrumentChanged(this.chartPanelId, instrument);
/*     */     }
/* 125 */     this.tabsAndFramesController.updateInstrument(this.chartPanelId, instrument);
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(DataType dataType)
/*     */   {
/* 130 */     if (GreedContext.isStrategyAllowed()) {
/* 131 */       this.workspaceController.dataTypeChanged(this.chartPanelId, dataType);
/*     */     }
/* 133 */     this.tabsAndFramesController.updateDataType(this.chartPanelId, dataType);
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(PriceRange priceRange)
/*     */   {
/* 138 */     if (GreedContext.isStrategyAllowed()) {
/* 139 */       this.workspaceController.priceRangeChanged(this.chartPanelId, priceRange);
/*     */     }
/* 141 */     this.tabsAndFramesController.updatePriceRange(this.chartPanelId, priceRange);
/*     */   }
/*     */ 
/*     */   public void jForexPeriodChanged(JForexPeriod jForexPeriod)
/*     */   {
/* 147 */     if (GreedContext.isStrategyAllowed()) {
/* 148 */       this.workspaceController.jForexPeriodChanged(this.chartPanelId, jForexPeriod);
/*     */     }
/* 150 */     this.tabsAndFramesController.updateJForexPeriod(this.chartPanelId, jForexPeriod);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.TabsAndTreeDDSChartsActionListener
 * JD-Core Version:    0.6.0
 */