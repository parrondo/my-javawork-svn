/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public abstract class Query
/*     */   implements Serializable, Cloneable
/*     */ {
/*  50 */   private float boost = 1.0F;
/*     */ 
/*     */   public void setBoost(float b)
/*     */   {
/*  56 */     this.boost = b;
/*     */   }
/*     */ 
/*     */   public float getBoost()
/*     */   {
/*  62 */     return this.boost;
/*     */   }
/*     */ 
/*     */   public abstract String toString(String paramString);
/*     */ 
/*     */   public String toString()
/*     */   {
/*  83 */     return toString("");
/*     */   }
/*     */ 
/*     */   public Weight createWeight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/*  93 */     throw new UnsupportedOperationException("Query " + this + " does not implement createWeight");
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Weight weight(Searcher searcher)
/*     */     throws IOException
/*     */   {
/* 103 */     return searcher.createNormalizedWeight(this);
/*     */   }
/*     */ 
/*     */   public Query rewrite(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   public Query combine(Query[] queries)
/*     */   {
/* 128 */     HashSet uniques = new HashSet();
/* 129 */     for (int i = 0; i < queries.length; i++) {
/* 130 */       Query query = queries[i];
/* 131 */       BooleanClause[] clauses = null;
/*     */ 
/* 133 */       boolean splittable = query instanceof BooleanQuery;
/* 134 */       if (splittable) {
/* 135 */         BooleanQuery bq = (BooleanQuery)query;
/* 136 */         splittable = bq.isCoordDisabled();
/* 137 */         clauses = bq.getClauses();
/* 138 */         for (int j = 0; (splittable) && (j < clauses.length); j++) {
/* 139 */           splittable = clauses[j].getOccur() == BooleanClause.Occur.SHOULD;
/*     */         }
/*     */       }
/* 142 */       if (splittable) {
/* 143 */         for (int j = 0; j < clauses.length; j++)
/* 144 */           uniques.add(clauses[j].getQuery());
/*     */       }
/*     */       else {
/* 147 */         uniques.add(query);
/*     */       }
/*     */     }
/*     */ 
/* 151 */     if (uniques.size() == 1) {
/* 152 */       return (Query)uniques.iterator().next();
/*     */     }
/* 154 */     BooleanQuery result = new BooleanQuery(true);
/* 155 */     for (Query query : uniques)
/* 156 */       result.add(query, BooleanClause.Occur.SHOULD);
/* 157 */     return result;
/*     */   }
/*     */ 
/*     */   public void extractTerms(Set<Term> terms)
/*     */   {
/* 169 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public static Query mergeBooleanQueries(BooleanQuery[] queries)
/*     */   {
/* 180 */     HashSet allClauses = new HashSet();
/* 181 */     for (BooleanQuery booleanQuery : queries) {
/* 182 */       for (BooleanClause clause : booleanQuery) {
/* 183 */         allClauses.add(clause);
/*     */       }
/*     */     }
/*     */ 
/* 187 */     boolean coordDisabled = queries.length == 0 ? false : queries[0].isCoordDisabled();
/*     */ 
/* 189 */     BooleanQuery result = new BooleanQuery(coordDisabled);
/* 190 */     for (BooleanClause clause2 : allClauses) {
/* 191 */       result.add(clause2);
/*     */     }
/* 193 */     return result;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Similarity getSimilarity(Searcher searcher)
/*     */   {
/* 205 */     return searcher.getSimilarity();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 212 */       return super.clone(); } catch (CloneNotSupportedException e) {
/*     */     }
/* 214 */     throw new RuntimeException("Clone not supported: " + e.getMessage());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 220 */     int prime = 31;
/* 221 */     int result = 1;
/* 222 */     result = 31 * result + Float.floatToIntBits(this.boost);
/* 223 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 228 */     if (this == obj)
/* 229 */       return true;
/* 230 */     if (obj == null)
/* 231 */       return false;
/* 232 */     if (getClass() != obj.getClass())
/* 233 */       return false;
/* 234 */     Query other = (Query)obj;
/*     */ 
/* 236 */     return Float.floatToIntBits(this.boost) == Float.floatToIntBits(other.boost);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Query
 * JD-Core Version:    0.6.0
 */