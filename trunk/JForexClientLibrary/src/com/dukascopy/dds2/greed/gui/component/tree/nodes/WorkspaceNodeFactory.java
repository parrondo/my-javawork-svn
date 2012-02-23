/*     */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.charts.persistence.ServiceBean;
/*     */ import com.dukascopy.charts.persistence.StrategyBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.RemoteStrategyWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.StrategyHelper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import java.io.File;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ 
/*     */ public class WorkspaceNodeFactory
/*     */ {
/*     */   private final WorkspaceJTree workspaceJTree;
/*     */   private final IdManager idManager;
/*     */ 
/*     */   public WorkspaceNodeFactory(WorkspaceJTree workspaceJTree, IdManager idManager)
/*     */   {
/*  30 */     this.workspaceJTree = workspaceJTree;
/*  31 */     this.idManager = idManager;
/*     */   }
/*     */ 
/*     */   public WorkspaceRootNode createWorkspaceRootNode() {
/*  35 */     return new WorkspaceRootNode();
/*     */   }
/*     */ 
/*     */   public ChartTreeNode createChartTreeNode(Instrument selectedCurrency, OfferSide offerSide, JForexPeriod jForexPeriod) {
/*  39 */     return new ChartTreeNode(this.idManager.getNextChartId(), selectedCurrency, offerSide, jForexPeriod, getChartsNode());
/*     */   }
/*     */ 
/*     */   public TesterChartTreeNode createTesterChartTreeNodeFrom(ChartBean chartBean) {
/*  43 */     JForexPeriod jForexPeriod = new JForexPeriod(chartBean.getDataType(), chartBean.getPeriod(), chartBean.getPriceRange(), chartBean.getReversalAmount(), chartBean.getTickBarSize());
/*  44 */     return new TesterChartTreeNode(chartBean.getId(), chartBean.getInstrument(), chartBean.getOfferSide(), jForexPeriod, getChartsNode());
/*     */   }
/*     */ 
/*     */   public TesterChartTreeNode createTesterChartTreeNode(Instrument instrument) {
/*  48 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.TICKS, Period.TICK, null, null, null);
/*  49 */     return new TesterChartTreeNode(this.idManager.getNextChartId(), instrument, OfferSide.BID, jForexPeriod, getChartsNode());
/*     */   }
/*     */ 
/*     */   public TesterChartTreeNode createTesterChartTreeNode(Instrument instrument, OfferSide offerSide, JForexPeriod jForexPeriod) {
/*  53 */     return new TesterChartTreeNode(this.idManager.getNextChartId(), instrument, offerSide, jForexPeriod, getChartsNode());
/*     */   }
/*     */ 
/*     */   public IndicatorTreeNode createIndicatorTreeNode(int subChartId, IndicatorWrapper indicatorWrapper, ChartTreeNode chartTreeNode) {
/*  57 */     return new IndicatorTreeNode(subChartId, indicatorWrapper, chartTreeNode);
/*     */   }
/*     */ 
/*     */   public DrawingTreeNode createDrawingTreeNode(IChartObject chartObject, ChartTreeNode chartTreeNode) {
/*  61 */     return new DrawingTreeNode(chartObject, chartTreeNode);
/*     */   }
/*     */ 
/*     */   public CustIndTreeNode createCustIndTreeNode(ServiceBean serviceBean) {
/*  65 */     int id = extractServiceId(serviceBean);
/*  66 */     CustIndicatorWrapper serviceWrapper = new CustIndicatorWrapper();
/*  67 */     boolean shouldBeCreated = addFilesToServiceWrapper(serviceWrapper, serviceBean);
/*  68 */     if (shouldBeCreated) {
/*  69 */       return new CustIndTreeNode(serviceWrapper, getCustIndicatorsNode(), id);
/*     */     }
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean restoreDeletedFile(StrategyBean strategyBean, RemoteStrategyWrapper wrapper)
/*     */   {
/*  77 */     String srcFileName = strategyBean.getSourceFullFileName();
/*     */     File strategyFile;
/*  78 */     if ((srcFileName != null) && (srcFileName.length() > 1)) {
/*  79 */       strategyFile = new File(srcFileName);
/*     */     } else {
/*  81 */       String binFileName = strategyBean.getBinaryFullFileName();
/*     */       File strategyFile;
/*  82 */       if ((binFileName != null) && (binFileName.length() > 1))
/*  83 */         strategyFile = new File(binFileName);
/*     */       else
/*  85 */         return false;
/*     */     }
/*     */     File strategyFile;
/*  88 */     strategyFile.getParentFile().mkdirs();
/*  89 */     return StrategyHelper.downloadStrategy(wrapper, strategyFile);
/*     */   }
/*     */ 
/*     */   public ChartTreeNode createChartTreeNodeFrom(ChartBean chartBean) {
/*  93 */     int chartId = chartBean.getId();
/*  94 */     this.idManager.reserveChartId(chartId);
/*  95 */     JForexPeriod jForexPeriod = new JForexPeriod(chartBean.getDataType(), chartBean.getPeriod(), chartBean.getPriceRange(), chartBean.getReversalAmount(), chartBean.getTickBarSize());
/*  96 */     return new ChartTreeNode(chartId, chartBean.getInstrument(), chartBean.getOfferSide(), jForexPeriod, getChartsNode());
/*     */   }
/*     */ 
/*     */   public WorkspaceJTree getWorkspaceJTree() {
/* 100 */     return this.workspaceJTree;
/*     */   }
/*     */ 
/*     */   ChartsNode getChartsNode() {
/* 104 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 105 */     return workspaceRootNode.getChartsNode();
/*     */   }
/*     */ 
/*     */   IndicatorsNode getCustIndicatorsNode() {
/* 109 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 110 */     return workspaceRootNode.getIndicatorsTreeNode();
/*     */   }
/*     */ 
/*     */   StrategiesNode getStrategiesNode() {
/* 114 */     WorkspaceRootNode workspaceRootNode = (WorkspaceRootNode)this.workspaceJTree.getModel().getRoot();
/* 115 */     return workspaceRootNode.getStrategiesTreeNode();
/*     */   }
/*     */ 
/*     */   int extractServiceId(ServiceBean serviceBean) {
/* 119 */     int id = serviceBean.getId().intValue();
/* 120 */     this.idManager.reserveServiceId(id);
/* 121 */     return id;
/*     */   }
/*     */ 
/*     */   boolean addFilesToServiceWrapper(ServiceWrapper serviceWrapper, ServiceBean serviceBean)
/*     */   {
/* 126 */     File sourceFile = createFile(serviceBean.getSourceFullFileName());
/* 127 */     File binaryFile = createFile(serviceBean.getBinaryFullFileName());
/*     */ 
/* 129 */     boolean shouldNodeBeCreated = false;
/* 130 */     if ((sourceFile != null) && (sourceFile.exists())) {
/* 131 */       serviceWrapper.setSourceFile(sourceFile);
/* 132 */       shouldNodeBeCreated = true;
/*     */     }
/* 134 */     if ((binaryFile != null) && (binaryFile.exists())) {
/* 135 */       serviceWrapper.setBinaryFile(binaryFile);
/* 136 */       shouldNodeBeCreated = true;
/*     */     }
/*     */ 
/* 140 */     return shouldNodeBeCreated;
/*     */   }
/*     */ 
/*     */   File createFile(String fullFileName) {
/* 144 */     if (fullFileName == null) {
/* 145 */       return null;
/*     */     }
/* 147 */     File file = new File(fullFileName);
/* 148 */     if (!file.exists()) {
/* 149 */       return null;
/*     */     }
/* 151 */     return file;
/*     */   }
/*     */ 
/*     */   public CustIndTreeNode createServiceTreeNodeFrom(CustIndicatorWrapper custIndWrapper) {
/* 155 */     return new CustIndTreeNode(custIndWrapper, getCustIndicatorsNode(), this.idManager.getNextServiceId());
/*     */   }
/*     */ 
/*     */   public StrategyTreeNode createStrategyTreeNodeFrom(StrategyNewBean strategyBean) {
/* 159 */     return new StrategyTreeNode(strategyBean, getStrategiesNode());
/*     */   }
/*     */ 
/*     */   public StrategyTreeNode createStrategyTreeNodeFrom(StrategyWrapper strategyWrapper, StrategyNewBean strategyBean) {
/* 163 */     return new StrategyTreeNode(strategyWrapper, strategyBean, getStrategiesNode());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory
 * JD-Core Version:    0.6.0
 */