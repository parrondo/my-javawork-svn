/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ abstract class PhraseScorer extends Scorer
/*     */ {
/*     */   protected byte[] norms;
/*     */   protected float value;
/*  36 */   private boolean firstTime = true;
/*  37 */   private boolean more = true;
/*     */   protected PhraseQueue pq;
/*     */   protected PhrasePositions first;
/*     */   protected PhrasePositions last;
/*     */   private float freq;
/*     */ 
/*     */   PhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity similarity, byte[] norms)
/*     */   {
/*  45 */     super(similarity, weight);
/*  46 */     this.norms = norms;
/*  47 */     this.value = weight.getValue();
/*     */ 
/*  54 */     for (int i = 0; i < postings.length; i++) {
/*  55 */       PhrasePositions pp = new PhrasePositions(postings[i].postings, postings[i].position, i);
/*  56 */       if (this.last != null)
/*  57 */         this.last.next = pp;
/*     */       else {
/*  59 */         this.first = pp;
/*     */       }
/*  61 */       this.last = pp;
/*     */     }
/*     */ 
/*  64 */     this.pq = new PhraseQueue(postings.length);
/*  65 */     this.first.doc = -1;
/*     */   }
/*     */ 
/*     */   public int docID() {
/*  69 */     return this.first.doc;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException {
/*  73 */     if (this.firstTime) {
/*  74 */       init();
/*  75 */       this.firstTime = false;
/*  76 */     } else if (this.more) {
/*  77 */       this.more = this.last.next();
/*     */     }
/*  79 */     if (!doNext()) {
/*  80 */       this.first.doc = 2147483647;
/*     */     }
/*  82 */     return this.first.doc;
/*     */   }
/*     */ 
/*     */   private boolean doNext() throws IOException
/*     */   {
/*  87 */     while (this.more) {
/*  88 */       while ((this.more) && (this.first.doc < this.last.doc)) {
/*  89 */         this.more = this.first.skipTo(this.last.doc);
/*  90 */         firstToLast();
/*     */       }
/*     */ 
/*  93 */       if (!this.more)
/*     */         continue;
/*  95 */       this.freq = phraseFreq();
/*  96 */       if (this.freq == 0.0F) {
/*  97 */         this.more = this.last.next(); continue;
/*     */       }
/*  99 */       return true;
/*     */     }
/*     */ 
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */     throws IOException
/*     */   {
/* 108 */     float raw = getSimilarity().tf(this.freq) * this.value;
/* 109 */     return this.norms == null ? raw : raw * getSimilarity().decodeNormValue(this.norms[this.first.doc]);
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 114 */     this.firstTime = false;
/* 115 */     for (PhrasePositions pp = this.first; (this.more) && (pp != null); pp = pp.next) {
/* 116 */       this.more = pp.skipTo(target);
/*     */     }
/* 118 */     if (this.more) {
/* 119 */       sort();
/*     */     }
/* 121 */     if (!doNext()) {
/* 122 */       this.first.doc = 2147483647;
/*     */     }
/* 124 */     return this.first.doc;
/*     */   }
/*     */ 
/*     */   public final float freq()
/*     */   {
/* 132 */     return this.freq;
/*     */   }
/*     */ 
/*     */   protected abstract float phraseFreq()
/*     */     throws IOException;
/*     */ 
/*     */   private void init()
/*     */     throws IOException
/*     */   {
/* 145 */     for (PhrasePositions pp = this.first; (this.more) && (pp != null); pp = pp.next) {
/* 146 */       this.more = pp.next();
/*     */     }
/* 148 */     if (this.more)
/* 149 */       sort();
/*     */   }
/*     */ 
/*     */   private void sort()
/*     */   {
/* 154 */     this.pq.clear();
/* 155 */     for (PhrasePositions pp = this.first; pp != null; pp = pp.next) {
/* 156 */       this.pq.add(pp);
/*     */     }
/* 158 */     pqToList();
/*     */   }
/*     */ 
/*     */   protected final void pqToList() {
/* 162 */     this.last = (this.first = null);
/* 163 */     while (this.pq.top() != null) {
/* 164 */       PhrasePositions pp = (PhrasePositions)this.pq.pop();
/* 165 */       if (this.last != null)
/* 166 */         this.last.next = pp;
/*     */       else
/* 168 */         this.first = pp;
/* 169 */       this.last = pp;
/* 170 */       pp.next = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void firstToLast() {
/* 175 */     this.last.next = this.first;
/* 176 */     this.last = this.first;
/* 177 */     this.first = this.first.next;
/* 178 */     this.last.next = null;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 182 */     return "scorer(" + this.weight + ")";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PhraseScorer
 * JD-Core Version:    0.6.0
 */