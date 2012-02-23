/*     */ package com.dukascopy.dds2.greed.gui.helpers;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartsFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JClientWorkspaceHellper extends CommonWorkspaceHellper
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(JClientWorkspaceHellper.class);
/*     */ 
/*     */   public void populateWorkspace() {
/*  36 */     boolean isExpanded = getClientSettingsStorage().isFramesExpandedOf(getClientSettingsStorage().getChartsNode());
/*  37 */     List chartBeans = getClientSettingsStorage().getChartBeans();
/*     */     Preferences mainFramePreferencesNode;
/*  39 */     if (!chartBeans.isEmpty()) {
/*  40 */       mainFramePreferencesNode = getClientSettingsStorage().getMainFramePreferencesNode();
/*  41 */       for (ChartBean chartBean : chartBeans) {
/*     */         try {
/*  43 */           ChartsFrame.getInstance().addChart(chartBean, getClientSettingsStorage().isFrameUndocked(mainFramePreferencesNode, Integer.valueOf(chartBean.getId())), isExpanded);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*  48 */           LOGGER.error("Failed to add chart with id: " + chartBean.getId());
/*  49 */         }continue;
/*     */       }
/*     */     }
/*     */ 
/*  53 */     ChartsFrame.getInstance().restoreFrames();
/*     */   }
/*     */ 
/*     */   public int calculateNextNodeIndexToBeFocused(int nodeToBeRemovedIndx)
/*     */   {
/*  58 */     return 0;
/*     */   }
/*     */ 
/*     */   public int calculatePreviousNodeIndxToBeFocused(int nodeToBeRemovedIndx)
/*     */   {
/*  63 */     return 0;
/*     */   }
/*     */ 
/*     */   public void checkDependantCurrenciesAndAddThemIfNecessary(WorkspaceJTree workspaceJTree, String orderInstrument)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void findAndsubscribeToCurrenciesForProfitLossCalculation()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Instrument getSelectedInstrument(TreeSelectionEvent event)
/*     */   {
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments()
/*     */   {
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getUnsubscribedInstruments()
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */ 
/*     */   public void loadDataIntoWorkspace(WorkspaceJTree workspaceJTree)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void refreshDealPanel(Instrument selectedInstrument)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void selectTabForSelectedNode(WorkspaceJTree workspaceJTree)
/*     */   {
/*     */   }
/*     */ 
/*     */   public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess)
/*     */     throws JFException
/*     */   {
/* 110 */     return 0L;
/*     */   }
/*     */ 
/*     */   public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess)
/*     */     throws JFException
/*     */   {
/* 116 */     return 0L;
/*     */   }
/*     */ 
/*     */   public void showChart(Instrument instr)
/*     */   {
/* 121 */     if (instr == null) return;
/*     */ 
/* 123 */     ChartsFrame chartsFrame = ChartsFrame.getInstance();
/* 124 */     chartsFrame.addChart(instr);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.JClientWorkspaceHellper
 * JD-Core Version:    0.6.0
 */