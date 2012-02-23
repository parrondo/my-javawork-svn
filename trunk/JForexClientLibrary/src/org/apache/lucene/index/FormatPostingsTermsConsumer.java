/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.util.ArrayUtil;
/*    */ 
/*    */ abstract class FormatPostingsTermsConsumer
/*    */ {
/*    */   char[] termBuffer;
/*    */ 
/*    */   abstract FormatPostingsDocsConsumer addTerm(char[] paramArrayOfChar, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   FormatPostingsDocsConsumer addTerm(String text)
/*    */     throws IOException
/*    */   {
/* 37 */     int len = text.length();
/* 38 */     if ((this.termBuffer == null) || (this.termBuffer.length < 1 + len))
/* 39 */       this.termBuffer = new char[ArrayUtil.oversize(1 + len, 2)];
/* 40 */     text.getChars(0, len, this.termBuffer, 0);
/* 41 */     this.termBuffer[len] = 65535;
/* 42 */     return addTerm(this.termBuffer, 0);
/*    */   }
/*    */ 
/*    */   abstract void finish()
/*    */     throws IOException;
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsTermsConsumer
 * JD-Core Version:    0.6.0
 */