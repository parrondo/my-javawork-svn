/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.util.CharacterUtils;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public final class LowerCaseFilter extends TokenFilter
/*    */ {
/*    */   private final CharacterUtils charUtils;
/* 37 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */ 
/*    */   public LowerCaseFilter(Version matchVersion, TokenStream in)
/*    */   {
/* 46 */     super(in);
/* 47 */     this.charUtils = CharacterUtils.getInstance(matchVersion);
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public LowerCaseFilter(TokenStream in)
/*    */   {
/* 55 */     this(Version.LUCENE_30, in);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken() throws IOException
/*    */   {
/* 60 */     if (this.input.incrementToken()) {
/* 61 */       char[] buffer = this.termAtt.buffer();
/* 62 */       int length = this.termAtt.length();
/* 63 */       for (int i = 0; i < length; ) {
/* 64 */         i += Character.toChars(Character.toLowerCase(this.charUtils.codePointAt(buffer, i)), buffer, i);
/*    */       }
/*    */ 
/* 68 */       return true;
/*    */     }
/* 70 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LowerCaseFilter
 * JD-Core Version:    0.6.0
 */