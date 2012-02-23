/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ 
/*    */ public class IndicatorTreeNode extends ChartTreeNodeChild
/*    */ {
/*    */   private int subChartId;
/*    */   private IndicatorWrapper indicatorWrapper;
/*    */ 
/*    */   IndicatorTreeNode(int subChartId, IndicatorWrapper indicatorWrapper, ChartTreeNode parent)
/*    */   {
/* 11 */     super(false, "");
/* 12 */     this.subChartId = subChartId;
/* 13 */     if (indicatorWrapper == null) {
/* 14 */       throw new IllegalArgumentException("IndicatorWrapper cannot be null");
/*    */     }
/* 16 */     if (parent == null) {
/* 17 */       throw new IllegalArgumentException("IndicatorTreeNode cannot have a null parent!");
/*    */     }
/* 19 */     this.indicatorWrapper = indicatorWrapper;
/* 20 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public IndicatorWrapper getIndicator() {
/* 24 */     return this.indicatorWrapper;
/*    */   }
/*    */ 
/*    */   public void setIndicator(IndicatorWrapper indicatorWrapper) {
/* 28 */     if (indicatorWrapper == null) throw new IllegalArgumentException("IndicatorWrapper cannot be null");
/* 29 */     this.indicatorWrapper = indicatorWrapper;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 33 */     String props = this.indicatorWrapper.getPropsStr();
/* 34 */     if (props != null) {
/* 35 */       return this.indicatorWrapper.getName() + "(" + props + ")";
/*    */     }
/* 37 */     return this.indicatorWrapper.getName();
/*    */   }
/*    */ 
/*    */   public int getSubPanelId()
/*    */   {
/* 42 */     return this.subChartId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode
 * JD-Core Version:    0.6.0
 */