/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public final class WhitespaceAnalyzer extends ReusableAnalyzerBase
/*    */ {
/*    */   private final Version matchVersion;
/*    */ 
/*    */   public WhitespaceAnalyzer(Version matchVersion)
/*    */   {
/* 45 */     this.matchVersion = matchVersion;
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public WhitespaceAnalyzer()
/*    */   {
/* 54 */     this(Version.LUCENE_30);
/*    */   }
/*    */ 
/*    */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*    */   {
/* 60 */     return new ReusableAnalyzerBase.TokenStreamComponents(new WhitespaceTokenizer(this.matchVersion, reader));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.WhitespaceAnalyzer
 * JD-Core Version:    0.6.0
 */