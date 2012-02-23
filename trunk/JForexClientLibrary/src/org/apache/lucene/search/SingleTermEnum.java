/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.index.Term;
/*    */ 
/*    */ public class SingleTermEnum extends FilteredTermEnum
/*    */ {
/*    */   private Term singleTerm;
/* 34 */   private boolean endEnum = false;
/*    */ 
/*    */   public SingleTermEnum(IndexReader reader, Term singleTerm)
/*    */     throws IOException
/*    */   {
/* 44 */     this.singleTerm = singleTerm;
/* 45 */     setEnum(reader.terms(singleTerm));
/*    */   }
/*    */ 
/*    */   public float difference()
/*    */   {
/* 50 */     return 1.0F;
/*    */   }
/*    */ 
/*    */   protected boolean endEnum()
/*    */   {
/* 55 */     return this.endEnum;
/*    */   }
/*    */ 
/*    */   protected boolean termCompare(Term term)
/*    */   {
/* 60 */     if (term.equals(this.singleTerm)) {
/* 61 */       return true;
/*    */     }
/* 63 */     this.endEnum = true;
/* 64 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SingleTermEnum
 * JD-Core Version:    0.6.0
 */