/*    */ package org.apache.lucene.search.spans;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Set;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.index.Term;
/*    */ import org.apache.lucene.util.ToStringUtils;
/*    */ 
/*    */ public class SpanTermQuery extends SpanQuery
/*    */ {
/*    */   protected Term term;
/*    */ 
/*    */   public SpanTermQuery(Term term)
/*    */   {
/* 32 */     this.term = term;
/*    */   }
/*    */   public Term getTerm() {
/* 35 */     return this.term;
/*    */   }
/*    */   public String getField() {
/* 38 */     return this.term.field();
/*    */   }
/*    */ 
/*    */   public void extractTerms(Set<Term> terms) {
/* 42 */     terms.add(this.term);
/*    */   }
/*    */ 
/*    */   public String toString(String field)
/*    */   {
/* 47 */     StringBuilder buffer = new StringBuilder();
/* 48 */     if (this.term.field().equals(field))
/* 49 */       buffer.append(this.term.text());
/*    */     else
/* 51 */       buffer.append(this.term.toString());
/* 52 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 53 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 58 */     int prime = 31;
/* 59 */     int result = super.hashCode();
/* 60 */     result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
/* 61 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 66 */     if (this == obj)
/* 67 */       return true;
/* 68 */     if (!super.equals(obj))
/* 69 */       return false;
/* 70 */     if (getClass() != obj.getClass())
/* 71 */       return false;
/* 72 */     SpanTermQuery other = (SpanTermQuery)obj;
/* 73 */     if (this.term == null) {
/* 74 */       if (other.term != null)
/* 75 */         return false;
/* 76 */     } else if (!this.term.equals(other.term))
/* 77 */       return false;
/* 78 */     return true;
/*    */   }
/*    */ 
/*    */   public Spans getSpans(IndexReader reader) throws IOException
/*    */   {
/* 83 */     return new TermSpans(reader.termPositions(this.term), this.term);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanTermQuery
 * JD-Core Version:    0.6.0
 */