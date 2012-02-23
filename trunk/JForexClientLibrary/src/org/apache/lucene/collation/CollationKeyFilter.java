/*     */ package org.apache.lucene.collation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.CollationKey;
/*     */ import java.text.Collator;
/*     */ import org.apache.lucene.analysis.TokenFilter;
/*     */ import org.apache.lucene.analysis.TokenStream;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.util.IndexableBinaryStringTools;
/*     */ 
/*     */ public final class CollationKeyFilter extends TokenFilter
/*     */ {
/*     */   private final Collator collator;
/*  77 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*     */ 
/*     */   public CollationKeyFilter(TokenStream input, Collator collator)
/*     */   {
/*  84 */     super(input);
/*     */ 
/*  87 */     this.collator = ((Collator)collator.clone());
/*     */   }
/*     */ 
/*     */   public boolean incrementToken() throws IOException
/*     */   {
/*  92 */     if (this.input.incrementToken()) {
/*  93 */       byte[] collationKey = this.collator.getCollationKey(this.termAtt.toString()).toByteArray();
/*  94 */       int encodedLength = IndexableBinaryStringTools.getEncodedLength(collationKey, 0, collationKey.length);
/*     */ 
/*  96 */       this.termAtt.resizeBuffer(encodedLength);
/*  97 */       this.termAtt.setLength(encodedLength);
/*  98 */       IndexableBinaryStringTools.encode(collationKey, 0, collationKey.length, this.termAtt.buffer(), 0, encodedLength);
/*     */ 
/* 100 */       return true;
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.collation.CollationKeyFilter
 * JD-Core Version:    0.6.0
 */