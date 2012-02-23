/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.analysis.LowerCaseFilter;
/*     */ import org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents;
/*     */ import org.apache.lucene.analysis.StopAnalyzer;
/*     */ import org.apache.lucene.analysis.StopFilter;
/*     */ import org.apache.lucene.analysis.StopwordAnalyzerBase;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.analysis.Tokenizer;
/*     */ import org.apache.lucene.analysis.WordlistLoader;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class StandardAnalyzer extends StopwordAnalyzerBase
/*     */ {
/*     */   public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
/*  55 */   private int maxTokenLength = 255;
/*     */   private final boolean replaceInvalidAcronym;
/*  65 */   public static final Set<?> STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
/*     */ 
/*     */   public StandardAnalyzer(Version matchVersion, Set<?> stopWords)
/*     */   {
/*  72 */     super(matchVersion, stopWords);
/*  73 */     this.replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_24);
/*     */   }
/*     */ 
/*     */   public StandardAnalyzer(Version matchVersion)
/*     */   {
/*  82 */     this(matchVersion, STOP_WORDS_SET);
/*     */   }
/*     */ 
/*     */   public StandardAnalyzer(Version matchVersion, File stopwords)
/*     */     throws IOException
/*     */   {
/*  91 */     this(matchVersion, WordlistLoader.getWordSet(stopwords));
/*     */   }
/*     */ 
/*     */   public StandardAnalyzer(Version matchVersion, Reader stopwords)
/*     */     throws IOException
/*     */   {
/* 100 */     this(matchVersion, WordlistLoader.getWordSet(stopwords));
/*     */   }
/*     */ 
/*     */   public void setMaxTokenLength(int length)
/*     */   {
/* 110 */     this.maxTokenLength = length;
/*     */   }
/*     */ 
/*     */   public int getMaxTokenLength()
/*     */   {
/* 117 */     return this.maxTokenLength;
/*     */   }
/*     */ 
/*     */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*     */   {
/* 122 */     StandardTokenizer src = new StandardTokenizer(this.matchVersion, reader);
/* 123 */     src.setMaxTokenLength(this.maxTokenLength);
/* 124 */     src.setReplaceInvalidAcronym(this.replaceInvalidAcronym);
/* 125 */     TokenStream tok = new StandardFilter(this.matchVersion, src);
/* 126 */     tok = new LowerCaseFilter(this.matchVersion, tok);
/* 127 */     tok = new StopFilter(this.matchVersion, tok, this.stopwords);
/* 128 */     return new ReusableAnalyzerBase.TokenStreamComponents(src, tok, src)
/*     */     {
/*     */       protected boolean reset(Reader reader) throws IOException {
/* 131 */         this.val$src.setMaxTokenLength(StandardAnalyzer.this.maxTokenLength);
/* 132 */         return super.reset(reader);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.StandardAnalyzer
 * JD-Core Version:    0.6.0
 */