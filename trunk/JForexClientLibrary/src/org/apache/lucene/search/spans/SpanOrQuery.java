/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanOrQuery extends SpanQuery
/*     */   implements Cloneable
/*     */ {
/*     */   private List<SpanQuery> clauses;
/*     */   private String field;
/*     */ 
/*     */   public SpanOrQuery(SpanQuery[] clauses)
/*     */   {
/*  43 */     this.clauses = new ArrayList(clauses.length);
/*  44 */     for (int i = 0; i < clauses.length; i++)
/*  45 */       addClause(clauses[i]);
/*     */   }
/*     */ 
/*     */   public final void addClause(SpanQuery clause)
/*     */   {
/*  51 */     if (this.field == null)
/*  52 */       this.field = clause.getField();
/*  53 */     else if (!clause.getField().equals(this.field)) {
/*  54 */       throw new IllegalArgumentException("Clauses must have same field.");
/*     */     }
/*  56 */     this.clauses.add(clause);
/*     */   }
/*     */ 
/*     */   public SpanQuery[] getClauses()
/*     */   {
/*  61 */     return (SpanQuery[])this.clauses.toArray(new SpanQuery[this.clauses.size()]);
/*     */   }
/*     */ 
/*     */   public String getField() {
/*  65 */     return this.field;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms) {
/*  69 */     for (SpanQuery clause : this.clauses)
/*  70 */       clause.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  76 */     int sz = this.clauses.size();
/*  77 */     SpanQuery[] newClauses = new SpanQuery[sz];
/*     */ 
/*  79 */     for (int i = 0; i < sz; i++) {
/*  80 */       newClauses[i] = ((SpanQuery)((SpanQuery)this.clauses.get(i)).clone());
/*     */     }
/*  82 */     SpanOrQuery soq = new SpanOrQuery(newClauses);
/*  83 */     soq.setBoost(getBoost());
/*  84 */     return soq;
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/*  89 */     SpanOrQuery clone = null;
/*  90 */     for (int i = 0; i < this.clauses.size(); i++) {
/*  91 */       SpanQuery c = (SpanQuery)this.clauses.get(i);
/*  92 */       SpanQuery query = (SpanQuery)c.rewrite(reader);
/*  93 */       if (query != c) {
/*  94 */         if (clone == null)
/*  95 */           clone = (SpanOrQuery)clone();
/*  96 */         clone.clauses.set(i, query);
/*     */       }
/*     */     }
/*  99 */     if (clone != null) {
/* 100 */       return clone;
/*     */     }
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 108 */     StringBuilder buffer = new StringBuilder();
/* 109 */     buffer.append("spanOr([");
/* 110 */     Iterator i = this.clauses.iterator();
/* 111 */     while (i.hasNext()) {
/* 112 */       SpanQuery clause = (SpanQuery)i.next();
/* 113 */       buffer.append(clause.toString(field));
/* 114 */       if (i.hasNext()) {
/* 115 */         buffer.append(", ");
/*     */       }
/*     */     }
/* 118 */     buffer.append("])");
/* 119 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 120 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 125 */     if (this == o) return true;
/* 126 */     if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/* 128 */     SpanOrQuery that = (SpanOrQuery)o;
/*     */ 
/* 130 */     if (!this.clauses.equals(that.clauses)) return false;
/* 131 */     if ((!this.clauses.isEmpty()) && (!this.field.equals(that.field))) return false;
/*     */ 
/* 133 */     return getBoost() == that.getBoost();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 138 */     int h = this.clauses.hashCode();
/* 139 */     h ^= (h << 10 | h >>> 23);
/* 140 */     h ^= Float.floatToRawIntBits(getBoost());
/* 141 */     return h;
/*     */   }
/*     */ 
/*     */   public Spans getSpans(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 166 */     if (this.clauses.size() == 1) {
/* 167 */       return ((SpanQuery)this.clauses.get(0)).getSpans(reader);
/*     */     }
/* 169 */     return new Spans(reader) {
/* 170 */       private SpanOrQuery.SpanQueue queue = null;
/*     */ 
/*     */       private boolean initSpanQueue(int target) throws IOException {
/* 173 */         this.queue = new SpanOrQuery.SpanQueue(SpanOrQuery.this, SpanOrQuery.this.clauses.size());
/* 174 */         Iterator i = SpanOrQuery.this.clauses.iterator();
/* 175 */         while (i.hasNext()) {
/* 176 */           Spans spans = ((SpanQuery)i.next()).getSpans(this.val$reader);
/* 177 */           if (((target == -1) && (spans.next())) || ((target != -1) && (spans.skipTo(target))))
/*     */           {
/* 179 */             this.queue.add(spans);
/*     */           }
/*     */         }
/* 182 */         return this.queue.size() != 0;
/*     */       }
/*     */ 
/*     */       public boolean next() throws IOException
/*     */       {
/* 187 */         if (this.queue == null) {
/* 188 */           return initSpanQueue(-1);
/*     */         }
/*     */ 
/* 191 */         if (this.queue.size() == 0) {
/* 192 */           return false;
/*     */         }
/*     */ 
/* 195 */         if (top().next()) {
/* 196 */           this.queue.updateTop();
/* 197 */           return true;
/*     */         }
/*     */ 
/* 200 */         this.queue.pop();
/* 201 */         return this.queue.size() != 0;
/*     */       }
/*     */       private Spans top() {
/* 204 */         return (Spans)this.queue.top();
/*     */       }
/*     */ 
/*     */       public boolean skipTo(int target) throws IOException {
/* 208 */         if (this.queue == null) {
/* 209 */           return initSpanQueue(target);
/*     */         }
/*     */ 
/* 212 */         boolean skipCalled = false;
/* 213 */         while ((this.queue.size() != 0) && (top().doc() < target)) {
/* 214 */           if (top().skipTo(target))
/* 215 */             this.queue.updateTop();
/*     */           else {
/* 217 */             this.queue.pop();
/*     */           }
/* 219 */           skipCalled = true;
/*     */         }
/*     */ 
/* 222 */         if (skipCalled) {
/* 223 */           return this.queue.size() != 0;
/*     */         }
/* 225 */         return next();
/*     */       }
/*     */ 
/*     */       public int doc() {
/* 229 */         return top().doc();
/*     */       }
/* 231 */       public int start() { return top().start(); } 
/*     */       public int end() {
/* 233 */         return top().end();
/*     */       }
/*     */ 
/*     */       public Collection<byte[]> getPayload() throws IOException {
/* 237 */         ArrayList result = null;
/* 238 */         Spans theTop = top();
/* 239 */         if ((theTop != null) && (theTop.isPayloadAvailable())) {
/* 240 */           result = new ArrayList(theTop.getPayload());
/*     */         }
/* 242 */         return result;
/*     */       }
/*     */ 
/*     */       public boolean isPayloadAvailable()
/*     */       {
/* 247 */         Spans top = top();
/* 248 */         return (top != null) && (top.isPayloadAvailable());
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 253 */         return "spans(" + SpanOrQuery.this + ")@" + (this.queue.size() > 0 ? doc() + ":" + start() + "-" + end() : this.queue == null ? "START" : "END");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private class SpanQueue extends PriorityQueue<Spans>
/*     */   {
/*     */     public SpanQueue(int size)
/*     */     {
/* 147 */       initialize(size);
/*     */     }
/*     */ 
/*     */     protected final boolean lessThan(Spans spans1, Spans spans2)
/*     */     {
/* 152 */       if (spans1.doc() == spans2.doc()) {
/* 153 */         if (spans1.start() == spans2.start()) {
/* 154 */           return spans1.end() < spans2.end();
/*     */         }
/* 156 */         return spans1.start() < spans2.start();
/*     */       }
/*     */ 
/* 159 */       return spans1.doc() < spans2.doc();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanOrQuery
 * JD-Core Version:    0.6.0
 */