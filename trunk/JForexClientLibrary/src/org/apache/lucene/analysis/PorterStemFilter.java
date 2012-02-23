/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
/*    */ 
/*    */ public final class PorterStemFilter extends TokenFilter
/*    */ {
/* 50 */   private final PorterStemmer stemmer = new PorterStemmer();
/* 51 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 52 */   private final KeywordAttribute keywordAttr = (KeywordAttribute)addAttribute(KeywordAttribute.class);
/*    */ 
/*    */   public PorterStemFilter(TokenStream in) {
/* 55 */     super(in);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken() throws IOException
/*    */   {
/* 60 */     if (!this.input.incrementToken()) {
/* 61 */       return false;
/*    */     }
/* 63 */     if ((!this.keywordAttr.isKeyword()) && (this.stemmer.stem(this.termAtt.buffer(), 0, this.termAtt.length())))
/* 64 */       this.termAtt.copyBuffer(this.stemmer.getResultBuffer(), 0, this.stemmer.getResultLength());
/* 65 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.PorterStemFilter
 * JD-Core Version:    0.6.0
 */