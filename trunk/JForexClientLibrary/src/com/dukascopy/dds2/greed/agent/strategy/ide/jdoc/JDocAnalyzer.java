/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.jdoc;
/*    */ 
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.analysis.Analyzer;
/*    */ import org.apache.lucene.analysis.LetterTokenizer;
/*    */ import org.apache.lucene.analysis.LowerCaseFilter;
/*    */ import org.apache.lucene.analysis.TokenStream;
/*    */ import org.apache.lucene.analysis.standard.StandardFilter;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public class JDocAnalyzer extends Analyzer
/*    */ {
/*    */   public TokenStream tokenStream(String fieldname, Reader reader)
/*    */   {
/* 18 */     TokenStream ts = new LetterTokenizer(Version.LUCENE_34, reader);
/* 19 */     ts = new StandardFilter(Version.LUCENE_34, ts);
/* 20 */     ts = new LowerCaseFilter(Version.LUCENE_34, ts);
/* 21 */     return ts;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocAnalyzer
 * JD-Core Version:    0.6.0
 */