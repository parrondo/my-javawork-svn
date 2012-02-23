/*    */ package org.apache.lucene.analysis.standard;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.TokenFilter;
/*    */ import org.apache.lucene.analysis.TokenStream;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*    */ 
/*    */ public class ClassicFilter extends TokenFilter
/*    */ {
/* 34 */   private static final String APOSTROPHE_TYPE = ClassicTokenizer.TOKEN_TYPES[1];
/* 35 */   private static final String ACRONYM_TYPE = ClassicTokenizer.TOKEN_TYPES[2];
/*    */ 
/* 38 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/* 39 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */ 
/*    */   public ClassicFilter(TokenStream in)
/*    */   {
/* 31 */     super(in);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken()
/*    */     throws IOException
/*    */   {
/* 47 */     if (!this.input.incrementToken()) {
/* 48 */       return false;
/*    */     }
/*    */ 
/* 51 */     char[] buffer = this.termAtt.buffer();
/* 52 */     int bufferLength = this.termAtt.length();
/* 53 */     String type = this.typeAtt.type();
/*    */ 
/* 55 */     if ((type == APOSTROPHE_TYPE) && (bufferLength >= 2) && (buffer[(bufferLength - 2)] == '\'') && ((buffer[(bufferLength - 1)] == 's') || (buffer[(bufferLength - 1)] == 'S')))
/*    */     {
/* 60 */       this.termAtt.setLength(bufferLength - 2);
/* 61 */     } else if (type == ACRONYM_TYPE) {
/* 62 */       int upto = 0;
/* 63 */       for (int i = 0; i < bufferLength; i++) {
/* 64 */         char c = buffer[i];
/* 65 */         if (c != '.')
/* 66 */           buffer[(upto++)] = c;
/*    */       }
/* 68 */       this.termAtt.setLength(upto);
/*    */     }
/*    */ 
/* 71 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.ClassicFilter
 * JD-Core Version:    0.6.0
 */