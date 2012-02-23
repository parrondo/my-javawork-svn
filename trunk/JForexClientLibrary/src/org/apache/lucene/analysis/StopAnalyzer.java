/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class StopAnalyzer extends StopwordAnalyzerBase
/*     */ {
/*     */   public static final Set<?> ENGLISH_STOP_WORDS_SET;
/*     */ 
/*     */   public StopAnalyzer(Version matchVersion)
/*     */   {
/*  66 */     this(matchVersion, ENGLISH_STOP_WORDS_SET);
/*     */   }
/*     */ 
/*     */   public StopAnalyzer(Version matchVersion, Set<?> stopWords)
/*     */   {
/*  73 */     super(matchVersion, stopWords);
/*     */   }
/*     */ 
/*     */   public StopAnalyzer(Version matchVersion, File stopwordsFile)
/*     */     throws IOException
/*     */   {
/*  81 */     this(matchVersion, WordlistLoader.getWordSet(stopwordsFile));
/*     */   }
/*     */ 
/*     */   public StopAnalyzer(Version matchVersion, Reader stopwords)
/*     */     throws IOException
/*     */   {
/*  89 */     this(matchVersion, WordlistLoader.getWordSet(stopwords));
/*     */   }
/*     */ 
/*     */   protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader)
/*     */   {
/* 104 */     Tokenizer source = new LowerCaseTokenizer(this.matchVersion, reader);
/* 105 */     return new ReusableAnalyzerBase.TokenStreamComponents(source, new StopFilter(this.matchVersion, source, this.stopwords));
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  48 */     List stopWords = Arrays.asList(new String[] { "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with" });
/*     */ 
/*  55 */     CharArraySet stopSet = new CharArraySet(Version.LUCENE_CURRENT, stopWords.size(), false);
/*     */ 
/*  57 */     stopSet.addAll(stopWords);
/*  58 */     ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.StopAnalyzer
 * JD-Core Version:    0.6.0
 */