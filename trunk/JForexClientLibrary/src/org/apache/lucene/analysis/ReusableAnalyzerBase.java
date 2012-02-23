/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ 
/*     */ public abstract class ReusableAnalyzerBase extends Analyzer
/*     */ {
/*     */   protected abstract TokenStreamComponents createComponents(String paramString, Reader paramReader);
/*     */ 
/*     */   public final TokenStream reusableTokenStream(String fieldName, Reader reader)
/*     */     throws IOException
/*     */   {
/*  69 */     TokenStreamComponents streamChain = (TokenStreamComponents)getPreviousTokenStream();
/*     */ 
/*  71 */     Reader r = initReader(reader);
/*  72 */     if ((streamChain == null) || (!streamChain.reset(r))) {
/*  73 */       streamChain = createComponents(fieldName, r);
/*  74 */       setPreviousTokenStream(streamChain);
/*     */     }
/*  76 */     return streamChain.getTokenStream();
/*     */   }
/*     */ 
/*     */   public final TokenStream tokenStream(String fieldName, Reader reader)
/*     */   {
/*  92 */     return createComponents(fieldName, initReader(reader)).getTokenStream();
/*     */   }
/*     */ 
/*     */   protected Reader initReader(Reader reader)
/*     */   {
/*  99 */     return reader;
/*     */   }
/*     */ 
/*     */   public static class TokenStreamComponents
/*     */   {
/*     */     protected final Tokenizer source;
/*     */     protected final TokenStream sink;
/*     */ 
/*     */     public TokenStreamComponents(Tokenizer source, TokenStream result)
/*     */     {
/* 124 */       this.source = source;
/* 125 */       this.sink = result;
/*     */     }
/*     */ 
/*     */     public TokenStreamComponents(Tokenizer source)
/*     */     {
/* 135 */       this.source = source;
/* 136 */       this.sink = source;
/*     */     }
/*     */ 
/*     */     protected boolean reset(Reader reader)
/*     */       throws IOException
/*     */     {
/* 154 */       this.source.reset(reader);
/* 155 */       return true;
/*     */     }
/*     */ 
/*     */     protected TokenStream getTokenStream()
/*     */     {
/* 164 */       return this.sink;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.ReusableAnalyzerBase
 * JD-Core Version:    0.6.0
 */