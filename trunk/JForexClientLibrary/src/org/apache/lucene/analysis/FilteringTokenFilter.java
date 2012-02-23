/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*    */ 
/*    */ public abstract class FilteringTokenFilter extends TokenFilter
/*    */ {
/* 33 */   private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)addAttribute(PositionIncrementAttribute.class);
/*    */   private boolean enablePositionIncrements;
/*    */ 
/*    */   public FilteringTokenFilter(boolean enablePositionIncrements, TokenStream input)
/*    */   {
/* 37 */     super(input);
/* 38 */     this.enablePositionIncrements = enablePositionIncrements;
/*    */   }
/*    */ 
/*    */   protected abstract boolean accept() throws IOException;
/*    */ 
/*    */   public final boolean incrementToken() throws IOException
/*    */   {
/* 46 */     if (this.enablePositionIncrements) {
/* 47 */       int skippedPositions = 0;
/* 48 */       while (this.input.incrementToken()) {
/* 49 */         if (accept()) {
/* 50 */           if (skippedPositions != 0) {
/* 51 */             this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + skippedPositions);
/*    */           }
/* 53 */           return true;
/*    */         }
/* 55 */         skippedPositions += this.posIncrAtt.getPositionIncrement();
/*    */       }
/*    */     } else {
/* 58 */       while (this.input.incrementToken()) {
/* 59 */         if (accept()) {
/* 60 */           return true;
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 65 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean getEnablePositionIncrements()
/*    */   {
/* 72 */     return this.enablePositionIncrements;
/*    */   }
/*    */ 
/*    */   public void setEnablePositionIncrements(boolean enable)
/*    */   {
/* 92 */     this.enablePositionIncrements = enable;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.FilteringTokenFilter
 * JD-Core Version:    0.6.0
 */