/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class WhitespaceTokenizer extends CharTokenizer
/*     */ {
/*     */   public WhitespaceTokenizer(Version matchVersion, Reader in)
/*     */   {
/*  48 */     super(matchVersion, in);
/*     */   }
/*     */ 
/*     */   public WhitespaceTokenizer(Version matchVersion, AttributeSource source, Reader in)
/*     */   {
/*  62 */     super(matchVersion, source, in);
/*     */   }
/*     */ 
/*     */   public WhitespaceTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/*  78 */     super(matchVersion, factory, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public WhitespaceTokenizer(Reader in)
/*     */   {
/*  89 */     super(in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public WhitespaceTokenizer(AttributeSource source, Reader in)
/*     */   {
/* 100 */     super(source, in);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public WhitespaceTokenizer(AttributeSource.AttributeFactory factory, Reader in)
/*     */   {
/* 112 */     super(factory, in);
/*     */   }
/*     */ 
/*     */   protected boolean isTokenChar(int c)
/*     */   {
/* 119 */     return !Character.isWhitespace(c);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.WhitespaceTokenizer
 * JD-Core Version:    0.6.0
 */