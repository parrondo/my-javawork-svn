/*    */ package org.eclipse.jdt.internal.compiler.parser;
/*    */ 
/*    */ public class NLSTag
/*    */ {
/*    */   public int start;
/*    */   public int end;
/*    */   public int lineNumber;
/*    */   public int index;
/*    */ 
/*    */   public NLSTag(int start, int end, int lineNumber, int index)
/*    */   {
/* 21 */     this.start = start;
/* 22 */     this.end = end;
/* 23 */     this.lineNumber = lineNumber;
/* 24 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 28 */     return "NLSTag(" + this.start + "," + this.end + "," + this.lineNumber + ")";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.NLSTag
 * JD-Core Version:    0.6.0
 */