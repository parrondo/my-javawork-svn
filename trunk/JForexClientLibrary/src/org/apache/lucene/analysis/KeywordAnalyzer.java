/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.Reader;
/*    */ 
/*    */ public final class KeywordAnalyzer extends ReusableAnalyzerBase
/*    */ {
/*    */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*    */   {
/* 32 */     return new ReusableAnalyzerBase.TokenStreamComponents(new KeywordTokenizer(reader));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.KeywordAnalyzer
 * JD-Core Version:    0.6.0
 */