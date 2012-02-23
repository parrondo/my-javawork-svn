/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public class LetterTokenizer extends CharTokenizer
/*     */ {
/*     */   public LetterTokenizer(Version matchVersion, Reader in)
/*     */   {
/*  56 */     super(matchVersion, in);
/*     */   }
/*     */ 
/*     */   public LetterTokenizer(Version matchVersion, AttributeSource source, Reader in)
/*     */   {
/*  70 */     super(matchVersion, source, in);
/*     */   }
/*     */ 
/*     */   public LetterTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/*  85 */     super(matchVersion, factory, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LetterTokenizer(Reader in)
/*     */   {
/*  96 */     super(Version.LUCENE_30, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LetterTokenizer(AttributeSource source, Reader in)
/*     */   {
/* 107 */     super(Version.LUCENE_30, source, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public LetterTokenizer(AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/* 119 */     super(Version.LUCENE_30, factory, in);
/*     */   }
/*     */ 
/*     */   protected boolean isTokenChar(int c)
/*     */   {
/* 126 */     return Character.isLetter(c);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LetterTokenizer
 * JD-Core Version:    0.6.0
 */