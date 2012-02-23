/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class StopFilter extends FilteringTokenFilter
/*     */ {
/*     */   private final CharArraySet stopWords;
/*  44 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*     */ 
/*     */   @Deprecated
/*     */   public StopFilter(boolean enablePositionIncrements, TokenStream input, Set<?> stopWords, boolean ignoreCase)
/*     */   {
/*  66 */     this(Version.LUCENE_30, enablePositionIncrements, input, stopWords, ignoreCase);
/*     */   }
/*     */ 
/*     */   public StopFilter(Version matchVersion, TokenStream input, Set<?> stopWords, boolean ignoreCase)
/*     */   {
/*  93 */     this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_29), input, stopWords, ignoreCase);
/*     */   }
/*     */ 
/*     */   private StopFilter(Version matchVersion, boolean enablePositionIncrements, TokenStream input, Set<?> stopWords, boolean ignoreCase)
/*     */   {
/* 100 */     super(enablePositionIncrements, input);
/* 101 */     this.stopWords = ((stopWords instanceof CharArraySet) ? (CharArraySet)stopWords : new CharArraySet(matchVersion, stopWords, ignoreCase));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public StopFilter(boolean enablePositionIncrements, TokenStream in, Set<?> stopWords)
/*     */   {
/* 116 */     this(Version.LUCENE_30, enablePositionIncrements, in, stopWords, false);
/*     */   }
/*     */ 
/*     */   public StopFilter(Version matchVersion, TokenStream in, Set<?> stopWords)
/*     */   {
/* 134 */     this(matchVersion, in, stopWords, false);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final Set<Object> makeStopSet(String[] stopWords)
/*     */   {
/* 148 */     return makeStopSet(Version.LUCENE_30, stopWords, false);
/*     */   }
/*     */ 
/*     */   public static final Set<Object> makeStopSet(Version matchVersion, String[] stopWords)
/*     */   {
/* 162 */     return makeStopSet(matchVersion, stopWords, false);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final Set<Object> makeStopSet(List<?> stopWords)
/*     */   {
/* 177 */     return makeStopSet(Version.LUCENE_30, stopWords, false);
/*     */   }
/*     */ 
/*     */   public static final Set<Object> makeStopSet(Version matchVersion, List<?> stopWords)
/*     */   {
/* 192 */     return makeStopSet(matchVersion, stopWords, false);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final Set<Object> makeStopSet(String[] stopWords, boolean ignoreCase)
/*     */   {
/* 204 */     return makeStopSet(Version.LUCENE_30, stopWords, ignoreCase);
/*     */   }
/*     */ 
/*     */   public static final Set<Object> makeStopSet(Version matchVersion, String[] stopWords, boolean ignoreCase)
/*     */   {
/* 215 */     CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.length, ignoreCase);
/* 216 */     stopSet.addAll(Arrays.asList(stopWords));
/* 217 */     return stopSet;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final Set<Object> makeStopSet(List<?> stopWords, boolean ignoreCase)
/*     */   {
/* 229 */     return makeStopSet(Version.LUCENE_30, stopWords, ignoreCase);
/*     */   }
/*     */ 
/*     */   public static final Set<Object> makeStopSet(Version matchVersion, List<?> stopWords, boolean ignoreCase)
/*     */   {
/* 240 */     CharArraySet stopSet = new CharArraySet(matchVersion, stopWords.size(), ignoreCase);
/* 241 */     stopSet.addAll(stopWords);
/* 242 */     return stopSet;
/*     */   }
/*     */ 
/*     */   protected boolean accept()
/*     */     throws IOException
/*     */   {
/* 250 */     return !this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static boolean getEnablePositionIncrementsVersionDefault(Version matchVersion)
/*     */   {
/* 263 */     return matchVersion.onOrAfter(Version.LUCENE_29);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.StopFilter
 * JD-Core Version:    0.6.0
 */