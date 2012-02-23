/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*    */ 
/*    */ public class SwitchStatement extends Statement
/*    */ {
/* 10 */   DefaultStatement defaultStatement = null;
/*    */ 
/*    */   public String endText()
/*    */   {
/* 14 */     return "}";
/*    */   }
/*    */ 
/*    */   public String startText()
/*    */   {
/* 19 */     StringBuilder buf = new StringBuilder();
/* 20 */     buf.append("switch (");
/* 21 */     if (this.expressionNode != null) {
/* 22 */       buf.append("toInt(");
/* 23 */       buf.append(DeclarationHelpers.getAssignmentExpression(this.expressionNode));
/* 24 */       buf.append(")");
/*    */     } else {
/* 26 */       ASTNode aeNode = null;
/* 27 */       for (int i = 0; i < getStartNode().getChildren().length; i++) {
/* 28 */         aeNode = getStartNode().getChildren()[i];
/* 29 */         if ((aeNode.getId() == 50) || (aeNode.getId() == 49))
/*    */           break;
/*    */       }
/* 32 */       Token token = aeNode.getBeginToken();
/* 33 */       while (token.kind != 41) {
/* 34 */         buf.append(token.image);
/* 35 */         token = token.next;
/*    */       }
/*    */     }
/* 38 */     buf.append("){");
/* 39 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public DefaultStatement getDefaultStatement() {
/* 43 */     return this.defaultStatement;
/*    */   }
/*    */ 
/*    */   public void setDefaultStatement(DefaultStatement defaultStatement) {
/* 47 */     this.defaultStatement = defaultStatement;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.SwitchStatement
 * JD-Core Version:    0.6.0
 */