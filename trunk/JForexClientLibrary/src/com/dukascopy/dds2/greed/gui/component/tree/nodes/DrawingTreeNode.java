/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.IChartObject;
/*    */ import com.dukascopy.charts.drawings.ChartObject;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public class DrawingTreeNode extends ChartTreeNodeChild
/*    */ {
/*    */   private final IChartObject chartObject;
/*    */ 
/*    */   DrawingTreeNode(IChartObject chartObject, ChartTreeNode parent)
/*    */   {
/* 12 */     super(false, "");
/*    */ 
/* 14 */     if (chartObject == null) {
/* 15 */       throw new IllegalArgumentException("ChartObject cannot be null");
/*    */     }
/*    */ 
/* 18 */     if (parent == null) {
/* 19 */       throw new IllegalArgumentException("DrawingTreeNode cannot have a null parent");
/*    */     }
/*    */ 
/* 22 */     this.chartObject = chartObject;
/* 23 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 27 */     return LocalizationManager.getText(((ChartObject)this.chartObject).getLocalizationKey());
/*    */   }
/*    */ 
/*    */   public IChartObject getDrawing() {
/* 31 */     return this.chartObject;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode
 * JD-Core Version:    0.6.0
 */