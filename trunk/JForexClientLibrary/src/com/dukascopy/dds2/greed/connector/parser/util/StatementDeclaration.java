/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*    */ 
/*    */ public class StatementDeclaration extends Declaration
/*    */ {
/*    */   Token parentToken;
/*    */   String expression;
/*    */ 
/*    */   public String getExpression()
/*    */   {
/*  9 */     return this.expression;
/*    */   }
/*    */   public void setExpression(String expression) {
/* 12 */     this.expression = expression;
/*    */   }
/*    */ 
/*    */   public String endText()
/*    */   {
/* 17 */     return "";
/*    */   }
/*    */ 
/*    */   public String startText()
/*    */   {
/* 22 */     return "";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.StatementDeclaration
 * JD-Core Version:    0.6.0
 */