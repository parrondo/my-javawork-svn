/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.tablebuilder.ITablePresentationManager;
/*     */ import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLayeredPane;
/*     */ 
/*     */ class GuiRefresherImpl
/*     */   implements GuiRefresher
/*     */ {
/*     */   GuiRefresher chartsGuiManager;
/*     */ 
/*     */   public void setChartsGuiManager(GuiRefresher chartsGuiManager)
/*     */   {
/*  17 */     this.chartsGuiManager = chartsGuiManager;
/*     */   }
/*     */ 
/*     */   public void createMainChartView()
/*     */   {
/*  22 */     this.chartsGuiManager.createMainChartView();
/*     */   }
/*     */ 
/*     */   public Integer getSubChartViewIdFor(int indicatorId)
/*     */   {
/*  27 */     return this.chartsGuiManager.getSubChartViewIdFor(indicatorId);
/*     */   }
/*     */ 
/*     */   public int createSubChartView()
/*     */   {
/*  32 */     return this.chartsGuiManager.createSubChartView();
/*     */   }
/*     */ 
/*     */   public void createSubChartView(Integer subChartId)
/*     */   {
/*  37 */     this.chartsGuiManager.createSubChartView(subChartId);
/*     */   }
/*     */ 
/*     */   public void addSubIndicatorToSubChartView(int subWindowId, IndicatorWrapper indicatorWrapper)
/*     */   {
/*  42 */     this.chartsGuiManager.addSubIndicatorToSubChartView(subWindowId, indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public int deleteSubIndicatorFromSubChartView(int subWindowId, IndicatorWrapper indicatorWrapper)
/*     */   {
/*  47 */     return this.chartsGuiManager.deleteSubIndicatorFromSubChartView(subWindowId, indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public void deleteSubChartView(Integer subWindowId)
/*     */   {
/*  52 */     this.chartsGuiManager.deleteSubChartView(subWindowId);
/*     */   }
/*     */ 
/*     */   public void deleteSubChartViewsIfNecessary(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  57 */     this.chartsGuiManager.deleteSubChartViewsIfNecessary(indicatorWrappers);
/*     */   }
/*     */ 
/*     */   public boolean isIndicatorShownOnSubWindow(int indicatorId)
/*     */   {
/*  62 */     return this.chartsGuiManager.isIndicatorShownOnSubWindow(indicatorId);
/*     */   }
/*     */ 
/*     */   public boolean isSubViewEmpty(Integer subWindowId)
/*     */   {
/*  67 */     return this.chartsGuiManager.isSubViewEmpty(subWindowId);
/*     */   }
/*     */ 
/*     */   public boolean doesSubViewExists(Integer subWindowId)
/*     */   {
/*  72 */     return this.chartsGuiManager.doesSubViewExists(subWindowId);
/*     */   }
/*     */ 
/*     */   public JComponent getChartsContainer()
/*     */   {
/*  77 */     return this.chartsGuiManager.getChartsContainer();
/*     */   }
/*     */ 
/*     */   public void refreshMainContent()
/*     */   {
/*  82 */     this.chartsGuiManager.refreshMainContent();
/*     */   }
/*     */ 
/*     */   public void refreshSubContentByIndicatorId(Integer indicatorId)
/*     */   {
/*  87 */     this.chartsGuiManager.refreshSubContentByIndicatorId(indicatorId);
/*     */   }
/*     */ 
/*     */   public void refreshSubContentBySubViewId(int subViewId)
/*     */   {
/*  92 */     this.chartsGuiManager.refreshSubContentBySubViewId(subViewId);
/*     */   }
/*     */ 
/*     */   public void refreshAllContent()
/*     */   {
/*  97 */     this.chartsGuiManager.refreshAllContent();
/*     */   }
/*     */ 
/*     */   public void repaintMainContent()
/*     */   {
/* 102 */     this.chartsGuiManager.repaintMainContent();
/*     */   }
/*     */ 
/*     */   public void repaintSubContentBySubViewId(int subViewId)
/*     */   {
/* 107 */     this.chartsGuiManager.repaintSubContentBySubViewId(subViewId);
/*     */   }
/*     */ 
/*     */   public void invalidateMainContent()
/*     */   {
/* 112 */     this.chartsGuiManager.invalidateMainContent();
/*     */   }
/*     */ 
/*     */   public void invalidateAllContent()
/*     */   {
/* 117 */     this.chartsGuiManager.invalidateAllContent();
/*     */   }
/*     */ 
/*     */   public void setFocusToMainChartView()
/*     */   {
/* 122 */     this.chartsGuiManager.setFocusToMainChartView();
/*     */   }
/*     */ 
/*     */   public int getWindowsCount()
/*     */   {
/* 127 */     return this.chartsGuiManager.getWindowsCount();
/*     */   }
/*     */ 
/*     */   public Integer getBasicIndicatorIdByWindowIndex(int index)
/*     */   {
/* 132 */     return this.chartsGuiManager.getBasicIndicatorIdByWindowIndex(index);
/*     */   }
/*     */ 
/*     */   public JComponent getMainContainer()
/*     */   {
/* 137 */     return this.chartsGuiManager.getMainContainer();
/*     */   }
/*     */ 
/*     */   public DataTablePresentationAbstractJTable<?, ?> getCurrentChartDataTable()
/*     */   {
/* 142 */     return this.chartsGuiManager.getCurrentChartDataTable();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getCandleTablePresentationManager()
/*     */   {
/* 147 */     return this.chartsGuiManager.getCandleTablePresentationManager();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getTickTablePresentationManager()
/*     */   {
/* 152 */     return this.chartsGuiManager.getTickTablePresentationManager();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getPriceRangeTablePresentationManager()
/*     */   {
/* 157 */     return this.chartsGuiManager.getPriceRangeTablePresentationManager();
/*     */   }
/*     */ 
/*     */   public boolean isSubChartLast(int subWindowId)
/*     */   {
/* 162 */     return this.chartsGuiManager.isSubChartLast(subWindowId);
/*     */   }
/*     */ 
/*     */   public void refreshSubContents()
/*     */   {
/* 167 */     this.chartsGuiManager.refreshSubContents();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getPointAndFigureTablePresentationManager()
/*     */   {
/* 172 */     return this.chartsGuiManager.getPointAndFigureTablePresentationManager();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getTickBarTablePresentationManager()
/*     */   {
/* 177 */     return this.chartsGuiManager.getTickBarTablePresentationManager();
/*     */   }
/*     */ 
/*     */   public JLayeredPane getChartsLayeredPane()
/*     */   {
/* 182 */     return this.chartsGuiManager.getChartsLayeredPane();
/*     */   }
/*     */ 
/*     */   public ITablePresentationManager getRenkoTablePresentationManager()
/*     */   {
/* 187 */     return this.chartsGuiManager.getRenkoTablePresentationManager();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.GuiRefresherImpl
 * JD-Core Version:    0.6.0
 */