/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ 
/*    */ public class TesterChartTreeNode extends ChartTreeNode
/*    */ {
/*    */   TesterChartTreeNode(int chartPanelId, Instrument instrument, OfferSide offerSide, JForexPeriod jForexPeriod, ChartsNode parent)
/*    */   {
/* 18 */     super(chartPanelId, instrument, offerSide, jForexPeriod, parent);
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 22 */     return "* " + super.getName();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.TesterChartTreeNode
 * JD-Core Version:    0.6.0
 */