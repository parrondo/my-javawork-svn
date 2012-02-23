/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ class BooleanScorer2 extends Scorer
/*     */ {
/*     */   private final List<Scorer> requiredScorers;
/*     */   private final List<Scorer> optionalScorers;
/*     */   private final List<Scorer> prohibitedScorers;
/*     */   private final Coordinator coordinator;
/*     */   private final Scorer countingSumScorer;
/*     */   private final int minNrShouldMatch;
/*  63 */   private int doc = -1;
/*     */ 
/*     */   public BooleanScorer2(Weight weight, boolean disableCoord, Similarity similarity, int minNrShouldMatch, List<Scorer> required, List<Scorer> prohibited, List<Scorer> optional, int maxCoord)
/*     */     throws IOException
/*     */   {
/*  88 */     super(weight);
/*  89 */     if (minNrShouldMatch < 0) {
/*  90 */       throw new IllegalArgumentException("Minimum number of optional scorers should not be negative");
/*     */     }
/*  92 */     this.coordinator = new Coordinator(null);
/*  93 */     this.minNrShouldMatch = minNrShouldMatch;
/*  94 */     this.coordinator.maxCoord = maxCoord;
/*     */ 
/*  96 */     this.optionalScorers = optional;
/*  97 */     this.requiredScorers = required;
/*  98 */     this.prohibitedScorers = prohibited;
/*     */ 
/* 100 */     this.coordinator.init(similarity, disableCoord);
/* 101 */     this.countingSumScorer = makeCountingSumScorer(disableCoord, similarity);
/*     */   }
/*     */ 
/*     */   private Scorer countingDisjunctionSumScorer(List<Scorer> scorers, int minNrShouldMatch)
/*     */     throws IOException
/*     */   {
/* 149 */     return new DisjunctionSumScorer(this.weight, scorers, minNrShouldMatch) {
/* 150 */       private int lastScoredDoc = -1;
/*     */ 
/* 153 */       private float lastDocScore = (0.0F / 0.0F);
/*     */ 
/* 155 */       public float score() throws IOException { int doc = docID();
/* 156 */         if (doc >= this.lastScoredDoc) {
/* 157 */           if (doc > this.lastScoredDoc) {
/* 158 */             this.lastDocScore = super.score();
/* 159 */             this.lastScoredDoc = doc;
/*     */           }
/* 161 */           BooleanScorer2.this.coordinator.nrMatchers += this.nrMatchers;
/*     */         }
/* 163 */         return this.lastDocScore;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private Scorer countingConjunctionSumScorer(boolean disableCoord, Similarity similarity, List<Scorer> requiredScorers)
/*     */     throws IOException
/*     */   {
/* 172 */     int requiredNrMatchers = requiredScorers.size();
/* 173 */     return new ConjunctionScorer(this.weight, disableCoord ? 1.0F : similarity.coord(requiredScorers.size(), requiredScorers.size()), requiredScorers, requiredNrMatchers) {
/* 174 */       private int lastScoredDoc = -1;
/*     */ 
/* 177 */       private float lastDocScore = (0.0F / 0.0F);
/*     */ 
/* 179 */       public float score() throws IOException { int doc = docID();
/* 180 */         if (doc >= this.lastScoredDoc) {
/* 181 */           if (doc > this.lastScoredDoc) {
/* 182 */             this.lastDocScore = super.score();
/* 183 */             this.lastScoredDoc = doc;
/*     */           }
/* 185 */           BooleanScorer2.this.coordinator.nrMatchers += this.val$requiredNrMatchers;
/*     */         }
/*     */ 
/* 191 */         return this.lastDocScore; }
/*     */     };
/*     */   }
/*     */ 
/*     */   private Scorer dualConjunctionSumScorer(boolean disableCoord, Similarity similarity, Scorer req1, Scorer req2)
/*     */     throws IOException
/*     */   {
/* 199 */     return new ConjunctionScorer(this.weight, disableCoord ? 1.0F : similarity.coord(2, 2), new Scorer[] { req1, req2 });
/*     */   }
/*     */ 
/*     */   private Scorer makeCountingSumScorer(boolean disableCoord, Similarity similarity)
/*     */     throws IOException
/*     */   {
/* 211 */     return this.requiredScorers.size() == 0 ? makeCountingSumScorerNoReq(disableCoord, similarity) : makeCountingSumScorerSomeReq(disableCoord, similarity);
/*     */   }
/*     */ 
/*     */   private Scorer makeCountingSumScorerNoReq(boolean disableCoord, Similarity similarity)
/*     */     throws IOException
/*     */   {
/* 218 */     int nrOptRequired = this.minNrShouldMatch < 1 ? 1 : this.minNrShouldMatch;
/*     */     Scorer requiredCountingSumScorer;
/*     */     Scorer requiredCountingSumScorer;
/* 220 */     if (this.optionalScorers.size() > nrOptRequired) {
/* 221 */       requiredCountingSumScorer = countingDisjunctionSumScorer(this.optionalScorers, nrOptRequired);
/*     */     }
/*     */     else
/*     */     {
/*     */       Scorer requiredCountingSumScorer;
/* 222 */       if (this.optionalScorers.size() == 1)
/* 223 */         requiredCountingSumScorer = new SingleMatchScorer((Scorer)this.optionalScorers.get(0));
/*     */       else
/* 225 */         requiredCountingSumScorer = countingConjunctionSumScorer(disableCoord, similarity, this.optionalScorers);
/*     */     }
/* 227 */     return addProhibitedScorers(requiredCountingSumScorer);
/*     */   }
/*     */ 
/*     */   private Scorer makeCountingSumScorerSomeReq(boolean disableCoord, Similarity similarity) throws IOException {
/* 231 */     if (this.optionalScorers.size() == this.minNrShouldMatch) {
/* 232 */       ArrayList allReq = new ArrayList(this.requiredScorers);
/* 233 */       allReq.addAll(this.optionalScorers);
/* 234 */       return addProhibitedScorers(countingConjunctionSumScorer(disableCoord, similarity, allReq));
/*     */     }
/* 236 */     Scorer requiredCountingSumScorer = this.requiredScorers.size() == 1 ? new SingleMatchScorer((Scorer)this.requiredScorers.get(0)) : countingConjunctionSumScorer(disableCoord, similarity, this.requiredScorers);
/*     */ 
/* 240 */     if (this.minNrShouldMatch > 0) {
/* 241 */       return addProhibitedScorers(dualConjunctionSumScorer(disableCoord, similarity, requiredCountingSumScorer, countingDisjunctionSumScorer(this.optionalScorers, this.minNrShouldMatch)));
/*     */     }
/*     */ 
/* 250 */     return new ReqOptSumScorer(addProhibitedScorers(requiredCountingSumScorer), this.optionalScorers.size() == 1 ? new SingleMatchScorer((Scorer)this.optionalScorers.get(0)) : countingDisjunctionSumScorer(this.optionalScorers, 1));
/*     */   }
/*     */ 
/*     */   private Scorer addProhibitedScorers(Scorer requiredCountingSumScorer)
/*     */     throws IOException
/*     */   {
/* 266 */     return this.prohibitedScorers.size() == 0 ? requiredCountingSumScorer : new ReqExclScorer(requiredCountingSumScorer, this.prohibitedScorers.size() == 1 ? (Scorer)this.prohibitedScorers.get(0) : new DisjunctionSumScorer(this.weight, this.prohibitedScorers));
/*     */   }
/*     */ 
/*     */   public void score(Collector collector)
/*     */     throws IOException
/*     */   {
/* 279 */     collector.setScorer(this);
/* 280 */     while ((this.doc = this.countingSumScorer.nextDoc()) != 2147483647)
/* 281 */       collector.collect(this.doc);
/*     */   }
/*     */ 
/*     */   protected boolean score(Collector collector, int max, int firstDocID)
/*     */     throws IOException
/*     */   {
/* 287 */     this.doc = firstDocID;
/* 288 */     collector.setScorer(this);
/* 289 */     while (this.doc < max) {
/* 290 */       collector.collect(this.doc);
/* 291 */       this.doc = this.countingSumScorer.nextDoc();
/*     */     }
/* 293 */     return this.doc != 2147483647;
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/* 298 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/* 303 */     return this.doc = this.countingSumScorer.nextDoc();
/*     */   }
/*     */ 
/*     */   public float score() throws IOException
/*     */   {
/* 308 */     this.coordinator.nrMatchers = 0;
/* 309 */     float sum = this.countingSumScorer.score();
/* 310 */     return sum * this.coordinator.coordFactors[this.coordinator.nrMatchers];
/*     */   }
/*     */ 
/*     */   public float freq()
/*     */   {
/* 315 */     return this.coordinator.nrMatchers;
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 320 */     return this.doc = this.countingSumScorer.advance(target);
/*     */   }
/*     */ 
/*     */   protected void visitSubScorers(Query parent, BooleanClause.Occur relationship, Scorer.ScorerVisitor<Query, Query, Scorer> visitor)
/*     */   {
/* 325 */     super.visitSubScorers(parent, relationship, visitor);
/* 326 */     Query q = this.weight.getQuery();
/* 327 */     for (Scorer s : this.optionalScorers) {
/* 328 */       s.visitSubScorers(q, BooleanClause.Occur.SHOULD, visitor);
/*     */     }
/* 330 */     for (Scorer s : this.prohibitedScorers) {
/* 331 */       s.visitSubScorers(q, BooleanClause.Occur.MUST_NOT, visitor);
/*     */     }
/* 333 */     for (Scorer s : this.requiredScorers)
/* 334 */       s.visitSubScorers(q, BooleanClause.Occur.MUST, visitor);
/*     */   }
/*     */ 
/*     */   private class SingleMatchScorer extends Scorer
/*     */   {
/*     */     private Scorer scorer;
/* 107 */     private int lastScoredDoc = -1;
/*     */ 
/* 110 */     private float lastDocScore = (0.0F / 0.0F);
/*     */ 
/*     */     SingleMatchScorer(Scorer scorer) {
/* 113 */       super();
/* 114 */       this.scorer = scorer;
/*     */     }
/*     */ 
/*     */     public float score() throws IOException
/*     */     {
/* 119 */       int doc = docID();
/* 120 */       if (doc >= this.lastScoredDoc) {
/* 121 */         if (doc > this.lastScoredDoc) {
/* 122 */           this.lastDocScore = this.scorer.score();
/* 123 */           this.lastScoredDoc = doc;
/*     */         }
/* 125 */         BooleanScorer2.this.coordinator.nrMatchers += 1;
/*     */       }
/* 127 */       return this.lastDocScore;
/*     */     }
/*     */ 
/*     */     public int docID()
/*     */     {
/* 132 */       return this.scorer.docID();
/*     */     }
/*     */ 
/*     */     public int nextDoc() throws IOException
/*     */     {
/* 137 */       return this.scorer.nextDoc();
/*     */     }
/*     */ 
/*     */     public int advance(int target) throws IOException
/*     */     {
/* 142 */       return this.scorer.advance(target);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Coordinator
/*     */   {
/*  41 */     float[] coordFactors = null;
/*  42 */     int maxCoord = 0;
/*     */     int nrMatchers;
/*     */ 
/*     */     private Coordinator()
/*     */     {
/*     */     }
/*     */ 
/*     */     void init(Similarity sim, boolean disableCoord)
/*     */     {
/*  46 */       this.coordFactors = new float[BooleanScorer2.this.optionalScorers.size() + BooleanScorer2.this.requiredScorers.size() + 1];
/*  47 */       for (int i = 0; i < this.coordFactors.length; i++)
/*  48 */         this.coordFactors[i] = (disableCoord ? 1.0F : sim.coord(i, this.maxCoord));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.BooleanScorer2
 * JD-Core Version:    0.6.0
 */