/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ 
/*    */ public final class CharReader extends CharStream
/*    */ {
/*    */   private final Reader input;
/*    */ 
/*    */   public static CharStream get(Reader input)
/*    */   {
/* 34 */     return (input instanceof CharStream) ? (CharStream)input : new CharReader(input);
/*    */   }
/*    */ 
/*    */   private CharReader(Reader in)
/*    */   {
/* 39 */     this.input = in;
/*    */   }
/*    */ 
/*    */   public int correctOffset(int currentOff)
/*    */   {
/* 44 */     return currentOff;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 49 */     this.input.close();
/*    */   }
/*    */ 
/*    */   public int read(char[] cbuf, int off, int len) throws IOException
/*    */   {
/* 54 */     return this.input.read(cbuf, off, len);
/*    */   }
/*    */ 
/*    */   public boolean markSupported()
/*    */   {
/* 59 */     return this.input.markSupported();
/*    */   }
/*    */ 
/*    */   public void mark(int readAheadLimit) throws IOException
/*    */   {
/* 64 */     this.input.mark(readAheadLimit);
/*    */   }
/*    */ 
/*    */   public void reset() throws IOException
/*    */   {
/* 69 */     this.input.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CharReader
 * JD-Core Version:    0.6.0
 */