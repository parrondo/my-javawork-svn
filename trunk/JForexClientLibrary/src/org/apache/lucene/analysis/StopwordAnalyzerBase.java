/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public abstract class StopwordAnalyzerBase extends ReusableAnalyzerBase
/*     */ {
/*     */   protected final CharArraySet stopwords;
/*     */   protected final Version matchVersion;
/*     */ 
/*     */   public Set<?> getStopwordSet()
/*     */   {
/*  49 */     return this.stopwords;
/*     */   }
/*     */ 
/*     */   protected StopwordAnalyzerBase(Version version, Set<?> stopwords)
/*     */   {
/*  61 */     this.matchVersion = version;
/*     */ 
/*  63 */     this.stopwords = (stopwords == null ? CharArraySet.EMPTY_SET : CharArraySet.unmodifiableSet(CharArraySet.copy(version, stopwords)));
/*     */   }
/*     */ 
/*     */   protected StopwordAnalyzerBase(Version version)
/*     */   {
/*  74 */     this(version, null);
/*     */   }
/*     */ 
/*     */   protected static CharArraySet loadStopwordSet(boolean ignoreCase, Class<? extends ReusableAnalyzerBase> aClass, String resource, String comment)
/*     */     throws IOException
/*     */   {
/*  98 */     Set wordSet = WordlistLoader.getWordSet(aClass, resource, comment);
/*     */ 
/* 100 */     CharArraySet set = new CharArraySet(Version.LUCENE_31, wordSet.size(), ignoreCase);
/* 101 */     set.addAll(wordSet);
/* 102 */     return set;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.StopwordAnalyzerBase
 * JD-Core Version:    0.6.0
 */