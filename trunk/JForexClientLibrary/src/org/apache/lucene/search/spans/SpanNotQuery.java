/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanNotQuery extends SpanQuery
/*     */   implements Cloneable
/*     */ {
/*     */   private SpanQuery include;
/*     */   private SpanQuery exclude;
/*     */ 
/*     */   public SpanNotQuery(SpanQuery include, SpanQuery exclude)
/*     */   {
/*  38 */     this.include = include;
/*  39 */     this.exclude = exclude;
/*     */ 
/*  41 */     if (!include.getField().equals(exclude.getField()))
/*  42 */       throw new IllegalArgumentException("Clauses must have same field.");
/*     */   }
/*     */ 
/*     */   public SpanQuery getInclude() {
/*  46 */     return this.include;
/*     */   }
/*     */   public SpanQuery getExclude() {
/*  49 */     return this.exclude;
/*     */   }
/*     */   public String getField() {
/*  52 */     return this.include.getField();
/*     */   }
/*     */   public void extractTerms(Set<Term> terms) {
/*  55 */     this.include.extractTerms(terms);
/*     */   }
/*     */ 
/*     */   public String toString(String field) {
/*  59 */     StringBuilder buffer = new StringBuilder();
/*  60 */     buffer.append("spanNot(");
/*  61 */     buffer.append(this.include.toString(field));
/*  62 */     buffer.append(", ");
/*  63 */     buffer.append(this.exclude.toString(field));
/*  64 */     buffer.append(")");
/*  65 */     buffer.append(ToStringUtils.boost(getBoost()));
/*  66 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  71 */     SpanNotQuery spanNotQuery = new SpanNotQuery((SpanQuery)this.include.clone(), (SpanQuery)this.exclude.clone());
/*  72 */     spanNotQuery.setBoost(getBoost());
/*  73 */     return spanNotQuery;
/*     */   }
/*     */ 
/*     */   public Spans getSpans(IndexReader reader) throws IOException
/*     */   {
/*  78 */     return new Spans(reader) {
/*  79 */       private Spans includeSpans = SpanNotQuery.this.include.getSpans(this.val$reader);
/*  80 */       private boolean moreInclude = true;
/*     */ 
/*  82 */       private Spans excludeSpans = SpanNotQuery.this.exclude.getSpans(this.val$reader);
/*  83 */       private boolean moreExclude = this.excludeSpans.next();
/*     */ 
/*     */       public boolean next() throws IOException
/*     */       {
/*  87 */         if (this.moreInclude) {
/*  88 */           this.moreInclude = this.includeSpans.next();
/*     */         }
/*  90 */         while ((this.moreInclude) && (this.moreExclude))
/*     */         {
/*  92 */           if (this.includeSpans.doc() > this.excludeSpans.doc()) {
/*  93 */             this.moreExclude = this.excludeSpans.skipTo(this.includeSpans.doc());
/*     */           }
/*     */ 
/*  97 */           while ((this.moreExclude) && (this.includeSpans.doc() == this.excludeSpans.doc()) && (this.excludeSpans.end() <= this.includeSpans.start())) {
/*  98 */             this.moreExclude = this.excludeSpans.next();
/*     */           }
/*     */ 
/* 101 */           if ((!this.moreExclude) || (this.includeSpans.doc() != this.excludeSpans.doc()) || (this.includeSpans.end() <= this.excludeSpans.start()))
/*     */           {
/*     */             break;
/*     */           }
/*     */ 
/* 106 */           this.moreInclude = this.includeSpans.next();
/*     */         }
/* 108 */         return this.moreInclude;
/*     */       }
/*     */ 
/*     */       public boolean skipTo(int target) throws IOException
/*     */       {
/* 113 */         if (this.moreInclude) {
/* 114 */           this.moreInclude = this.includeSpans.skipTo(target);
/*     */         }
/* 116 */         if (!this.moreInclude) {
/* 117 */           return false;
/*     */         }
/* 119 */         if ((this.moreExclude) && (this.includeSpans.doc() > this.excludeSpans.doc()))
/*     */         {
/* 121 */           this.moreExclude = this.excludeSpans.skipTo(this.includeSpans.doc());
/*     */         }
/*     */ 
/* 125 */         while ((this.moreExclude) && (this.includeSpans.doc() == this.excludeSpans.doc()) && (this.excludeSpans.end() <= this.includeSpans.start())) {
/* 126 */           this.moreExclude = this.excludeSpans.next();
/*     */         }
/*     */ 
/* 129 */         if ((!this.moreExclude) || (this.includeSpans.doc() != this.excludeSpans.doc()) || (this.includeSpans.end() <= this.excludeSpans.start()))
/*     */         {
/* 132 */           return true;
/*     */         }
/* 134 */         return next();
/*     */       }
/*     */ 
/*     */       public int doc() {
/* 138 */         return this.includeSpans.doc();
/*     */       }
/* 140 */       public int start() { return this.includeSpans.start(); } 
/*     */       public int end() {
/* 142 */         return this.includeSpans.end();
/*     */       }
/*     */ 
/*     */       public Collection<byte[]> getPayload() throws IOException
/*     */       {
/* 147 */         ArrayList result = null;
/* 148 */         if (this.includeSpans.isPayloadAvailable()) {
/* 149 */           result = new ArrayList(this.includeSpans.getPayload());
/*     */         }
/* 151 */         return result;
/*     */       }
/*     */ 
/*     */       public boolean isPayloadAvailable()
/*     */       {
/* 157 */         return this.includeSpans.isPayloadAvailable();
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 162 */         return "spans(" + SpanNotQuery.this.toString() + ")";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/* 170 */     SpanNotQuery clone = null;
/*     */ 
/* 172 */     SpanQuery rewrittenInclude = (SpanQuery)this.include.rewrite(reader);
/* 173 */     if (rewrittenInclude != this.include) {
/* 174 */       clone = (SpanNotQuery)clone();
/* 175 */       clone.include = rewrittenInclude;
/*     */     }
/* 177 */     SpanQuery rewrittenExclude = (SpanQuery)this.exclude.rewrite(reader);
/* 178 */     if (rewrittenExclude != this.exclude) {
/* 179 */       if (clone == null) clone = (SpanNotQuery)clone();
/* 180 */       clone.exclude = rewrittenExclude;
/*     */     }
/*     */ 
/* 183 */     if (clone != null) {
/* 184 */       return clone;
/*     */     }
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 193 */     if (this == o) return true;
/* 194 */     if (!(o instanceof SpanNotQuery)) return false;
/*     */ 
/* 196 */     SpanNotQuery other = (SpanNotQuery)o;
/* 197 */     return (this.include.equals(other.include)) && (this.exclude.equals(other.exclude)) && (getBoost() == other.getBoost());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 204 */     int h = this.include.hashCode();
/* 205 */     h = h << 1 | h >>> 31;
/* 206 */     h ^= this.exclude.hashCode();
/* 207 */     h = h << 1 | h >>> 31;
/* 208 */     h ^= Float.floatToRawIntBits(getBoost());
/* 209 */     return h;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanNotQuery
 * JD-Core Version:    0.6.0
 */