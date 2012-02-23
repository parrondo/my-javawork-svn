/*     */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.file.filter.TemplateFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.TesterChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage;
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ 
/*     */ class AddTesterChartTreeAction extends TreeAction
/*     */ {
/*     */   final IChartTabsAndFramesController chartTabsAndFramesController;
/*     */   final WorkspaceNodeFactory workspaceNodeFactory;
/*     */   final IWorkspaceHelper workspaceHelper;
/*     */   final ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   AddTesterChartTreeAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IWorkspaceHelper workspaceHelper, ClientSettingsStorage clientSettingsStorage)
/*     */   {
/*  40 */     super(workspaceJTree);
/*  41 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*  42 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*  43 */     this.workspaceHelper = workspaceHelper;
/*  44 */     this.clientSettingsStorage = clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   protected Object executeInternal(Object param)
/*     */   {
/*  49 */     Object param0 = ((Object[])(Object[])param)[0];
/*  50 */     Object param1 = ((Object[])(Object[])param)[1];
/*     */ 
/*  52 */     Map instrumentsAndProviders = (Map)param0;
/*  53 */     String toolTipText = (String)param1;
/*     */ 
/*  55 */     for (Instrument instrument : instrumentsAndProviders.keySet()) {
/*  56 */       TesterChartData chartData = (TesterChartData)instrumentsAndProviders.get(instrument);
/*  57 */       TesterChartTreeNode node = this.workspaceNodeFactory.createTesterChartTreeNode(instrument, chartData.offerSide, chartData.jForexPeriod);
/*  58 */       this.workspaceJTree.addChartNode(node);
/*  59 */       this.workspaceJTree.selectNode(node);
/*     */ 
/*  61 */       String templateFullFileName = getTemplateFullFileName(chartData.templateName);
/*  62 */       if (isChartTemplateExist(templateFullFileName)) {
/*  63 */         ChartBean chartBean = createChartBean(node.getChartPanelId(), node.getInstrument(), node.getJForexPeriod(), node.getOfferSide(), templateFullFileName);
/*  64 */         this.chartTabsAndFramesController.addOrSelectInstumentTesterChart(toolTipText, chartBean, chartData.feedDataProvider);
/*     */       }
/*     */       else
/*     */       {
/*  70 */         this.chartTabsAndFramesController.addOrSelectInstumentTesterChart(node.getChartPanelId(), toolTipText, node.getJForexPeriod(), node.getInstrument(), node.getOfferSide(), chartData.feedDataProvider);
/*     */       }
/*     */ 
/*  79 */       chartData.chartPanelId = node.getChartPanelId();
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   private ChartBean createChartBean(int chartPanelId, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, String chartTemplateName) {
/*  85 */     IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/*  86 */     ChartBean chartBean = chartTemplateSettingsStorage.loadChartTemplate(new File(chartTemplateName));
/*     */ 
/*  88 */     chartBean.setAutoShiftActive(1);
/*  89 */     chartBean.setId(chartPanelId);
/*  90 */     chartBean.setInstrument(instrument);
/*  91 */     chartBean.setJForexPeriod(jForexPeriod);
/*  92 */     chartBean.setOfferSide(offerSide);
/*     */ 
/*  94 */     return chartBean;
/*     */   }
/*     */ 
/*     */   private boolean isChartTemplateExist(String chartTemplateName)
/*     */   {
/* 102 */     return (chartTemplateName != null) && (chartTemplateName.length() != 0) && (isFileExist(chartTemplateName));
/*     */   }
/*     */ 
/*     */   private String getTemplateFullFileName(String chartTemplateName)
/*     */   {
/* 109 */     if ((chartTemplateName == null) || (chartTemplateName.length() == 0)) {
/* 110 */       return chartTemplateName;
/*     */     }
/*     */ 
/* 113 */     TemplateFileFilter templateFileFilter = new TemplateFileFilter(LocalizationManager.getText("jforex.chart.template.files"));
/* 114 */     String templatesPath = this.clientSettingsStorage.getMyChartTemplatesPath();
/*     */ 
/* 116 */     StringBuffer buf = new StringBuffer(128);
/* 117 */     buf.append(templatesPath).append(System.getProperty("file.separator")).append(chartTemplateName).append(".").append(templateFileFilter.getExtension());
/*     */ 
/* 124 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private boolean isFileExist(String fileName) {
/* 128 */     File file = new File(fileName);
/* 129 */     return file.exists();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddTesterChartTreeAction
 * JD-Core Version:    0.6.0
 */