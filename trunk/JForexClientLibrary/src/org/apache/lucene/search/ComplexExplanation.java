/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ public class ComplexExplanation extends Explanation
/*    */ {
/*    */   private Boolean match;
/*    */ 
/*    */   public ComplexExplanation()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ComplexExplanation(boolean match, float value, String description)
/*    */   {
/* 32 */     super(value, description);
/* 33 */     this.match = Boolean.valueOf(match);
/*    */   }
/*    */ 
/*    */   public Boolean getMatch()
/*    */   {
/* 40 */     return this.match;
/*    */   }
/*    */ 
/*    */   public void setMatch(Boolean match)
/*    */   {
/* 45 */     this.match = match;
/*    */   }
/*    */ 
/*    */   public boolean isMatch()
/*    */   {
/* 57 */     Boolean m = getMatch();
/* 58 */     return null != m ? m.booleanValue() : super.isMatch();
/*    */   }
/*    */ 
/*    */   protected String getSummary()
/*    */   {
/* 63 */     if (null == getMatch()) {
/* 64 */       return super.getSummary();
/*    */     }
/* 66 */     return getValue() + " = " + (isMatch() ? "(MATCH) " : "(NON-MATCH) ") + getDescription();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ComplexExplanation
 * JD-Core Version:    0.6.0
 */