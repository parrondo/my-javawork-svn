/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.index.TermPositions;
/*     */ 
/*     */ final class ExactPhraseScorer extends Scorer
/*     */ {
/*     */   private final byte[] norms;
/*     */   private final float value;
/*     */   private static final int SCORE_CACHE_SIZE = 32;
/*  30 */   private final float[] scoreCache = new float[32];
/*     */   private final int endMinus1;
/*     */   private static final int CHUNK = 4096;
/*     */   private int gen;
/*  37 */   private final int[] counts = new int[4096];
/*  38 */   private final int[] gens = new int[4096];
/*     */   boolean noDocs;
/*     */   private final ChunkState[] chunkStates;
/*  60 */   private int docID = -1;
/*     */   private int freq;
/*     */ 
/*     */   ExactPhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity similarity, byte[] norms)
/*     */     throws IOException
/*     */   {
/*  65 */     super(similarity, weight);
/*  66 */     this.norms = norms;
/*  67 */     this.value = weight.getValue();
/*     */ 
/*  69 */     this.chunkStates = new ChunkState[postings.length];
/*     */ 
/*  71 */     this.endMinus1 = (postings.length - 1);
/*     */ 
/*  73 */     for (int i = 0; i < postings.length; i++)
/*     */     {
/*  81 */       boolean useAdvance = postings[i].docFreq > 5 * postings[0].docFreq;
/*  82 */       this.chunkStates[i] = new ChunkState(postings[i].postings, -postings[i].position, useAdvance);
/*  83 */       if ((i > 0) && (!postings[i].postings.next())) {
/*  84 */         this.noDocs = true;
/*  85 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  89 */     for (int i = 0; i < 32; i++)
/*  90 */       this.scoreCache[i] = (getSimilarity().tf(i) * this.value);
/*     */   }
/*     */ 
/*     */   public int nextDoc()
/*     */     throws IOException
/*     */   {
/*     */     while (true)
/*     */     {
/*  99 */       if (!this.chunkStates[0].posEnum.next()) {
/* 100 */         this.docID = 2147483647;
/* 101 */         return this.docID;
/*     */       }
/*     */ 
/* 104 */       int doc = this.chunkStates[0].posEnum.doc();
/*     */ 
/* 107 */       int i = 1;
/* 108 */       while (i < this.chunkStates.length) {
/* 109 */         ChunkState cs = this.chunkStates[i];
/* 110 */         int doc2 = cs.posEnum.doc();
/* 111 */         if (cs.useAdvance) {
/* 112 */           if (doc2 < doc) {
/* 113 */             if (!cs.posEnum.skipTo(doc)) {
/* 114 */               this.docID = 2147483647;
/* 115 */               return this.docID;
/*     */             }
/* 117 */             doc2 = cs.posEnum.doc();
/*     */           }
/*     */         }
/*     */         else {
/* 121 */           int iter = 0;
/* 122 */           while (doc2 < doc)
/*     */           {
/* 125 */             iter++; if (iter == 50) {
/* 126 */               if (!cs.posEnum.skipTo(doc)) {
/* 127 */                 this.docID = 2147483647;
/* 128 */                 return this.docID;
/*     */               }
/* 130 */               doc2 = cs.posEnum.doc();
/*     */             }
/*     */             else
/*     */             {
/* 134 */               if (cs.posEnum.next()) {
/* 135 */                 doc2 = cs.posEnum.doc(); continue;
/*     */               }
/* 137 */               this.docID = 2147483647;
/* 138 */               return this.docID;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 143 */         if (doc2 > doc) {
/*     */           break;
/*     */         }
/* 146 */         i++;
/*     */       }
/*     */ 
/* 149 */       if (i == this.chunkStates.length)
/*     */       {
/* 152 */         this.docID = doc;
/*     */ 
/* 154 */         this.freq = phraseFreq();
/* 155 */         if (this.freq != 0)
/* 156 */           return this.docID;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int advance(int target)
/*     */     throws IOException
/*     */   {
/* 166 */     if (!this.chunkStates[0].posEnum.skipTo(target)) {
/* 167 */       this.docID = 2147483647;
/* 168 */       return this.docID;
/*     */     }
/* 170 */     int doc = this.chunkStates[0].posEnum.doc();
/*     */     while (true)
/*     */     {
/* 175 */       int i = 1;
/* 176 */       while (i < this.chunkStates.length) {
/* 177 */         int doc2 = this.chunkStates[i].posEnum.doc();
/* 178 */         if (doc2 < doc) {
/* 179 */           if (!this.chunkStates[i].posEnum.skipTo(doc)) {
/* 180 */             this.docID = 2147483647;
/* 181 */             return this.docID;
/*     */           }
/* 183 */           doc2 = this.chunkStates[i].posEnum.doc();
/*     */         }
/*     */ 
/* 186 */         if (doc2 > doc) {
/*     */           break;
/*     */         }
/* 189 */         i++;
/*     */       }
/*     */ 
/* 192 */       if (i == this.chunkStates.length)
/*     */       {
/* 195 */         this.docID = doc;
/* 196 */         this.freq = phraseFreq();
/* 197 */         if (this.freq != 0) {
/* 198 */           return this.docID;
/*     */         }
/*     */       }
/*     */ 
/* 202 */       if (!this.chunkStates[0].posEnum.next()) {
/* 203 */         this.docID = 2147483647;
/* 204 */         return this.docID;
/*     */       }
/* 206 */       doc = this.chunkStates[0].posEnum.doc();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 213 */     return "ExactPhraseScorer(" + this.weight + ")";
/*     */   }
/*     */ 
/*     */   public float freq()
/*     */   {
/* 218 */     return this.freq;
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/* 223 */     return this.docID;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */     throws IOException
/*     */   {
/*     */     float raw;
/*     */     float raw;
/* 229 */     if (this.freq < 32)
/* 230 */       raw = this.scoreCache[this.freq];
/*     */     else {
/* 232 */       raw = getSimilarity().tf(this.freq) * this.value;
/*     */     }
/* 234 */     return this.norms == null ? raw : raw * getSimilarity().decodeNormValue(this.norms[this.docID]);
/*     */   }
/*     */ 
/*     */   private int phraseFreq() throws IOException
/*     */   {
/* 239 */     this.freq = 0;
/*     */ 
/* 242 */     for (int i = 0; i < this.chunkStates.length; i++) {
/* 243 */       ChunkState cs = this.chunkStates[i];
/* 244 */       cs.posLimit = cs.posEnum.freq();
/* 245 */       cs.pos = (cs.offset + cs.posEnum.nextPosition());
/* 246 */       cs.posUpto = 1;
/* 247 */       cs.lastPos = -1;
/*     */     }
/*     */ 
/* 250 */     int chunkStart = 0;
/* 251 */     int chunkEnd = 4096;
/*     */ 
/* 254 */     boolean end = false;
/*     */ 
/* 259 */     while (!end)
/*     */     {
/* 261 */       this.gen += 1;
/*     */ 
/* 263 */       if (this.gen == 0)
/*     */       {
/* 265 */         Arrays.fill(this.gens, 0);
/* 266 */         this.gen += 1;
/*     */       }
/*     */ 
/* 271 */       ChunkState cs = this.chunkStates[0];
/* 272 */       while (cs.pos < chunkEnd) {
/* 273 */         if (cs.pos > cs.lastPos) {
/* 274 */           cs.lastPos = cs.pos;
/* 275 */           int posIndex = cs.pos - chunkStart;
/* 276 */           this.counts[posIndex] = 1;
/* 277 */           assert (this.gens[posIndex] != this.gen);
/* 278 */           this.gens[posIndex] = this.gen;
/*     */         }
/*     */ 
/* 281 */         if (cs.posUpto == cs.posLimit) {
/* 282 */           end = true;
/* 283 */           break;
/*     */         }
/* 285 */         cs.posUpto += 1;
/* 286 */         cs.pos = (cs.offset + cs.posEnum.nextPosition());
/*     */       }
/*     */ 
/* 291 */       boolean any = true;
/* 292 */       for (int t = 1; t < this.endMinus1; t++) {
/* 293 */         ChunkState cs = this.chunkStates[t];
/* 294 */         any = false;
/* 295 */         while (cs.pos < chunkEnd) {
/* 296 */           if (cs.pos > cs.lastPos) {
/* 297 */             cs.lastPos = cs.pos;
/* 298 */             int posIndex = cs.pos - chunkStart;
/* 299 */             if ((posIndex >= 0) && (this.gens[posIndex] == this.gen) && (this.counts[posIndex] == t))
/*     */             {
/* 301 */               this.counts[posIndex] += 1;
/* 302 */               any = true;
/*     */             }
/*     */           }
/*     */ 
/* 306 */           if (cs.posUpto == cs.posLimit) {
/* 307 */             end = true;
/* 308 */             break;
/*     */           }
/* 310 */           cs.posUpto += 1;
/* 311 */           cs.pos = (cs.offset + cs.posEnum.nextPosition());
/*     */         }
/*     */ 
/* 314 */         if (!any)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/* 319 */       if (!any)
/*     */       {
/* 321 */         chunkStart += 4096;
/* 322 */         chunkEnd += 4096;
/* 323 */         continue;
/*     */       }
/*     */ 
/* 329 */       ChunkState cs = this.chunkStates[this.endMinus1];
/* 330 */       while (cs.pos < chunkEnd) {
/* 331 */         if (cs.pos > cs.lastPos) {
/* 332 */           cs.lastPos = cs.pos;
/* 333 */           int posIndex = cs.pos - chunkStart;
/* 334 */           if ((posIndex >= 0) && (this.gens[posIndex] == this.gen) && (this.counts[posIndex] == this.endMinus1)) {
/* 335 */             this.freq += 1;
/*     */           }
/*     */         }
/*     */ 
/* 339 */         if (cs.posUpto == cs.posLimit) {
/* 340 */           end = true;
/* 341 */           break;
/*     */         }
/* 343 */         cs.posUpto += 1;
/* 344 */         cs.pos = (cs.offset + cs.posEnum.nextPosition());
/*     */       }
/*     */ 
/* 348 */       chunkStart += 4096;
/* 349 */       chunkEnd += 4096;
/*     */     }
/*     */ 
/* 352 */     return this.freq;
/*     */   }
/*     */ 
/*     */   private static final class ChunkState
/*     */   {
/*     */     final TermPositions posEnum;
/*     */     final int offset;
/*     */     final boolean useAdvance;
/*     */     int posUpto;
/*     */     int posLimit;
/*     */     int pos;
/*     */     int lastPos;
/*     */ 
/*     */     public ChunkState(TermPositions posEnum, int offset, boolean useAdvance)
/*     */     {
/*  52 */       this.posEnum = posEnum;
/*  53 */       this.offset = offset;
/*  54 */       this.useAdvance = useAdvance;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ExactPhraseScorer
 * JD-Core Version:    0.6.0
 */