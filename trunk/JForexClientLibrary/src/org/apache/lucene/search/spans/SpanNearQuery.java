/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanNearQuery extends SpanQuery
/*     */   implements Cloneable
/*     */ {
/*     */   protected List<SpanQuery> clauses;
/*     */   protected int slop;
/*     */   protected boolean inOrder;
/*     */   protected String field;
/*     */   private boolean collectPayloads;
/*     */ 
/*     */   public SpanNearQuery(SpanQuery[] clauses, int slop, boolean inOrder)
/*     */   {
/*  54 */     this(clauses, slop, inOrder, true);
/*     */   }
/*     */ 
/*     */   public SpanNearQuery(SpanQuery[] clauses, int slop, boolean inOrder, boolean collectPayloads)
/*     */   {
/*  60 */     this.clauses = new ArrayList(clauses.length);
/*  61 */     for (int i = 0; i < clauses.length; i++) {
/*  62 */       SpanQuery clause = clauses[i];
/*  63 */       if (i == 0)
/*  64 */         this.field = clause.getField();
/*  65 */       else if (!clause.getField().equals(this.field)) {
/*  66 */         throw new IllegalArgumentException("Clauses must have same field.");
/*     */       }
/*  68 */       this.clauses.add(clause);
/*     */     }
/*  70 */     this.collectPayloads = collectPayloads;
/*  71 */     this.slop = slop;
/*  72 */     this.inOrder = inOrder;
/*     */   }
/*     */ 
/*     */   public SpanQuery[] getClauses()
/*     */   {
/*  77 */     return (SpanQuery[])this.clauses.toArray(new SpanQuery[this.clauses.size()]);
/*     */   }
/*     */ 
/*     */   public int getSlop() {
/*  81 */     return this.slop;
/*     */   }
/*     */   public boolean isInOrder() {
/*  84 */     return this.inOrder;
/*     */   }
/*     */   public String getField() {
/*  87 */     return this.field;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms) {
/*  91 */     for (SpanQuery clause : this.clauses)
/*  92 */       clause.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/*  99 */     StringBuilder buffer = new StringBuilder();
/* 100 */     buffer.append("spanNear([");
/* 101 */     Iterator i = this.clauses.iterator();
/* 102 */     while (i.hasNext()) {
/* 103 */       SpanQuery clause = (SpanQuery)i.next();
/* 104 */       buffer.append(clause.toString(field));
/* 105 */       if (i.hasNext()) {
/* 106 */         buffer.append(", ");
/*     */       }
/*     */     }
/* 109 */     buffer.append("], ");
/* 110 */     buffer.append(this.slop);
/* 111 */     buffer.append(", ");
/* 112 */     buffer.append(this.inOrder);
/* 113 */     buffer.append(")");
/* 114 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 115 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public Spans getSpans(IndexReader reader) throws IOException
/*     */   {
/* 120 */     if (this.clauses.size() == 0) {
/* 121 */       return new SpanOrQuery(getClauses()).getSpans(reader);
/*     */     }
/* 123 */     if (this.clauses.size() == 1) {
/* 124 */       return ((SpanQuery)this.clauses.get(0)).getSpans(reader);
/*     */     }
/* 126 */     return this.inOrder ? new NearSpansOrdered(this, reader, this.collectPayloads) : new NearSpansUnordered(this, reader);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 133 */     SpanNearQuery clone = null;
/* 134 */     for (int i = 0; i < this.clauses.size(); i++) {
/* 135 */       SpanQuery c = (SpanQuery)this.clauses.get(i);
/* 136 */       SpanQuery query = (SpanQuery)c.rewrite(reader);
/* 137 */       if (query != c) {
/* 138 */         if (clone == null)
/* 139 */           clone = (SpanNearQuery)clone();
/* 140 */         clone.clauses.set(i, query);
/*     */       }
/*     */     }
/* 143 */     if (clone != null) {
/* 144 */       return clone;
/*     */     }
/* 146 */     return this;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 152 */     int sz = this.clauses.size();
/* 153 */     SpanQuery[] newClauses = new SpanQuery[sz];
/*     */ 
/* 155 */     for (int i = 0; i < sz; i++) {
/* 156 */       newClauses[i] = ((SpanQuery)((SpanQuery)this.clauses.get(i)).clone());
/*     */     }
/* 158 */     SpanNearQuery spanNearQuery = new SpanNearQuery(newClauses, this.slop, this.inOrder);
/* 159 */     spanNearQuery.setBoost(getBoost());
/* 160 */     return spanNearQuery;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 166 */     if (this == o) return true;
/* 167 */     if (!(o instanceof SpanNearQuery)) return false;
/*     */ 
/* 169 */     SpanNearQuery spanNearQuery = (SpanNearQuery)o;
/*     */ 
/* 171 */     if (this.inOrder != spanNearQuery.inOrder) return false;
/* 172 */     if (this.slop != spanNearQuery.slop) return false;
/* 173 */     if (!this.clauses.equals(spanNearQuery.clauses)) return false;
/*     */ 
/* 175 */     return getBoost() == spanNearQuery.getBoost();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 181 */     int result = this.clauses.hashCode();
/*     */ 
/* 185 */     result ^= (result << 14 | result >>> 19);
/* 186 */     result += Float.floatToRawIntBits(getBoost());
/* 187 */     result += this.slop;
/* 188 */     result ^= (this.inOrder ? -1716530243 : 0);
/* 189 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanNearQuery
 * JD-Core Version:    0.6.0
 */