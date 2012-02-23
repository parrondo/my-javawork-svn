/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class TokenFilter extends TokenStream
/*    */ {
/*    */   protected final TokenStream input;
/*    */ 
/*    */   protected TokenFilter(TokenStream input)
/*    */   {
/* 33 */     super(input);
/* 34 */     this.input = input;
/*    */   }
/*    */ 
/*    */   public void end()
/*    */     throws IOException
/*    */   {
/* 42 */     this.input.end();
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 48 */     this.input.close();
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */     throws IOException
/*    */   {
/* 54 */     this.input.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.TokenFilter
 * JD-Core Version:    0.6.0
 */