/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.index.Term;
/*    */ 
/*    */ public class PrefixTermEnum extends FilteredTermEnum
/*    */ {
/*    */   private final Term prefix;
/* 36 */   private boolean endEnum = false;
/*    */ 
/*    */   public PrefixTermEnum(IndexReader reader, Term prefix) throws IOException {
/* 39 */     this.prefix = prefix;
/*    */ 
/* 41 */     setEnum(reader.terms(new Term(prefix.field(), prefix.text())));
/*    */   }
/*    */ 
/*    */   public float difference()
/*    */   {
/* 46 */     return 1.0F;
/*    */   }
/*    */ 
/*    */   protected boolean endEnum()
/*    */   {
/* 51 */     return this.endEnum;
/*    */   }
/*    */ 
/*    */   protected Term getPrefixTerm() {
/* 55 */     return this.prefix;
/*    */   }
/*    */ 
/*    */   protected boolean termCompare(Term term)
/*    */   {
/* 60 */     if ((term.field() == this.prefix.field()) && (term.text().startsWith(this.prefix.text()))) {
/* 61 */       return true;
/*    */     }
/* 63 */     this.endEnum = true;
/* 64 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PrefixTermEnum
 * JD-Core Version:    0.6.0
 */