/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Set;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public final class KeywordMarkerFilter extends TokenFilter
/*    */ {
/* 36 */   private final KeywordAttribute keywordAttr = (KeywordAttribute)addAttribute(KeywordAttribute.class);
/* 37 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */   private final CharArraySet keywordSet;
/*    */ 
/*    */   public KeywordMarkerFilter(TokenStream in, CharArraySet keywordSet)
/*    */   {
/* 52 */     super(in);
/* 53 */     this.keywordSet = keywordSet;
/*    */   }
/*    */ 
/*    */   public KeywordMarkerFilter(TokenStream in, Set<?> keywordSet)
/*    */   {
/* 67 */     this(in, (keywordSet instanceof CharArraySet) ? (CharArraySet)keywordSet : CharArraySet.copy(Version.LUCENE_31, keywordSet));
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken()
/*    */     throws IOException
/*    */   {
/* 73 */     if (this.input.incrementToken()) {
/* 74 */       if (this.keywordSet.contains(this.termAtt.buffer(), 0, this.termAtt.length())) {
/* 75 */         this.keywordAttr.setKeyword(true);
/*    */       }
/* 77 */       return true;
/*    */     }
/* 79 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.KeywordMarkerFilter
 * JD-Core Version:    0.6.0
 */