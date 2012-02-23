/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.search.MultiTermQuery;
/*     */ import org.apache.lucene.search.MultiTermQuery.RewriteMethod;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.ScoringRewrite;
/*     */ import org.apache.lucene.search.TopTermsRewrite;
/*     */ 
/*     */ public class SpanMultiTermQueryWrapper<Q extends MultiTermQuery> extends SpanQuery
/*     */ {
/*     */   protected final Q query;
/*  49 */   private Method getFieldMethod = null; private Method getTermMethod = null;
/*     */   public static final SpanRewriteMethod SCORING_SPAN_QUERY_REWRITE;
/*     */ 
/*     */   public SpanMultiTermQueryWrapper(Q query)
/*     */   {
/*  65 */     this.query = query;
/*     */ 
/*  67 */     MultiTermQuery.RewriteMethod method = query.getRewriteMethod();
/*  68 */     if ((method instanceof TopTermsRewrite)) {
/*  69 */       int pqsize = ((TopTermsRewrite)method).getSize();
/*  70 */       setRewriteMethod(new TopTermsSpanBooleanQueryRewrite(pqsize));
/*     */     } else {
/*  72 */       setRewriteMethod(SCORING_SPAN_QUERY_REWRITE);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  78 */       this.getFieldMethod = query.getClass().getMethod("getField", new Class[0]);
/*     */     } catch (Exception e1) {
/*     */       try {
/*  81 */         this.getTermMethod = query.getClass().getMethod("getTerm", new Class[0]);
/*     */       } catch (Exception e2) {
/*     */         try {
/*  84 */           this.getTermMethod = query.getClass().getMethod("getPrefix", new Class[0]);
/*     */         } catch (Exception e3) {
/*  86 */           throw new IllegalArgumentException("SpanMultiTermQueryWrapper can only wrap MultiTermQueries that can return a field name using getField() or getTerm()");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final SpanRewriteMethod getRewriteMethod()
/*     */   {
/*  97 */     MultiTermQuery.RewriteMethod m = this.query.getRewriteMethod();
/*  98 */     if (!(m instanceof SpanRewriteMethod))
/*  99 */       throw new UnsupportedOperationException("You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod.");
/* 100 */     return (SpanRewriteMethod)m;
/*     */   }
/*     */ 
/*     */   public final void setRewriteMethod(SpanRewriteMethod rewriteMethod)
/*     */   {
/* 108 */     this.query.setRewriteMethod(rewriteMethod);
/*     */   }
/*     */ 
/*     */   public Spans getSpans(IndexReader reader) throws IOException
/*     */   {
/* 113 */     throw new UnsupportedOperationException("Query should have been rewritten");
/*     */   }
/*     */ 
/*     */   public String getField()
/*     */   {
/*     */     try {
/* 119 */       if (this.getFieldMethod != null) {
/* 120 */         return (String)this.getFieldMethod.invoke(this.query, new Object[0]);
/*     */       }
/* 122 */       assert (this.getTermMethod != null);
/* 123 */       return ((Term)this.getTermMethod.invoke(this.query, new Object[0])).field();
/*     */     } catch (Exception e) {
/*     */     }
/* 126 */     throw new RuntimeException("Cannot invoke getField() or getTerm() on wrapped query.", e);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 132 */     StringBuilder builder = new StringBuilder();
/* 133 */     builder.append("SpanMultiTermQueryWrapper(");
/* 134 */     builder.append(this.query.toString(field));
/* 135 */     builder.append(")");
/* 136 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader) throws IOException
/*     */   {
/* 141 */     Query q = this.query.rewrite(reader);
/* 142 */     if (!(q instanceof SpanQuery))
/* 143 */       throw new UnsupportedOperationException("You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod.");
/* 144 */     return q;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 149 */     return 31 * this.query.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 154 */     if (this == obj) return true;
/* 155 */     if (obj == null) return false;
/* 156 */     if (getClass() != obj.getClass()) return false;
/* 157 */     SpanMultiTermQueryWrapper other = (SpanMultiTermQueryWrapper)obj;
/* 158 */     return this.query.equals(other.query);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 174 */     SCORING_SPAN_QUERY_REWRITE = new SpanRewriteMethod() {
/* 175 */       private final ScoringRewrite<SpanOrQuery> delegate = new ScoringRewrite()
/*     */       {
/*     */         protected SpanOrQuery getTopLevelQuery() {
/* 178 */           return new SpanOrQuery(new SpanQuery[0]);
/*     */         }
/*     */ 
/*     */         protected void addClause(SpanOrQuery topLevel, Term term, float boost)
/*     */         {
/* 183 */           SpanTermQuery q = new SpanTermQuery(term);
/* 184 */           q.setBoost(boost);
/* 185 */           topLevel.addClause(q);
/*     */         }
/* 175 */       };
/*     */ 
/*     */       public SpanQuery rewrite(IndexReader reader, MultiTermQuery query)
/*     */         throws IOException
/*     */       {
/* 191 */         return (SpanQuery)this.delegate.rewrite(reader, query);
/*     */       }
/*     */ 
/*     */       protected Object readResolve()
/*     */       {
/* 196 */         return SpanMultiTermQueryWrapper.SCORING_SPAN_QUERY_REWRITE;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static final class TopTermsSpanBooleanQueryRewrite extends SpanMultiTermQueryWrapper.SpanRewriteMethod
/*     */   {
/*     */     private final TopTermsRewrite<SpanOrQuery> delegate;
/*     */ 
/*     */     public TopTermsSpanBooleanQueryRewrite(int size)
/*     */     {
/* 219 */       this.delegate = new TopTermsRewrite(size)
/*     */       {
/*     */         protected int getMaxSize() {
/* 222 */           return 2147483647;
/*     */         }
/*     */ 
/*     */         protected SpanOrQuery getTopLevelQuery()
/*     */         {
/* 227 */           return new SpanOrQuery(new SpanQuery[0]);
/*     */         }
/*     */ 
/*     */         protected void addClause(SpanOrQuery topLevel, Term term, float boost)
/*     */         {
/* 232 */           SpanTermQuery q = new SpanTermQuery(term);
/* 233 */           q.setBoost(boost);
/* 234 */           topLevel.addClause(q);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 241 */       return this.delegate.getSize();
/*     */     }
/*     */ 
/*     */     public SpanQuery rewrite(IndexReader reader, MultiTermQuery query) throws IOException
/*     */     {
/* 246 */       return (SpanQuery)this.delegate.rewrite(reader, query);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 251 */       return 31 * this.delegate.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 256 */       if (this == obj) return true;
/* 257 */       if (obj == null) return false;
/* 258 */       if (getClass() != obj.getClass()) return false;
/* 259 */       TopTermsSpanBooleanQueryRewrite other = (TopTermsSpanBooleanQueryRewrite)obj;
/* 260 */       return this.delegate.equals(other.delegate);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class SpanRewriteMethod extends MultiTermQuery.RewriteMethod
/*     */   {
/*     */     public abstract SpanQuery rewrite(IndexReader paramIndexReader, MultiTermQuery paramMultiTermQuery)
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanMultiTermQueryWrapper
 * JD-Core Version:    0.6.0
 */