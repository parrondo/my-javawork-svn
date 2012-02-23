/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class ElseStatement extends Statement
/*    */ {
/*    */   public String startText()
/*    */   {
/*  9 */     boolean hasIfChild = false;
/* 10 */     StringBuilder buf = new StringBuilder();
/* 11 */     buf.append("} else ");
/*    */ 
/* 13 */     if ((this.children.get(0) instanceof IfStatement)) {
/* 14 */       hasIfChild = true;
/*    */     }
/* 16 */     if (!hasIfChild) {
/* 17 */       buf.append("{");
/*    */     }
/* 19 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public boolean isElseIfStructure() {
/* 23 */     boolean result = false;
/* 24 */     if ((this.children != null) && (this.children.size() > 0) && 
/* 25 */       ((this.children.get(0) instanceof IfStatement))) {
/* 26 */       result = true;
/*    */     }
/*    */ 
/* 29 */     return result;
/*    */   }
/*    */ 
/*    */   public String endText()
/*    */   {
/* 34 */     StringBuilder buf = new StringBuilder();
/* 35 */     if (!isElseIfStructure()) {
/* 36 */       buf.append("\r\n}");
/*    */     }
/*    */ 
/* 39 */     return buf.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.ElseStatement
 * JD-Core Version:    0.6.0
 */