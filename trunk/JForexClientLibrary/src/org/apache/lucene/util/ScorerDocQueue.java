/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.search.Scorer;
/*     */ 
/*     */ public class ScorerDocQueue
/*     */ {
/*     */   private final HeapedScorerDoc[] heap;
/*     */   private final int maxSize;
/*     */   private int size;
/*     */   private HeapedScorerDoc topHSD;
/*     */ 
/*     */   public ScorerDocQueue(int maxSize)
/*     */   {
/*  57 */     this.size = 0;
/*  58 */     int heapSize = maxSize + 1;
/*  59 */     this.heap = new HeapedScorerDoc[heapSize];
/*  60 */     this.maxSize = maxSize;
/*  61 */     this.topHSD = this.heap[1];
/*     */   }
/*     */ 
/*     */   public final void put(Scorer scorer)
/*     */   {
/*  70 */     this.size += 1;
/*  71 */     this.heap[this.size] = new HeapedScorerDoc(scorer);
/*  72 */     upHeap();
/*     */   }
/*     */ 
/*     */   public boolean insert(Scorer scorer)
/*     */   {
/*  82 */     if (this.size < this.maxSize) {
/*  83 */       put(scorer);
/*  84 */       return true;
/*     */     }
/*  86 */     int docNr = scorer.docID();
/*  87 */     if ((this.size > 0) && (docNr >= this.topHSD.doc)) {
/*  88 */       this.heap[1] = new HeapedScorerDoc(scorer, docNr);
/*  89 */       downHeap();
/*  90 */       return true;
/*     */     }
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */   public final Scorer top()
/*     */   {
/* 102 */     return this.topHSD.scorer;
/*     */   }
/*     */ 
/*     */   public final int topDoc()
/*     */   {
/* 111 */     return this.topHSD.doc;
/*     */   }
/*     */ 
/*     */   public final float topScore() throws IOException
/*     */   {
/* 116 */     return this.topHSD.scorer.score();
/*     */   }
/*     */ 
/*     */   public final boolean topNextAndAdjustElsePop() throws IOException {
/* 120 */     return checkAdjustElsePop(this.topHSD.scorer.nextDoc() != 2147483647);
/*     */   }
/*     */ 
/*     */   public final boolean topSkipToAndAdjustElsePop(int target) throws IOException {
/* 124 */     return checkAdjustElsePop(this.topHSD.scorer.advance(target) != 2147483647);
/*     */   }
/*     */ 
/*     */   private boolean checkAdjustElsePop(boolean cond) {
/* 128 */     if (cond) {
/* 129 */       this.topHSD.doc = this.topHSD.scorer.docID();
/*     */     } else {
/* 131 */       this.heap[1] = this.heap[this.size];
/* 132 */       this.heap[this.size] = null;
/* 133 */       this.size -= 1;
/*     */     }
/* 135 */     downHeap();
/* 136 */     return cond;
/*     */   }
/*     */ 
/*     */   public final Scorer pop()
/*     */   {
/* 145 */     Scorer result = this.topHSD.scorer;
/* 146 */     popNoResult();
/* 147 */     return result;
/*     */   }
/*     */ 
/*     */   private final void popNoResult()
/*     */   {
/* 154 */     this.heap[1] = this.heap[this.size];
/* 155 */     this.heap[this.size] = null;
/* 156 */     this.size -= 1;
/* 157 */     downHeap();
/*     */   }
/*     */ 
/*     */   public final void adjustTop()
/*     */   {
/* 169 */     this.topHSD.adjust();
/* 170 */     downHeap();
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 175 */     return this.size;
/*     */   }
/*     */ 
/*     */   public final void clear()
/*     */   {
/* 180 */     for (int i = 0; i <= this.size; i++) {
/* 181 */       this.heap[i] = null;
/*     */     }
/* 183 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   private final void upHeap() {
/* 187 */     int i = this.size;
/* 188 */     HeapedScorerDoc node = this.heap[i];
/* 189 */     int j = i >>> 1;
/* 190 */     while ((j > 0) && (node.doc < this.heap[j].doc)) {
/* 191 */       this.heap[i] = this.heap[j];
/* 192 */       i = j;
/* 193 */       j >>>= 1;
/*     */     }
/* 195 */     this.heap[i] = node;
/* 196 */     this.topHSD = this.heap[1];
/*     */   }
/*     */ 
/*     */   private final void downHeap() {
/* 200 */     int i = 1;
/* 201 */     HeapedScorerDoc node = this.heap[i];
/* 202 */     int j = i << 1;
/* 203 */     int k = j + 1;
/* 204 */     if ((k <= this.size) && (this.heap[k].doc < this.heap[j].doc)) {
/* 205 */       j = k;
/*     */     }
/* 207 */     while ((j <= this.size) && (this.heap[j].doc < node.doc)) {
/* 208 */       this.heap[i] = this.heap[j];
/* 209 */       i = j;
/* 210 */       j = i << 1;
/* 211 */       k = j + 1;
/* 212 */       if ((k <= this.size) && (this.heap[k].doc < this.heap[j].doc)) {
/* 213 */         j = k;
/*     */       }
/*     */     }
/* 216 */     this.heap[i] = node;
/* 217 */     this.topHSD = this.heap[1];
/*     */   }
/*     */ 
/*     */   private class HeapedScorerDoc
/*     */   {
/*     */     Scorer scorer;
/*     */     int doc;
/*     */ 
/*     */     HeapedScorerDoc(Scorer s)
/*     */     {
/*  42 */       this(s, s.docID());
/*     */     }
/*     */     HeapedScorerDoc(Scorer scorer, int doc) {
/*  45 */       this.scorer = scorer;
/*  46 */       this.doc = doc;
/*     */     }
/*     */     void adjust() {
/*  49 */       this.doc = this.scorer.docID();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ScorerDocQueue
 * JD-Core Version:    0.6.0
 */