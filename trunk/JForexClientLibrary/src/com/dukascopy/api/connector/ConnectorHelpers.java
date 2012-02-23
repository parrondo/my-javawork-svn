/*     */ package com.dukascopy.api.connector;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.MQLConnector;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.InstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.ChartComboBoxModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*     */ import java.util.Set;
/*     */ import javax.swing.ComboBoxModel;
/*     */ 
/*     */ public class ConnectorHelpers
/*     */   implements IConnectorHelpers
/*     */ {
/*     */   public final Object[] getCharts()
/*     */   {
/*  30 */     IChart[] result = null;
/*  31 */     DDSChartsController comntroller = getChartsController();
/*  32 */     result = new IChart[comntroller.getChartControllerIdies().size()];
/*  33 */     int index = 0;
/*  34 */     for (Integer i : comntroller.getChartControllerIdies()) {
/*  35 */       IChart chart = comntroller.getIChartBy(i);
/*  36 */       result[(index++)] = chart;
/*     */     }
/*     */ 
/*  44 */     return result;
/*     */   }
/*     */ 
/*     */   public Object getConnectorInstance()
/*     */   {
/*  49 */     return new MQLConnector();
/*     */   }
/*     */ 
/*     */   private static DDSChartsController getChartsController()
/*     */   {
/*  54 */     return (DDSChartsController)GreedContext.get("chartsController");
/*     */   }
/*     */ 
/*     */   public ComboBoxModel getComboBoxModel()
/*     */   {
/*  59 */     return new ChartComboBoxModel((IChart[])(IChart[])getCharts());
/*     */   }
/*     */ 
/*     */   public int addChart(Instrument instrument, Period period)
/*     */   {
/*  65 */     return addChart(instrument, period, false);
/*     */   }
/*     */ 
/*     */   public int addChart(Instrument instrument, Period period, boolean historical)
/*     */   {
/*  74 */     JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  75 */     IChartTabsAndFramesController chartTabsAndFramesController = clientFormLayoutManager.getChartTabsController();
/*     */ 
/*  77 */     ChartBean chartBean = new ChartBean(IdManager.getInstance().getNextChartId(), instrument, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period), OfferSide.ASK);
/*     */ 
/*  83 */     chartBean.setHistoricalTesterChart(historical);
/*     */ 
/*  85 */     ChartTreeNode chartTreeNode = null;
/*  86 */     if (historical) {
/*  87 */       chartTreeNode = clientFormLayoutManager.getWorkspaceNodeFactory().createTesterChartTreeNodeFrom(chartBean);
/*     */     }
/*     */     else {
/*  90 */       chartTreeNode = clientFormLayoutManager.getWorkspaceNodeFactory().createChartTreeNodeFrom(chartBean);
/*     */     }
/*     */ 
/*  93 */     clientFormLayoutManager.getWorkspaceJTree().addChartNode(chartTreeNode);
/*  94 */     chartTabsAndFramesController.addChart(chartBean, false, false);
/*     */ 
/*  96 */     return chartBean.getId();
/*     */   }
/*     */ 
/*     */   public void subscribeToInstruments(Set<String> newInstrumentList)
/*     */   {
/* 101 */     AppActionEvent event = new InstrumentSubscribeAction(this, newInstrumentList);
/* 102 */     GreedContext.publishEvent(event);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.api.connector.ConnectorHelpers
 * JD-Core Version:    0.6.0
 */