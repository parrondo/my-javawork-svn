/*    */ package com.dukascopy.dds2.greed.connector.parser.util.conditions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ 
/*    */ abstract class ConditionItem
/*    */   implements IConditionItem
/*    */ {
/*    */   ASTNode node;
/*    */   ConditionRoot conditionRoot;
/*  8 */   boolean isInLRParnesis = false;
/*  9 */   boolean boolResult = false;
/*    */   IConditionItem parent;
/*    */   IConditionItem prev;
/*    */   IConditionItem next;
/*    */ 
/*    */   public ASTNode getNode()
/*    */   {
/* 15 */     return this.node;
/*    */   }
/*    */ 
/*    */   public void setNode(ASTNode node) {
/* 19 */     this.node = node;
/*    */   }
/*    */ 
/*    */   public ConditionRoot getConditionRoot() {
/* 23 */     return this.conditionRoot;
/*    */   }
/*    */ 
/*    */   public void setConditionRoot(ConditionRoot conditionRoot) {
/* 27 */     this.conditionRoot = conditionRoot;
/*    */   }
/*    */ 
/*    */   public boolean isInLRParnesis() {
/* 31 */     return this.isInLRParnesis;
/*    */   }
/*    */ 
/*    */   public void setInLRParnesis(boolean isInLRParnesis) {
/* 35 */     this.isInLRParnesis = isInLRParnesis;
/*    */   }
/*    */ 
/*    */   public void setNextConditionItem(IConditionItem next) {
/* 39 */     this.next = next;
/*    */   }
/*    */ 
/*    */   public IConditionItem next() {
/* 43 */     return this.next;
/*    */   }
/*    */ 
/*    */   public void setPreviousConditionItem(IConditionItem prev) {
/* 47 */     this.prev = prev;
/*    */   }
/*    */ 
/*    */   public IConditionItem prev() {
/* 51 */     return this.prev;
/*    */   }
/*    */ 
/*    */   public boolean isBoolResult() {
/* 55 */     return this.boolResult;
/*    */   }
/*    */ 
/*    */   public void setBoolResult(boolean boolResult) {
/* 59 */     this.boolResult = boolResult;
/*    */   }
/*    */ 
/*    */   public IConditionItem getParent() {
/* 63 */     return this.parent;
/*    */   }
/*    */ 
/*    */   public void setParent(IConditionItem parent) {
/* 67 */     this.parent = parent;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.conditions.ConditionItem
 * JD-Core Version:    0.6.0
 */