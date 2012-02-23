/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ 
/*     */ final class TermScorer extends Scorer
/*     */ {
/*     */   private TermDocs termDocs;
/*     */   private byte[] norms;
/*     */   private float weightValue;
/*  30 */   private int doc = -1;
/*     */   private int freq;
/*  33 */   private final int[] docs = new int[32];
/*  34 */   private final int[] freqs = new int[32];
/*     */   private int pointer;
/*     */   private int pointerMax;
/*     */   private static final int SCORE_CACHE_SIZE = 32;
/*  39 */   private float[] scoreCache = new float[32];
/*     */ 
/*     */   TermScorer(Weight weight, TermDocs td, Similarity similarity, byte[] norms)
/*     */   {
/*  55 */     super(similarity, weight);
/*     */ 
/*  57 */     this.termDocs = td;
/*  58 */     this.norms = norms;
/*  59 */     this.weightValue = weight.getValue();
/*     */ 
/*  61 */     for (int i = 0; i < 32; i++)
/*  62 */       this.scoreCache[i] = (getSimilarity().tf(i) * this.weightValue);
/*     */   }
/*     */ 
/*     */   public void score(Collector c) throws IOException
/*     */   {
/*  67 */     score(c, 2147483647, nextDoc());
/*     */   }
/*     */ 
/*     */   protected boolean score(Collector c, int end, int firstDocID)
/*     */     throws IOException
/*     */   {
/*  73 */     c.setScorer(this);
/*  74 */     while (this.doc < end) {
/*  75 */       c.collect(this.doc);
/*     */ 
/*  77 */       if (++this.pointer >= this.pointerMax) {
/*  78 */         this.pointerMax = this.termDocs.read(this.docs, this.freqs);
/*  79 */         if (this.pointerMax != 0) {
/*  80 */           this.pointer = 0;
/*     */         } else {
/*  82 */           this.termDocs.close();
/*  83 */           this.doc = 2147483647;
/*  84 */           return false;
/*     */         }
/*     */       }
/*  87 */       this.doc = this.docs[this.pointer];
/*  88 */       this.freq = this.freqs[this.pointer];
/*     */     }
/*  90 */     return true;
/*     */   }
/*     */ 
/*     */   public int docID() {
/*  94 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public float freq() {
/*  98 */     return this.freq;
/*     */   }
/*     */ 
/*     */   public int nextDoc()
/*     */     throws IOException
/*     */   {
/* 110 */     this.pointer += 1;
/* 111 */     if (this.pointer >= this.pointerMax) {
/* 112 */       this.pointerMax = this.termDocs.read(this.docs, this.freqs);
/* 113 */       if (this.pointerMax != 0) {
/* 114 */         this.pointer = 0;
/*     */       } else {
/* 116 */         this.termDocs.close();
/* 117 */         return this.doc = 2147483647;
/*     */       }
/*     */     }
/* 120 */     this.doc = this.docs[this.pointer];
/* 121 */     this.freq = this.freqs[this.pointer];
/* 122 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */   {
/* 127 */     assert (this.doc != -1);
/* 128 */     float raw = this.freq < 32 ? this.scoreCache[this.freq] : getSimilarity().tf(this.freq) * this.weightValue;
/*     */ 
/* 133 */     return this.norms == null ? raw : raw * getSimilarity().decodeNormValue(this.norms[this.doc]);
/*     */   }
/*     */ 
/*     */   public int advance(int target)
/*     */     throws IOException
/*     */   {
/* 148 */     for (this.pointer += 1; this.pointer < this.pointerMax; this.pointer += 1) {
/* 149 */       if (this.docs[this.pointer] >= target) {
/* 150 */         this.freq = this.freqs[this.pointer];
/* 151 */         return this.doc = this.docs[this.pointer];
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 156 */     boolean result = this.termDocs.skipTo(target);
/* 157 */     if (result) {
/* 158 */       this.pointerMax = 1;
/* 159 */       this.pointer = 0;
/*     */       int tmp118_113 = this.termDocs.doc(); this.doc = tmp118_113; this.docs[this.pointer] = tmp118_113;
/*     */       int tmp141_136 = this.termDocs.freq(); this.freq = tmp141_136; this.freqs[this.pointer] = tmp141_136;
/*     */     } else {
/* 163 */       this.doc = 2147483647;
/*     */     }
/* 165 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 170 */     return "scorer(" + this.weight + ")";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermScorer
 * JD-Core Version:    0.6.0
 */