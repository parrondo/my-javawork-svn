/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class CharFilter extends CharStream
/*    */ {
/*    */   protected CharStream input;
/*    */ 
/*    */   protected CharFilter(CharStream in)
/*    */   {
/* 33 */     this.input = in;
/*    */   }
/*    */ 
/*    */   protected int correct(int currentOff)
/*    */   {
/* 43 */     return currentOff;
/*    */   }
/*    */ 
/*    */   public final int correctOffset(int currentOff)
/*    */   {
/* 52 */     return this.input.correctOffset(correct(currentOff));
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 57 */     this.input.close();
/*    */   }
/*    */ 
/*    */   public int read(char[] cbuf, int off, int len) throws IOException
/*    */   {
/* 62 */     return this.input.read(cbuf, off, len);
/*    */   }
/*    */ 
/*    */   public boolean markSupported()
/*    */   {
/* 67 */     return this.input.markSupported();
/*    */   }
/*    */ 
/*    */   public void mark(int readAheadLimit) throws IOException
/*    */   {
/* 72 */     this.input.mark(readAheadLimit);
/*    */   }
/*    */ 
/*    */   public void reset() throws IOException
/*    */   {
/* 77 */     this.input.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CharFilter
 * JD-Core Version:    0.6.0
 */