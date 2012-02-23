/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ 
/*     */ class ConjunctionScorer extends Scorer
/*     */ {
/*     */   private final Scorer[] scorers;
/*     */   private final float coord;
/*  30 */   private int lastDoc = -1;
/*     */ 
/*     */   public ConjunctionScorer(Weight weight, float coord, Collection<Scorer> scorers) throws IOException {
/*  33 */     this(weight, coord, (Scorer[])scorers.toArray(new Scorer[scorers.size()]));
/*     */   }
/*     */ 
/*     */   public ConjunctionScorer(Weight weight, float coord, Scorer[] scorers) throws IOException {
/*  37 */     super(weight);
/*  38 */     this.scorers = scorers;
/*  39 */     this.coord = coord;
/*     */ 
/*  41 */     for (int i = 0; i < scorers.length; i++) {
/*  42 */       if (scorers[i].nextDoc() != 2147483647) {
/*     */         continue;
/*     */       }
/*  45 */       this.lastDoc = 2147483647;
/*  46 */       return;
/*     */     }
/*     */ 
/*  57 */     ArrayUtil.mergeSort(scorers, new Comparator() {
/*     */       public int compare(Scorer o1, Scorer o2) {
/*  59 */         return o1.docID() - o2.docID();
/*     */       }
/*     */     });
/*  72 */     if (doNext() == 2147483647)
/*     */     {
/*  74 */       this.lastDoc = 2147483647;
/*  75 */       return;
/*     */     }
/*     */ 
/*  84 */     int end = scorers.length - 1;
/*  85 */     int max = end >> 1;
/*  86 */     for (int i = 0; i < max; i++) {
/*  87 */       Scorer tmp = scorers[i];
/*  88 */       int idx = end - i - 1;
/*  89 */       scorers[i] = scorers[idx];
/*  90 */       scorers[idx] = tmp;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int doNext() throws IOException {
/*  95 */     int first = 0;
/*  96 */     int doc = this.scorers[(this.scorers.length - 1)].docID();
/*     */     Scorer firstScorer;
/*  98 */     while ((firstScorer = this.scorers[first]).docID() < doc) {
/*  99 */       doc = firstScorer.advance(doc);
/* 100 */       first = first == this.scorers.length - 1 ? 0 : first + 1;
/*     */     }
/* 102 */     return doc;
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 107 */     if (this.lastDoc == 2147483647)
/* 108 */       return this.lastDoc;
/* 109 */     if (this.scorers[(this.scorers.length - 1)].docID() < target) {
/* 110 */       this.scorers[(this.scorers.length - 1)].advance(target);
/*     */     }
/* 112 */     return this.lastDoc = doNext();
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/* 117 */     return this.lastDoc;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/* 122 */     if (this.lastDoc == 2147483647)
/* 123 */       return this.lastDoc;
/* 124 */     if (this.lastDoc == -1) {
/* 125 */       return this.lastDoc = this.scorers[(this.scorers.length - 1)].docID();
/*     */     }
/* 127 */     this.scorers[(this.scorers.length - 1)].nextDoc();
/* 128 */     return this.lastDoc = doNext();
/*     */   }
/*     */ 
/*     */   public float score() throws IOException
/*     */   {
/* 133 */     float sum = 0.0F;
/* 134 */     for (int i = 0; i < this.scorers.length; i++) {
/* 135 */       sum += this.scorers[i].score();
/*     */     }
/* 137 */     return sum * this.coord;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ConjunctionScorer
 * JD-Core Version:    0.6.0
 */