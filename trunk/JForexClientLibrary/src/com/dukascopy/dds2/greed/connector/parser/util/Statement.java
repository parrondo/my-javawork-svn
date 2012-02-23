/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ 
/*    */ public abstract class Statement extends Declaration
/*    */ {
/*  6 */   boolean hasDeclarationList = false;
/*    */   protected ASTNode startNode;
/*    */   protected ASTNode expressionNode;
/*    */ 
/*    */   public ASTNode getStartNode()
/*    */   {
/* 11 */     return this.startNode;
/*    */   }
/*    */ 
/*    */   public void setStartNode(ASTNode startNode) {
/* 15 */     this.startNode = startNode;
/*    */   }
/*    */ 
/*    */   public ASTNode getExpressionNode() {
/* 19 */     return this.expressionNode;
/*    */   }
/*    */ 
/*    */   public void setExpressionNode(ASTNode expressionNode) {
/* 23 */     this.expressionNode = expressionNode;
/*    */   }
/*    */ 
/*    */   public boolean isHasDeclarationList() {
/* 27 */     return this.hasDeclarationList;
/*    */   }
/*    */ 
/*    */   public void setHasDeclarationList(boolean hasDeclarationList) {
/* 31 */     this.hasDeclarationList = hasDeclarationList;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.Statement
 * JD-Core Version:    0.6.0
 */