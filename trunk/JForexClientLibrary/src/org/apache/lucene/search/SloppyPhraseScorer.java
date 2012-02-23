/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ final class SloppyPhraseScorer extends PhraseScorer
/*     */ {
/*     */   private int slop;
/*     */   private PhrasePositions[] repeats;
/*     */   private PhrasePositions[] tmpPos;
/*     */   private boolean checkedRepeats;
/*     */ 
/*     */   SloppyPhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity similarity, int slop, byte[] norms)
/*     */   {
/*  31 */     super(weight, postings, similarity, norms);
/*  32 */     this.slop = slop;
/*     */   }
/*     */ 
/*     */   protected float phraseFreq()
/*     */     throws IOException
/*     */   {
/*  55 */     int end = initPhrasePositions();
/*     */ 
/*  57 */     float freq = 0.0F;
/*  58 */     boolean done = end < 0;
/*  59 */     while (!done) {
/*  60 */       PhrasePositions pp = (PhrasePositions)this.pq.pop();
/*  61 */       int start = pp.position;
/*  62 */       int next = ((PhrasePositions)this.pq.top()).position;
/*     */ 
/*  64 */       boolean tpsDiffer = true;
/*  65 */       for (int pos = start; (pos <= next) || (!tpsDiffer); pos = pp.position) {
/*  66 */         if ((pos <= next) && (tpsDiffer))
/*  67 */           start = pos;
/*  68 */         if (!pp.nextPosition()) {
/*  69 */           done = true;
/*  70 */           break;
/*     */         }
/*  72 */         PhrasePositions pp2 = null;
/*  73 */         tpsDiffer = (!pp.repeats) || ((pp2 = termPositionsDiffer(pp)) == null);
/*  74 */         if ((pp2 != null) && (pp2 != pp)) {
/*  75 */           pp = flip(pp, pp2);
/*     */         }
/*     */       }
/*     */ 
/*  79 */       int matchLength = end - start;
/*  80 */       if (matchLength <= this.slop) {
/*  81 */         freq += getSimilarity().sloppyFreq(matchLength);
/*     */       }
/*  83 */       if (pp.position > end)
/*  84 */         end = pp.position;
/*  85 */       this.pq.add(pp);
/*     */     }
/*     */ 
/*  88 */     return freq;
/*     */   }
/*     */ 
/*     */   private PhrasePositions flip(PhrasePositions pp, PhrasePositions pp2)
/*     */   {
/*  95 */     int n = 0;
/*     */     PhrasePositions pp3;
/*  98 */     while ((pp3 = (PhrasePositions)this.pq.pop()) != pp2) {
/*  99 */       this.tmpPos[(n++)] = pp3;
/*     */     }
/*     */ 
/* 102 */     for (n--; n >= 0; n--) {
/* 103 */       this.pq.insertWithOverflow(this.tmpPos[n]);
/*     */     }
/*     */ 
/* 106 */     this.pq.add(pp);
/* 107 */     return pp2;
/*     */   }
/*     */ 
/*     */   private int initPhrasePositions()
/*     */     throws IOException
/*     */   {
/* 131 */     int end = 0;
/*     */ 
/* 134 */     if ((this.checkedRepeats) && (this.repeats == null))
/*     */     {
/* 136 */       this.pq.clear();
/* 137 */       for (PhrasePositions pp = this.first; pp != null; pp = pp.next) {
/* 138 */         pp.firstPosition();
/* 139 */         if (pp.position > end)
/* 140 */           end = pp.position;
/* 141 */         this.pq.add(pp);
/*     */       }
/* 143 */       return end;
/*     */     }
/*     */ 
/* 147 */     for (PhrasePositions pp = this.first; pp != null; pp = pp.next) {
/* 148 */       pp.firstPosition();
/*     */     }
/*     */ 
/* 151 */     if (!this.checkedRepeats) {
/* 152 */       this.checkedRepeats = true;
/*     */ 
/* 154 */       HashSet m = null;
/* 155 */       for (PhrasePositions pp = this.first; pp != null; pp = pp.next) {
/* 156 */         int tpPos = pp.position + pp.offset;
/* 157 */         for (PhrasePositions pp2 = pp.next; pp2 != null; pp2 = pp2.next) {
/* 158 */           if (pp.offset == pp2.offset) {
/*     */             continue;
/*     */           }
/* 161 */           int tpPos2 = pp2.position + pp2.offset;
/* 162 */           if (tpPos2 == tpPos) {
/* 163 */             if (m == null)
/* 164 */               m = new HashSet();
/* 165 */             pp.repeats = true;
/* 166 */             pp2.repeats = true;
/* 167 */             m.add(pp);
/* 168 */             m.add(pp2);
/*     */           }
/*     */         }
/*     */       }
/* 172 */       if (m != null) {
/* 173 */         this.repeats = ((PhrasePositions[])m.toArray(new PhrasePositions[0]));
/*     */       }
/*     */     }
/*     */ 
/* 177 */     if (this.repeats != null) {
/* 178 */       for (int i = 0; i < this.repeats.length; i++) {
/* 179 */         PhrasePositions pp = this.repeats[i];
/*     */         PhrasePositions pp2;
/* 181 */         while ((pp2 = termPositionsDiffer(pp)) != null) {
/* 182 */           if (!pp2.nextPosition()) {
/* 183 */             return -1;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 189 */     this.pq.clear();
/* 190 */     for (PhrasePositions pp = this.first; pp != null; pp = pp.next) {
/* 191 */       if (pp.position > end)
/* 192 */         end = pp.position;
/* 193 */       this.pq.add(pp);
/*     */     }
/*     */ 
/* 196 */     if (this.repeats != null) {
/* 197 */       this.tmpPos = new PhrasePositions[this.pq.size()];
/*     */     }
/* 199 */     return end;
/*     */   }
/*     */ 
/*     */   private PhrasePositions termPositionsDiffer(PhrasePositions pp)
/*     */   {
/* 213 */     int tpPos = pp.position + pp.offset;
/* 214 */     for (int i = 0; i < this.repeats.length; i++) {
/* 215 */       PhrasePositions pp2 = this.repeats[i];
/* 216 */       if (pp2 == pp) {
/*     */         continue;
/*     */       }
/* 219 */       if (pp.offset == pp2.offset) {
/*     */         continue;
/*     */       }
/* 222 */       int tpPos2 = pp2.position + pp2.offset;
/* 223 */       if (tpPos2 == tpPos) {
/* 224 */         return pp.offset > pp2.offset ? pp : pp2;
/*     */       }
/*     */     }
/* 227 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SloppyPhraseScorer
 * JD-Core Version:    0.6.0
 */