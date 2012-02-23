/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class LowerCaseTokenizer extends LetterTokenizer
/*     */ {
/*     */   public LowerCaseTokenizer(Version matchVersion, Reader in)
/*     */   {
/*  58 */     super(matchVersion, in);
/*     */   }
/*     */ 
/*     */   public LowerCaseTokenizer(Version matchVersion, AttributeSource source, Reader in)
/*     */   {
/*  72 */     super(matchVersion, source, in);
/*     */   }
/*     */ 
/*     */   public LowerCaseTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/*  87 */     super(matchVersion, factory, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LowerCaseTokenizer(Reader in)
/*     */   {
/*  98 */     super(Version.LUCENE_30, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LowerCaseTokenizer(AttributeSource source, Reader in)
/*     */   {
/* 109 */     super(Version.LUCENE_30, source, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LowerCaseTokenizer(AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/* 121 */     super(Version.LUCENE_30, factory, in);
/*     */   }
/*     */ 
/*     */   protected int normalize(int c)
/*     */   {
/* 128 */     return Character.toLowerCase(c);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LowerCaseTokenizer
 * JD-Core Version:    0.6.0
 */