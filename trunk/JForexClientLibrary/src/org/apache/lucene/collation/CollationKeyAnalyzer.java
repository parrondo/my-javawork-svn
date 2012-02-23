/*     */ package org.apache.lucene.collation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.text.Collator;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.analysis.KeywordTokenizer;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.analysis.Tokenizer;
/*     */ 
/*     */ public final class CollationKeyAnalyzer extends Analyzer
/*     */ {
/*     */   private Collator collator;
/*     */ 
/*     */   public CollationKeyAnalyzer(Collator collator)
/*     */   {
/*  83 */     this.collator = collator;
/*     */   }
/*     */ 
/*     */   public TokenStream tokenStream(String fieldName, Reader reader)
/*     */   {
/*  88 */     TokenStream result = new KeywordTokenizer(reader);
/*  89 */     result = new CollationKeyFilter(result, this.collator);
/*  90 */     return result;
/*     */   }
/*     */ 
/*     */   public TokenStream reusableTokenStream(String fieldName, Reader reader)
/*     */     throws IOException
/*     */   {
/* 102 */     SavedStreams streams = (SavedStreams)getPreviousTokenStream();
/* 103 */     if (streams == null) {
/* 104 */       streams = new SavedStreams(null);
/* 105 */       streams.source = new KeywordTokenizer(reader);
/* 106 */       streams.result = new CollationKeyFilter(streams.source, this.collator);
/* 107 */       setPreviousTokenStream(streams);
/*     */     } else {
/* 109 */       streams.source.reset(reader);
/*     */     }
/* 111 */     return streams.result;
/*     */   }
/*     */ 
/*     */   private class SavedStreams
/*     */   {
/*     */     Tokenizer source;
/*     */     TokenStream result;
/*     */ 
/*     */     private SavedStreams()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.collation.CollationKeyAnalyzer
 * JD-Core Version:    0.6.0
 */