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
/*     */ public final class ClassicAnalyzer extends StopwordAnalyzerBase
/*     */ {
/*     */   public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
/*  59 */   private int maxTokenLength = 255;
/*     */   private final boolean replaceInvalidAcronym;
/*  69 */   public static final Set<?> STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
/*     */ 
/*     */   public ClassicAnalyzer(Version matchVersion, Set<?> stopWords)
/*     */   {
/*  76 */     super(matchVersion, stopWords);
/*  77 */     this.replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_24);
/*     */   }
/*     */ 
/*     */   public ClassicAnalyzer(Version matchVersion)
/*     */   {
/*  86 */     this(matchVersion, STOP_WORDS_SET);
/*     */   }
/*     */ 
/*     */   public ClassicAnalyzer(Version matchVersion, File stopwords)
/*     */     throws IOException
/*     */   {
/*  95 */     this(matchVersion, WordlistLoader.getWordSet(stopwords));
/*     */   }
/*     */ 
/*     */   public ClassicAnalyzer(Version matchVersion, Reader stopwords)
/*     */     throws IOException
/*     */   {
/* 104 */     this(matchVersion, WordlistLoader.getWordSet(stopwords));
/*     */   }
/*     */ 
/*     */   public void setMaxTokenLength(int length)
/*     */   {
/* 114 */     this.maxTokenLength = length;
/*     */   }
/*     */ 
/*     */   public int getMaxTokenLength()
/*     */   {
/* 121 */     return this.maxTokenLength;
/*     */   }
/*     */ 
/*     */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*     */   {
/* 126 */     ClassicTokenizer src = new ClassicTokenizer(this.matchVersion, reader);
/* 127 */     src.setMaxTokenLength(this.maxTokenLength);
/* 128 */     src.setReplaceInvalidAcronym(this.replaceInvalidAcronym);
/* 129 */     TokenStream tok = new ClassicFilter(src);
/* 130 */     tok = new LowerCaseFilter(this.matchVersion, tok);
/* 131 */     tok = new StopFilter(this.matchVersion, tok, this.stopwords);
/* 132 */     return new ReusableAnalyzerBase.TokenStreamComponents(src, tok, src)
/*     */     {
/*     */       protected boolean reset(Reader reader) throws IOException {
/* 135 */         this.val$src.setMaxTokenLength(ClassicAnalyzer.this.maxTokenLength);
/* 136 */         return super.reset(reader);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.ClassicAnalyzer
 * JD-Core Version:    0.6.0
 */