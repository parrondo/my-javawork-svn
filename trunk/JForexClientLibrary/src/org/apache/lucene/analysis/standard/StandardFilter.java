/*    */ package org.apache.lucene.analysis.standard;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.TokenFilter;
/*    */ import org.apache.lucene.analysis.TokenStream;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*    */ import org.apache.lucene.util.Version;
/*    */ 
/*    */ public class StandardFilter extends TokenFilter
/*    */ {
/*    */   private final Version matchVersion;
/* 45 */   private static final String APOSTROPHE_TYPE = ClassicTokenizer.TOKEN_TYPES[1];
/* 46 */   private static final String ACRONYM_TYPE = ClassicTokenizer.TOKEN_TYPES[2];
/*    */ 
/* 49 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/* 50 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */ 
/*    */   @Deprecated
/*    */   public StandardFilter(TokenStream in)
/*    */   {
/* 37 */     this(Version.LUCENE_30, in);
/*    */   }
/*    */ 
/*    */   public StandardFilter(Version matchVersion, TokenStream in) {
/* 41 */     super(in);
/* 42 */     this.matchVersion = matchVersion;
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken()
/*    */     throws IOException
/*    */   {
/* 54 */     if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
/* 55 */       return this.input.incrementToken();
/*    */     }
/* 57 */     return incrementTokenClassic();
/*    */   }
/*    */ 
/*    */   public final boolean incrementTokenClassic() throws IOException {
/* 61 */     if (!this.input.incrementToken()) {
/* 62 */       return false;
/*    */     }
/*    */ 
/* 65 */     char[] buffer = this.termAtt.buffer();
/* 66 */     int bufferLength = this.termAtt.length();
/* 67 */     String type = this.typeAtt.type();
/*    */ 
/* 69 */     if ((type == APOSTROPHE_TYPE) && (bufferLength >= 2) && (buffer[(bufferLength - 2)] == '\'') && ((buffer[(bufferLength - 1)] == 's') || (buffer[(bufferLength - 1)] == 'S')))
/*    */     {
/* 74 */       this.termAtt.setLength(bufferLength - 2);
/* 75 */     } else if (type == ACRONYM_TYPE) {
/* 76 */       int upto = 0;
/* 77 */       for (int i = 0; i < bufferLength; i++) {
/* 78 */         char c = buffer[i];
/* 79 */         if (c != '.')
/* 80 */           buffer[(upto++)] = c;
/*    */       }
/* 82 */       this.termAtt.setLength(upto);
/*    */     }
/*    */ 
/* 85 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.StandardFilter
 * JD-Core Version:    0.6.0
 */