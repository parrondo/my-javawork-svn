/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class Scorer extends DocIdSetIterator
/*     */ {
/*     */   private final Similarity similarity;
/*     */   protected final Weight weight;
/*     */ 
/*     */   protected Scorer(Weight weight)
/*     */   {
/*  51 */     this(null, weight);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected Scorer(Similarity similarity)
/*     */   {
/*  60 */     this(similarity, null);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected Scorer(Similarity similarity, Weight weight)
/*     */   {
/*  71 */     this.similarity = similarity;
/*  72 */     this.weight = weight;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Similarity getSimilarity()
/*     */   {
/*  80 */     return this.similarity;
/*     */   }
/*     */ 
/*     */   public void score(Collector collector)
/*     */     throws IOException
/*     */   {
/*  87 */     collector.setScorer(this);
/*     */     int doc;
/*  89 */     while ((doc = nextDoc()) != 2147483647)
/*  90 */       collector.collect(doc);
/*     */   }
/*     */ 
/*     */   protected boolean score(Collector collector, int max, int firstDocID)
/*     */     throws IOException
/*     */   {
/* 115 */     collector.setScorer(this);
/* 116 */     int doc = firstDocID;
/* 117 */     while (doc < max) {
/* 118 */       collector.collect(doc);
/* 119 */       doc = nextDoc();
/*     */     }
/* 121 */     return doc != 2147483647;
/*     */   }
/*     */ 
/*     */   public abstract float score()
/*     */     throws IOException;
/*     */ 
/*     */   public float freq()
/*     */     throws IOException
/*     */   {
/* 138 */     throw new UnsupportedOperationException(this + " does not implement freq()");
/*     */   }
/*     */ 
/*     */   public void visitScorers(ScorerVisitor<Query, Query, Scorer> visitor)
/*     */   {
/* 186 */     visitSubScorers(null, BooleanClause.Occur.MUST, visitor);
/*     */   }
/*     */ 
/*     */   protected void visitSubScorers(Query parent, BooleanClause.Occur relationship, ScorerVisitor<Query, Query, Scorer> visitor)
/*     */   {
/* 203 */     if (this.weight == null) {
/* 204 */       throw new UnsupportedOperationException();
/*     */     }
/* 206 */     Query q = this.weight.getQuery();
/* 207 */     switch (1.$SwitchMap$org$apache$lucene$search$BooleanClause$Occur[relationship.ordinal()]) {
/*     */     case 1:
/* 209 */       visitor.visitRequired(parent, q, this);
/* 210 */       break;
/*     */     case 2:
/* 212 */       visitor.visitProhibited(parent, q, this);
/* 213 */       break;
/*     */     case 3:
/* 215 */       visitor.visitOptional(parent, q, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class ScorerVisitor<P extends Query, C extends Query, S extends Scorer>
/*     */   {
/*     */     public void visitOptional(P parent, C child, S scorer)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitRequired(P parent, C child, S scorer)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitProhibited(P parent, C child, S scorer)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Scorer
 * JD-Core Version:    0.6.0
 */