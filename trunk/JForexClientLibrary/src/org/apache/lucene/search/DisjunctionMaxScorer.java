/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ class DisjunctionMaxScorer extends Scorer
/*     */ {
/*     */   private final Scorer[] subScorers;
/*     */   private int numScorers;
/*     */   private final float tieBreakerMultiplier;
/*  34 */   private int doc = -1;
/*     */   private float scoreSum;
/*     */   private float scoreMax;
/*     */ 
/*     */   public DisjunctionMaxScorer(Weight weight, float tieBreakerMultiplier, Similarity similarity, Scorer[] subScorers, int numScorers)
/*     */     throws IOException
/*     */   {
/*  59 */     super(similarity, weight);
/*  60 */     this.tieBreakerMultiplier = tieBreakerMultiplier;
/*     */ 
/*  64 */     this.subScorers = subScorers;
/*  65 */     this.numScorers = numScorers;
/*     */ 
/*  67 */     heapify();
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/*  72 */     if (this.numScorers == 0) return this.doc = 2147483647;
/*  73 */     while (this.subScorers[0].docID() == this.doc) {
/*  74 */       if (this.subScorers[0].nextDoc() != 2147483647) {
/*  75 */         heapAdjust(0); continue;
/*     */       }
/*  77 */       heapRemoveRoot();
/*  78 */       if (this.numScorers == 0) {
/*  79 */         return this.doc = 2147483647;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  84 */     return this.doc = this.subScorers[0].docID();
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/*  89 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */     throws IOException
/*     */   {
/*  97 */     int doc = this.subScorers[0].docID();
/*  98 */     this.scoreSum = (this.scoreMax = this.subScorers[0].score());
/*  99 */     int size = this.numScorers;
/* 100 */     scoreAll(1, size, doc);
/* 101 */     scoreAll(2, size, doc);
/* 102 */     return this.scoreMax + (this.scoreSum - this.scoreMax) * this.tieBreakerMultiplier;
/*     */   }
/*     */ 
/*     */   private void scoreAll(int root, int size, int doc) throws IOException
/*     */   {
/* 107 */     if ((root < size) && (this.subScorers[root].docID() == doc)) {
/* 108 */       float sub = this.subScorers[root].score();
/* 109 */       this.scoreSum += sub;
/* 110 */       this.scoreMax = Math.max(this.scoreMax, sub);
/* 111 */       scoreAll((root << 1) + 1, size, doc);
/* 112 */       scoreAll((root << 1) + 2, size, doc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 118 */     if (this.numScorers == 0) return this.doc = 2147483647;
/* 119 */     while (this.subScorers[0].docID() < target) {
/* 120 */       if (this.subScorers[0].advance(target) != 2147483647) {
/* 121 */         heapAdjust(0); continue;
/*     */       }
/* 123 */       heapRemoveRoot();
/* 124 */       if (this.numScorers == 0) {
/* 125 */         return this.doc = 2147483647;
/*     */       }
/*     */     }
/*     */ 
/* 129 */     return this.doc = this.subScorers[0].docID();
/*     */   }
/*     */ 
/*     */   private void heapify()
/*     */   {
/* 134 */     for (int i = (this.numScorers >> 1) - 1; i >= 0; i--)
/* 135 */       heapAdjust(i);
/*     */   }
/*     */ 
/*     */   private void heapAdjust(int root)
/*     */   {
/* 143 */     Scorer scorer = this.subScorers[root];
/* 144 */     int doc = scorer.docID();
/* 145 */     int i = root;
/* 146 */     while (i <= (this.numScorers >> 1) - 1) {
/* 147 */       int lchild = (i << 1) + 1;
/* 148 */       Scorer lscorer = this.subScorers[lchild];
/* 149 */       int ldoc = lscorer.docID();
/* 150 */       int rdoc = 2147483647; int rchild = (i << 1) + 2;
/* 151 */       Scorer rscorer = null;
/* 152 */       if (rchild < this.numScorers) {
/* 153 */         rscorer = this.subScorers[rchild];
/* 154 */         rdoc = rscorer.docID();
/*     */       }
/* 156 */       if (ldoc < doc) {
/* 157 */         if (rdoc < ldoc) {
/* 158 */           this.subScorers[i] = rscorer;
/* 159 */           this.subScorers[rchild] = scorer;
/* 160 */           i = rchild;
/*     */         } else {
/* 162 */           this.subScorers[i] = lscorer;
/* 163 */           this.subScorers[lchild] = scorer;
/* 164 */           i = lchild;
/*     */         }
/* 166 */       } else if (rdoc < doc) {
/* 167 */         this.subScorers[i] = rscorer;
/* 168 */         this.subScorers[rchild] = scorer;
/* 169 */         i = rchild;
/*     */       } else {
/* 171 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void heapRemoveRoot()
/*     */   {
/* 178 */     if (this.numScorers == 1) {
/* 179 */       this.subScorers[0] = null;
/* 180 */       this.numScorers = 0;
/*     */     } else {
/* 182 */       this.subScorers[0] = this.subScorers[(this.numScorers - 1)];
/* 183 */       this.subScorers[(this.numScorers - 1)] = null;
/* 184 */       this.numScorers -= 1;
/* 185 */       heapAdjust(0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.DisjunctionMaxScorer
 * JD-Core Version:    0.6.0
 */