/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ 
/*     */ public abstract class CachingCollector extends Collector
/*     */ {
/*     */   private static final int MAX_ARRAY_SIZE = 524288;
/*     */   private static final int INITIAL_ARRAY_SIZE = 128;
/*  55 */   private static final int[] EMPTY_INT_ARRAY = new int[0];
/*     */   protected final Collector other;
/*     */   protected final int maxDocsToCache;
/* 317 */   protected final List<SegStart> cachedSegs = new ArrayList();
/*     */   protected final List<int[]> cachedDocs;
/*     */   private IndexReader lastReader;
/*     */   protected int[] curDocs;
/*     */   protected int upto;
/*     */   protected int base;
/*     */   protected int lastDocBase;
/*     */ 
/*     */   public static CachingCollector create(boolean acceptDocsOutOfOrder, boolean cacheScores, double maxRAMMB)
/*     */   {
/* 336 */     Collector other = new Collector(acceptDocsOutOfOrder)
/*     */     {
/*     */       public boolean acceptsDocsOutOfOrder() {
/* 339 */         return this.val$acceptDocsOutOfOrder;
/*     */       }
/*     */ 
/*     */       public void setScorer(Scorer scorer)
/*     */         throws IOException
/*     */       {
/*     */       }
/*     */ 
/*     */       public void collect(int doc)
/*     */         throws IOException
/*     */       {
/*     */       }
/*     */ 
/*     */       public void setNextReader(IndexReader reader, int docBase)
/*     */         throws IOException
/*     */       {
/*     */       }
/*     */     };
/* 352 */     return create(other, cacheScores, maxRAMMB);
/*     */   }
/*     */ 
/*     */   public static CachingCollector create(Collector other, boolean cacheScores, double maxRAMMB)
/*     */   {
/* 370 */     return cacheScores ? new ScoreCachingCollector(other, maxRAMMB) : new NoScoreCachingCollector(other, maxRAMMB);
/*     */   }
/*     */ 
/*     */   public static CachingCollector create(Collector other, boolean cacheScores, int maxDocsToCache)
/*     */   {
/* 388 */     return cacheScores ? new ScoreCachingCollector(other, maxDocsToCache) : new NoScoreCachingCollector(other, maxDocsToCache);
/*     */   }
/*     */ 
/*     */   private CachingCollector(Collector other, double maxRAMMB, boolean cacheScores)
/*     */   {
/* 393 */     this.other = other;
/*     */ 
/* 395 */     this.cachedDocs = new ArrayList();
/* 396 */     this.curDocs = new int[''];
/* 397 */     this.cachedDocs.add(this.curDocs);
/*     */ 
/* 399 */     int bytesPerDoc = 4;
/* 400 */     if (cacheScores) {
/* 401 */       bytesPerDoc += 4;
/*     */     }
/* 403 */     this.maxDocsToCache = (int)(maxRAMMB * 1024.0D * 1024.0D / bytesPerDoc);
/*     */   }
/*     */ 
/*     */   private CachingCollector(Collector other, int maxDocsToCache) {
/* 407 */     this.other = other;
/*     */ 
/* 409 */     this.cachedDocs = new ArrayList();
/* 410 */     this.curDocs = new int[''];
/* 411 */     this.cachedDocs.add(this.curDocs);
/* 412 */     this.maxDocsToCache = maxDocsToCache;
/*     */   }
/*     */ 
/*     */   public boolean acceptsDocsOutOfOrder()
/*     */   {
/* 417 */     return this.other.acceptsDocsOutOfOrder();
/*     */   }
/*     */ 
/*     */   public boolean isCached() {
/* 421 */     return this.curDocs != null;
/*     */   }
/*     */ 
/*     */   public void setNextReader(IndexReader reader, int docBase) throws IOException
/*     */   {
/* 426 */     this.other.setNextReader(reader, docBase);
/* 427 */     if (this.lastReader != null) {
/* 428 */       this.cachedSegs.add(new SegStart(this.lastReader, this.lastDocBase, this.base + this.upto));
/*     */     }
/* 430 */     this.lastDocBase = docBase;
/* 431 */     this.lastReader = reader;
/*     */   }
/*     */ 
/*     */   void replayInit(Collector other)
/*     */   {
/* 436 */     if (!isCached()) {
/* 437 */       throw new IllegalStateException("cannot replay: cache was cleared because too much RAM was required");
/*     */     }
/*     */ 
/* 440 */     if ((!other.acceptsDocsOutOfOrder()) && (this.other.acceptsDocsOutOfOrder())) {
/* 441 */       throw new IllegalArgumentException("cannot replay: given collector does not support out-of-order collection, while the wrapped collector does. Therefore cached documents may be out-of-order.");
/*     */     }
/*     */ 
/* 448 */     if (this.lastReader != null) {
/* 449 */       this.cachedSegs.add(new SegStart(this.lastReader, this.lastDocBase, this.base + this.upto));
/* 450 */       this.lastReader = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void replay(Collector paramCollector)
/*     */     throws IOException;
/*     */ 
/*     */   private static final class NoScoreCachingCollector extends CachingCollector
/*     */   {
/*     */     NoScoreCachingCollector(Collector other, double maxRAMMB)
/*     */     {
/* 222 */       super(maxRAMMB, false, null);
/*     */     }
/*     */ 
/*     */     NoScoreCachingCollector(Collector other, int maxDocsToCache) {
/* 226 */       super(maxDocsToCache, null);
/*     */     }
/*     */ 
/*     */     public void collect(int doc)
/*     */       throws IOException
/*     */     {
/* 232 */       if (this.curDocs == null)
/*     */       {
/* 234 */         this.other.collect(doc);
/* 235 */         return;
/*     */       }
/*     */ 
/* 239 */       if (this.upto == this.curDocs.length) {
/* 240 */         this.base += this.upto;
/*     */ 
/* 243 */         int nextLength = 8 * this.curDocs.length;
/* 244 */         if (nextLength > 524288) {
/* 245 */           nextLength = 524288;
/*     */         }
/*     */ 
/* 248 */         if (this.base + nextLength > this.maxDocsToCache)
/*     */         {
/* 250 */           nextLength = this.maxDocsToCache - this.base;
/* 251 */           if (nextLength <= 0)
/*     */           {
/* 253 */             this.curDocs = null;
/* 254 */             this.cachedSegs.clear();
/* 255 */             this.cachedDocs.clear();
/* 256 */             this.other.collect(doc);
/* 257 */             return;
/*     */           }
/*     */         }
/*     */ 
/* 261 */         this.curDocs = new int[nextLength];
/* 262 */         this.cachedDocs.add(this.curDocs);
/* 263 */         this.upto = 0;
/*     */       }
/*     */ 
/* 266 */       this.curDocs[this.upto] = doc;
/* 267 */       this.upto += 1;
/* 268 */       this.other.collect(doc);
/*     */     }
/*     */ 
/*     */     public void replay(Collector other) throws IOException
/*     */     {
/* 273 */       replayInit(other);
/*     */ 
/* 275 */       int curUpto = 0;
/* 276 */       int curbase = 0;
/* 277 */       int chunkUpto = 0;
/* 278 */       this.curDocs = CachingCollector.EMPTY_INT_ARRAY;
/* 279 */       for (CachingCollector.SegStart seg : this.cachedSegs) {
/* 280 */         other.setNextReader(seg.reader, seg.base);
/* 281 */         while (curbase + curUpto < seg.end) {
/* 282 */           if (curUpto == this.curDocs.length) {
/* 283 */             curbase += this.curDocs.length;
/* 284 */             this.curDocs = ((int[])this.cachedDocs.get(chunkUpto));
/* 285 */             chunkUpto++;
/* 286 */             curUpto = 0;
/*     */           }
/* 288 */           other.collect(this.curDocs[(curUpto++)]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer) throws IOException
/*     */     {
/* 295 */       this.other.setScorer(scorer);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 300 */       if (isCached()) {
/* 301 */         return "CachingCollector (" + (this.base + this.upto) + " docs cached)";
/*     */       }
/* 303 */       return "CachingCollector (cache was cleared)";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ScoreCachingCollector extends CachingCollector
/*     */   {
/*     */     private final CachingCollector.CachedScorer cachedScorer;
/*     */     private final List<float[]> cachedScores;
/*     */     private Scorer scorer;
/*     */     private float[] curScores;
/*     */ 
/*     */     ScoreCachingCollector(Collector other, double maxRAMMB)
/*     */     {
/* 106 */       super(maxRAMMB, true, null);
/*     */ 
/* 108 */       this.cachedScorer = new CachingCollector.CachedScorer(null);
/* 109 */       this.cachedScores = new ArrayList();
/* 110 */       this.curScores = new float[''];
/* 111 */       this.cachedScores.add(this.curScores);
/*     */     }
/*     */ 
/*     */     ScoreCachingCollector(Collector other, int maxDocsToCache) {
/* 115 */       super(maxDocsToCache, null);
/*     */ 
/* 117 */       this.cachedScorer = new CachingCollector.CachedScorer(null);
/* 118 */       this.cachedScores = new ArrayList();
/* 119 */       this.curScores = new float[''];
/* 120 */       this.cachedScores.add(this.curScores);
/*     */     }
/*     */ 
/*     */     public void collect(int doc)
/*     */       throws IOException
/*     */     {
/* 126 */       if (this.curDocs == null)
/*     */       {
/* 128 */         this.cachedScorer.score = this.scorer.score();
/* 129 */         this.cachedScorer.doc = doc;
/* 130 */         this.other.collect(doc);
/* 131 */         return;
/*     */       }
/*     */ 
/* 135 */       if (this.upto == this.curDocs.length) {
/* 136 */         this.base += this.upto;
/*     */ 
/* 139 */         int nextLength = 8 * this.curDocs.length;
/* 140 */         if (nextLength > 524288) {
/* 141 */           nextLength = 524288;
/*     */         }
/*     */ 
/* 144 */         if (this.base + nextLength > this.maxDocsToCache)
/*     */         {
/* 146 */           nextLength = this.maxDocsToCache - this.base;
/* 147 */           if (nextLength <= 0)
/*     */           {
/* 149 */             this.curDocs = null;
/* 150 */             this.curScores = null;
/* 151 */             this.cachedSegs.clear();
/* 152 */             this.cachedDocs.clear();
/* 153 */             this.cachedScores.clear();
/* 154 */             this.cachedScorer.score = this.scorer.score();
/* 155 */             this.cachedScorer.doc = doc;
/* 156 */             this.other.collect(doc);
/* 157 */             return;
/*     */           }
/*     */         }
/*     */ 
/* 161 */         this.curDocs = new int[nextLength];
/* 162 */         this.cachedDocs.add(this.curDocs);
/* 163 */         this.curScores = new float[nextLength];
/* 164 */         this.cachedScores.add(this.curScores);
/* 165 */         this.upto = 0;
/*     */       }
/*     */ 
/* 168 */       this.curDocs[this.upto] = doc;
/* 169 */       this.cachedScorer.score = (this.curScores[this.upto] = this.scorer.score());
/* 170 */       this.upto += 1;
/* 171 */       this.cachedScorer.doc = doc;
/* 172 */       this.other.collect(doc);
/*     */     }
/*     */ 
/*     */     public void replay(Collector other) throws IOException
/*     */     {
/* 177 */       replayInit(other);
/*     */ 
/* 179 */       int curUpto = 0;
/* 180 */       int curBase = 0;
/* 181 */       int chunkUpto = 0;
/* 182 */       this.curDocs = CachingCollector.EMPTY_INT_ARRAY;
/* 183 */       for (CachingCollector.SegStart seg : this.cachedSegs) {
/* 184 */         other.setNextReader(seg.reader, seg.base);
/* 185 */         other.setScorer(this.cachedScorer);
/* 186 */         while (curBase + curUpto < seg.end) {
/* 187 */           if (curUpto == this.curDocs.length) {
/* 188 */             curBase += this.curDocs.length;
/* 189 */             this.curDocs = ((int[])this.cachedDocs.get(chunkUpto));
/* 190 */             this.curScores = ((float[])this.cachedScores.get(chunkUpto));
/* 191 */             chunkUpto++;
/* 192 */             curUpto = 0;
/*     */           }
/* 194 */           this.cachedScorer.score = this.curScores[curUpto];
/* 195 */           this.cachedScorer.doc = this.curDocs[curUpto];
/* 196 */           other.collect(this.curDocs[(curUpto++)]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer) throws IOException
/*     */     {
/* 203 */       this.scorer = scorer;
/* 204 */       this.other.setScorer(this.cachedScorer);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 209 */       if (isCached()) {
/* 210 */         return "CachingCollector (" + (this.base + this.upto) + " docs & scores cached)";
/*     */       }
/* 212 */       return "CachingCollector (cache was cleared)";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class CachedScorer extends Scorer
/*     */   {
/*     */     int doc;
/*     */     float score;
/*     */ 
/*     */     private CachedScorer()
/*     */     {
/*  78 */       super();
/*     */     }
/*     */     public final float score() {
/*  81 */       return this.score;
/*     */     }
/*     */     public final int advance(int target) {
/*  84 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     public final int docID() {
/*  87 */       return this.doc;
/*     */     }
/*     */     public final float freq() {
/*  90 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     public final int nextDoc() {
/*  93 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SegStart
/*     */   {
/*     */     public final IndexReader reader;
/*     */     public final int base;
/*     */     public final int end;
/*     */ 
/*     */     public SegStart(IndexReader reader, int base, int end)
/*     */     {
/*  63 */       this.reader = reader;
/*  64 */       this.base = base;
/*  65 */       this.end = end;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.CachingCollector
 * JD-Core Version:    0.6.0
 */