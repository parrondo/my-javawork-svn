/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*    */ 
/*    */ public class ReturnStatement extends Statement
/*    */ {
/*    */   FunctionDeclaration functionDeclaration;
/*    */ 
/*    */   public String startText()
/*    */   {
/* 12 */     StringBuilder buf = new StringBuilder("");
/* 13 */     StringBuilder expression = new StringBuilder("");
/* 14 */     buf.append("if(true)");
/* 15 */     buf.append("return ");
/* 16 */     if ((getFunctionDeclaration() != null) && (!getFunctionDeclaration().getType().equals("void")))
/*    */     {
/* 18 */       if ((getStartNode() != null) && (getStartNode().getBeginToken() != null))
/*    */       {
/* 20 */         Token token = getStartNode().getBeginToken();
/* 21 */         token = token.next;
/* 22 */         while (token.kind != 44) {
/* 23 */           expression.append(token.image);
/* 24 */           token = token.next;
/*    */         }
/*    */       }
/* 27 */       if (expression.toString().trim().isEmpty())
/* 28 */         buf.append(this.functionDeclaration.getFunctionDefaultValue());
/*    */       else {
/* 30 */         buf.append(DeclarationHelpers.makeTypedExpression(expression.toString(), this.functionDeclaration.getType()));
/*    */       }
/*    */     }
/*    */ 
/* 34 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public String endText()
/*    */   {
/* 39 */     return ";";
/*    */   }
/*    */ 
/*    */   public FunctionDeclaration getFunctionDeclaration() {
/* 43 */     return this.functionDeclaration;
/*    */   }
/*    */ 
/*    */   public void setFunctionDeclaration(FunctionDeclaration functionDeclaration) {
/* 47 */     this.functionDeclaration = functionDeclaration;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.ReturnStatement
 * JD-Core Version:    0.6.0
 */