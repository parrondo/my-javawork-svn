/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.document.Fieldable;
/*    */ 
/*    */ public final class LimitTokenCountAnalyzer extends Analyzer
/*    */ {
/*    */   private final Analyzer delegate;
/*    */   private final int maxTokenCount;
/*    */ 
/*    */   public LimitTokenCountAnalyzer(Analyzer delegate, int maxTokenCount)
/*    */   {
/* 37 */     this.delegate = delegate;
/* 38 */     this.maxTokenCount = maxTokenCount;
/*    */   }
/*    */ 
/*    */   public TokenStream tokenStream(String fieldName, Reader reader)
/*    */   {
/* 43 */     return new LimitTokenCountFilter(this.delegate.tokenStream(fieldName, reader), this.maxTokenCount);
/*    */   }
/*    */ 
/*    */   public TokenStream reusableTokenStream(String fieldName, Reader reader)
/*    */     throws IOException
/*    */   {
/* 50 */     return new LimitTokenCountFilter(this.delegate.reusableTokenStream(fieldName, reader), this.maxTokenCount);
/*    */   }
/*    */ 
/*    */   public int getPositionIncrementGap(String fieldName)
/*    */   {
/* 57 */     return this.delegate.getPositionIncrementGap(fieldName);
/*    */   }
/*    */ 
/*    */   public int getOffsetGap(Fieldable field)
/*    */   {
/* 62 */     return this.delegate.getOffsetGap(field);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 67 */     return "LimitTokenCountAnalyzer(" + this.delegate.toString() + ", maxTokenCount=" + this.maxTokenCount + ")";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LimitTokenCountAnalyzer
 * JD-Core Version:    0.6.0
 */