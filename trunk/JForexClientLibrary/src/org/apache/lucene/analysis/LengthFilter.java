/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ 
/*    */ public final class LengthFilter extends FilteringTokenFilter
/*    */ {
/*    */   private final int min;
/*    */   private final int max;
/* 35 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */ 
/*    */   public LengthFilter(boolean enablePositionIncrements, TokenStream in, int min, int max)
/*    */   {
/* 42 */     super(enablePositionIncrements, in);
/* 43 */     this.min = min;
/* 44 */     this.max = max;
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public LengthFilter(TokenStream in, int min, int max)
/*    */   {
/* 54 */     this(false, in, min, max);
/*    */   }
/*    */ 
/*    */   public boolean accept() throws IOException
/*    */   {
/* 59 */     int len = this.termAtt.length();
/* 60 */     return (len >= this.min) && (len <= this.max);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.LengthFilter
 * JD-Core Version:    0.6.0
 */