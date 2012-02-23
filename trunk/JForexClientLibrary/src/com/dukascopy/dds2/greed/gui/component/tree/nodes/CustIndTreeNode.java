/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ 
/*    */ public class CustIndTreeNode extends AbstractServiceTreeNode
/*    */ {
/*    */   private final CustIndicatorWrapper custIndicatorWrapper;
/* 10 */   private int enabledIndicatorId = -2147483648;
/*    */ 
/*    */   CustIndTreeNode(CustIndicatorWrapper custIndicatorWrapper, ServicesTreeNode parent, int id) {
/* 13 */     super(id, false, custIndicatorWrapper.getName());
/* 14 */     if (parent == null) {
/* 15 */       throw new IllegalArgumentException("CustIndTreeNode parent cannot be null!");
/*    */     }
/* 17 */     this.custIndicatorWrapper = custIndicatorWrapper;
/* 18 */     if (custIndicatorWrapper != null) {
/* 19 */       setEditable(custIndicatorWrapper.isEditable());
/*    */     }
/*    */ 
/* 22 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public CustIndicatorWrapper getServiceWrapper() {
/* 26 */     return this.custIndicatorWrapper;
/*    */   }
/*    */ 
/*    */   public ServiceSourceType getServiceSourceType() {
/* 30 */     return ServiceSourceType.INDICATOR;
/*    */   }
/*    */ 
/*    */   public int getEnabledIndicatorId() {
/* 34 */     return this.enabledIndicatorId;
/*    */   }
/*    */ 
/*    */   public void setEnabledIndicatorId(int enabledIndicatorId) {
/* 38 */     this.enabledIndicatorId = enabledIndicatorId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode
 * JD-Core Version:    0.6.0
 */