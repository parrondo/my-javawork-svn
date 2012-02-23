/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public final class SimpleAnalyzer extends ReusableAnalyzerBase
/*    */ {
/*    */   private final Version matchVersion;
/*    */ 
/*    */   public SimpleAnalyzer(Version matchVersion)
/*    */   {
/* 45 */     this.matchVersion = matchVersion;
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public SimpleAnalyzer()
/*    */   {
/* 53 */     this(Version.LUCENE_30);
/*    */   }
/*    */ 
/*    */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*    */   {
/* 58 */     return new ReusableAnalyzerBase.TokenStreamComponents(new LowerCaseTokenizer(this.matchVersion, reader));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.SimpleAnalyzer
 * JD-Core Version:    0.6.0
 */