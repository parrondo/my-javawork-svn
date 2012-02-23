/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class BooleanClause
/*     */   implements Serializable
/*     */ {
/*     */   private Query query;
/*     */   private Occur occur;
/*     */ 
/*     */   public BooleanClause(Query query, Occur occur)
/*     */   {
/*  54 */     this.query = query;
/*  55 */     this.occur = occur;
/*     */   }
/*     */ 
/*     */   public Occur getOccur()
/*     */   {
/*  60 */     return this.occur;
/*     */   }
/*     */ 
/*     */   public void setOccur(Occur occur) {
/*  64 */     this.occur = occur;
/*     */   }
/*     */ 
/*     */   public Query getQuery()
/*     */   {
/*  69 */     return this.query;
/*     */   }
/*     */ 
/*     */   public void setQuery(Query query) {
/*  73 */     this.query = query;
/*     */   }
/*     */ 
/*     */   public boolean isProhibited() {
/*  77 */     return Occur.MUST_NOT == this.occur;
/*     */   }
/*     */ 
/*     */   public boolean isRequired() {
/*  81 */     return Occur.MUST == this.occur;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  89 */     if ((o == null) || (!(o instanceof BooleanClause)))
/*  90 */       return false;
/*  91 */     BooleanClause other = (BooleanClause)o;
/*  92 */     return (this.query.equals(other.query)) && (this.occur == other.occur);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  99 */     return this.query.hashCode() ^ (Occur.MUST == this.occur ? 1 : 0) ^ (Occur.MUST_NOT == this.occur ? 2 : 0);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 105 */     return this.occur.toString() + this.query.toString();
/*     */   }
/*     */ 
/*     */   public static enum Occur
/*     */   {
/*  27 */     MUST, 
/*     */ 
/*  35 */     SHOULD, 
/*     */ 
/*  40 */     MUST_NOT;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.BooleanClause
 * JD-Core Version:    0.6.0
 */