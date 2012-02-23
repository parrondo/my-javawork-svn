/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.util.ScorerDocQueue;
/*     */ 
/*     */ class DisjunctionSumScorer extends Scorer
/*     */ {
/*     */   private final int nrScorers;
/*     */   protected final List<Scorer> subScorers;
/*     */   private final int minimumNrMatchers;
/*     */   private ScorerDocQueue scorerDocQueue;
/*  53 */   private int currentDoc = -1;
/*     */ 
/*  56 */   protected int nrMatchers = -1;
/*     */ 
/*  58 */   private float currentScore = (0.0F / 0.0F);
/*     */ 
/*     */   public DisjunctionSumScorer(Weight weight, List<Scorer> subScorers, int minimumNrMatchers)
/*     */     throws IOException
/*     */   {
/*  72 */     super(weight);
/*     */ 
/*  74 */     this.nrScorers = subScorers.size();
/*     */ 
/*  76 */     if (minimumNrMatchers <= 0) {
/*  77 */       throw new IllegalArgumentException("Minimum nr of matchers must be positive");
/*     */     }
/*  79 */     if (this.nrScorers <= 1) {
/*  80 */       throw new IllegalArgumentException("There must be at least 2 subScorers");
/*     */     }
/*     */ 
/*  83 */     this.minimumNrMatchers = minimumNrMatchers;
/*  84 */     this.subScorers = subScorers;
/*     */ 
/*  86 */     initScorerDocQueue();
/*     */   }
/*     */ 
/*     */   public DisjunctionSumScorer(Weight weight, List<Scorer> subScorers)
/*     */     throws IOException
/*     */   {
/*  93 */     this(weight, subScorers, 1);
/*     */   }
/*     */ 
/*     */   private void initScorerDocQueue()
/*     */     throws IOException
/*     */   {
/* 100 */     this.scorerDocQueue = new ScorerDocQueue(this.nrScorers);
/* 101 */     for (Scorer se : this.subScorers)
/* 102 */       if (se.nextDoc() != 2147483647)
/* 103 */         this.scorerDocQueue.insert(se);
/*     */   }
/*     */ 
/*     */   public void score(Collector collector)
/*     */     throws IOException
/*     */   {
/* 113 */     collector.setScorer(this);
/* 114 */     while (nextDoc() != 2147483647)
/* 115 */       collector.collect(this.currentDoc);
/*     */   }
/*     */ 
/*     */   protected boolean score(Collector collector, int max, int firstDocID)
/*     */     throws IOException
/*     */   {
/* 129 */     collector.setScorer(this);
/* 130 */     while (this.currentDoc < max) {
/* 131 */       collector.collect(this.currentDoc);
/* 132 */       if (nextDoc() == 2147483647) {
/* 133 */         return false;
/*     */       }
/*     */     }
/* 136 */     return true;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/* 141 */     if ((this.scorerDocQueue.size() < this.minimumNrMatchers) || (!advanceAfterCurrent())) {
/* 142 */       this.currentDoc = 2147483647;
/*     */     }
/* 144 */     return this.currentDoc;
/*     */   }
/*     */ 
/*     */   protected boolean advanceAfterCurrent()
/*     */     throws IOException
/*     */   {
/*     */     do
/*     */     {
/* 167 */       this.currentDoc = this.scorerDocQueue.topDoc();
/* 168 */       this.currentScore = this.scorerDocQueue.topScore();
/* 169 */       this.nrMatchers = 1;
/*     */ 
/* 171 */       while ((this.scorerDocQueue.topNextAndAdjustElsePop()) || 
/* 172 */         (this.scorerDocQueue.size() != 0))
/*     */       {
/* 176 */         if (this.scorerDocQueue.topDoc() != this.currentDoc) {
/*     */           break;
/*     */         }
/* 179 */         this.currentScore += this.scorerDocQueue.topScore();
/* 180 */         this.nrMatchers += 1;
/*     */       }
/*     */ 
/* 183 */       if (this.nrMatchers >= this.minimumNrMatchers)
/* 184 */         return true; 
/*     */     }
/* 185 */     while (this.scorerDocQueue.size() >= this.minimumNrMatchers);
/* 186 */     return false;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */     throws IOException
/*     */   {
/* 195 */     return this.currentScore;
/*     */   }
/*     */ 
/*     */   public int docID() {
/* 199 */     return this.currentDoc;
/*     */   }
/*     */ 
/*     */   public int nrMatchers()
/*     */   {
/* 206 */     return this.nrMatchers;
/*     */   }
/*     */ 
/*     */   public int advance(int target)
/*     */     throws IOException
/*     */   {
/* 221 */     if (this.scorerDocQueue.size() < this.minimumNrMatchers) {
/* 222 */       return this.currentDoc = 2147483647;
/*     */     }
/* 224 */     if (target <= this.currentDoc) {
/* 225 */       return this.currentDoc;
/*     */     }
/*     */     do
/* 228 */       if (this.scorerDocQueue.topDoc() >= target)
/* 229 */         return this.currentDoc = 2147483647;
/* 230 */     while ((this.scorerDocQueue.topSkipToAndAdjustElsePop(target)) || 
/* 231 */       (this.scorerDocQueue.size() >= this.minimumNrMatchers));
/* 232 */     return this.currentDoc = 2147483647;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DisjunctionSumScorer
 * JD-Core Version:    0.6.0
 */