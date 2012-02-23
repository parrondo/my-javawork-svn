/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.Searcher;
/*     */ import org.apache.lucene.search.Weight;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class FieldMaskingSpanQuery extends SpanQuery
/*     */ {
/*     */   private SpanQuery maskedQuery;
/*     */   private String field;
/*     */ 
/*     */   public FieldMaskingSpanQuery(SpanQuery maskedQuery, String maskedField)
/*     */   {
/*  78 */     this.maskedQuery = maskedQuery;
/*  79 */     this.field = maskedField;
/*     */   }
/*     */ 
/*     */   public String getField()
/*     */   {
/*  84 */     return this.field;
/*     */   }
/*     */ 
/*     */   public SpanQuery getMaskedQuery() {
/*  88 */     return this.maskedQuery;
/*     */   }
/*     */ 
/*     */   public Spans getSpans(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  96 */     return this.maskedQuery.getSpans(reader);
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 101 */     this.maskedQuery.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher) throws IOException
/*     */   {
/* 106 */     return this.maskedQuery.createWeight(searcher);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/* 111 */     FieldMaskingSpanQuery clone = null;
/*     */ 
/* 113 */     SpanQuery rewritten = (SpanQuery)this.maskedQuery.rewrite(reader);
/* 114 */     if (rewritten != this.maskedQuery) {
/* 115 */       clone = (FieldMaskingSpanQuery)clone();
/* 116 */       clone.maskedQuery = rewritten;
/*     */     }
/*     */ 
/* 119 */     if (clone != null) {
/* 120 */       return clone;
/*     */     }
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 128 */     StringBuilder buffer = new StringBuilder();
/* 129 */     buffer.append("mask(");
/* 130 */     buffer.append(this.maskedQuery.toString(field));
/* 131 */     buffer.append(")");
/* 132 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 133 */     buffer.append(" as ");
/* 134 */     buffer.append(this.field);
/* 135 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 140 */     if (!(o instanceof FieldMaskingSpanQuery))
/* 141 */       return false;
/* 142 */     FieldMaskingSpanQuery other = (FieldMaskingSpanQuery)o;
/* 143 */     return (getField().equals(other.getField())) && (getBoost() == other.getBoost()) && (getMaskedQuery().equals(other.getMaskedQuery()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 151 */     return getMaskedQuery().hashCode() ^ getField().hashCode() ^ Float.floatToRawIntBits(getBoost());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.FieldMaskingSpanQuery
 * JD-Core Version:    0.6.0
 */