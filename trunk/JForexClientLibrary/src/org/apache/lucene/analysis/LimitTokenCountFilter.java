/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public final class LimitTokenCountFilter extends TokenFilter
/*    */ {
/*    */   private final int maxTokenCount;
/* 29 */   private int tokenCount = 0;
/*    */ 
/*    */   public LimitTokenCountFilter(TokenStream in, int maxTokenCount)
/*    */   {
/* 35 */     super(in);
/* 36 */     this.maxTokenCount = maxTokenCount;
/*    */   }
/*    */ 
/*    */   public boolean incrementToken() throws IOException
/*    */   {
/* 41 */     if ((this.tokenCount < this.maxTokenCount) && (this.input.incrementToken())) {
/* 42 */       this.tokenCount += 1;
/* 43 */       return true;
/*    */     }
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */   public void reset() throws IOException
/*    */   {
/* 50 */     super.reset();
/* 51 */     this.tokenCount = 0;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LimitTokenCountFilter
 * JD-Core Version:    0.6.0
 */