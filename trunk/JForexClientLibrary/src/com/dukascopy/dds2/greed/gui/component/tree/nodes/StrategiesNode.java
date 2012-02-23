/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ public class StrategiesNode extends ServicesTreeNode
/*    */ {
/*    */   public StrategiesNode(WorkspaceRootNode parent)
/*    */   {
/* 13 */     super(parent, "tree.node.strategies");
/*    */   }
/*    */ 
/*    */   public List<StrategyTreeNode> getStrategyTreeNodes()
/*    */   {
/* 18 */     List strategies = new ArrayList();
/*    */ 
/* 20 */     int count = getChildCount();
/* 21 */     for (int i = 0; i < count; i++) {
/* 22 */       TreeNode node = getChildAt(i);
/* 23 */       if ((node instanceof StrategyTreeNode)) {
/* 24 */         strategies.add((StrategyTreeNode)node);
/*    */       }
/*    */     }
/*    */ 
/* 28 */     return strategies;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode
 * JD-Core Version:    0.6.0
 */