/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*    */ 
/*    */ public class CaseStatement extends Statement
/*    */ {
/*    */   public String endText()
/*    */   {
/* 13 */     return "";
/*    */   }
/*    */ 
/*    */   public String startText()
/*    */   {
/* 18 */     StringBuilder buf = new StringBuilder();
/* 19 */     buf.append("case ");
/* 20 */     Token token = getStartNode().getBeginToken();
/* 21 */     token = token.next.next;
/* 22 */     while (token.kind != 43) {
/* 23 */       buf.append(token.image);
/* 24 */       token = token.next;
/*    */     }
/* 26 */     buf.append(": ");
/* 27 */     return buf.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.CaseStatement
 * JD-Core Version:    0.6.0
 */