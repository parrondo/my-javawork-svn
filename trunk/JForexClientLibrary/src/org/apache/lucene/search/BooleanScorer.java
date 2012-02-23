/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ 
/*     */ final class BooleanScorer extends Scorer
/*     */ {
/* 188 */   private SubScorer scorers = null;
/* 189 */   private BucketTable bucketTable = new BucketTable();
/*     */   private final float[] coordFactors;
/* 193 */   private int prohibitedMask = 0;
/* 194 */   private int nextMask = 1;
/*     */   private final int minNrShouldMatch;
/*     */   private int end;
/*     */   private Bucket current;
/* 198 */   private int doc = -1;
/*     */ 
/*     */   BooleanScorer(Weight weight, boolean disableCoord, Similarity similarity, int minNrShouldMatch, List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord) throws IOException
/*     */   {
/* 202 */     super(weight);
/* 203 */     this.minNrShouldMatch = minNrShouldMatch;
/*     */ 
/* 205 */     if ((optionalScorers != null) && (optionalScorers.size() > 0)) {
/* 206 */       for (Scorer scorer : optionalScorers) {
/* 207 */         if (scorer.nextDoc() != 2147483647) {
/* 208 */           this.scorers = new SubScorer(scorer, false, false, this.bucketTable.newCollector(0), this.scorers);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 213 */     if ((prohibitedScorers != null) && (prohibitedScorers.size() > 0)) {
/* 214 */       for (Scorer scorer : prohibitedScorers) {
/* 215 */         int mask = this.nextMask;
/* 216 */         this.nextMask <<= 1;
/* 217 */         this.prohibitedMask |= mask;
/* 218 */         if (scorer.nextDoc() != 2147483647) {
/* 219 */           this.scorers = new SubScorer(scorer, false, true, this.bucketTable.newCollector(mask), this.scorers);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 224 */     this.coordFactors = new float[optionalScorers.size() + 1];
/* 225 */     for (int i = 0; i < this.coordFactors.length; i++)
/* 226 */       this.coordFactors[i] = (disableCoord ? 1.0F : similarity.coord(i, maxCoord)); 
/*     */   }
/*     */   protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
/* 235 */     BucketScorer bs = new BucketScorer(this.weight);
/*     */ 
/* 237 */     collector.setScorer(bs);
/*     */     boolean more;
/*     */     do {
/* 239 */       this.bucketTable.first = null;
/*     */ 
/* 241 */       while (this.current != null)
/*     */       {
/* 244 */         if ((this.current.bits & this.prohibitedMask) == 0)
/*     */         {
/* 250 */           if (this.current.doc >= max) {
/* 251 */             Bucket tmp = this.current;
/* 252 */             this.current = this.current.next;
/* 253 */             tmp.next = this.bucketTable.first;
/* 254 */             this.bucketTable.first = tmp;
/* 255 */             continue;
/*     */           }
/*     */ 
/* 258 */           if (this.current.coord >= this.minNrShouldMatch) {
/* 259 */             bs.score = (this.current.score * this.coordFactors[this.current.coord]);
/* 260 */             bs.doc = this.current.doc;
/* 261 */             bs.freq = this.current.coord;
/* 262 */             collector.collect(this.current.doc);
/*     */           }
/*     */         }
/*     */ 
/* 266 */         this.current = this.current.next;
/*     */       }
/*     */ 
/* 269 */       if (this.bucketTable.first != null) {
/* 270 */         this.current = this.bucketTable.first;
/* 271 */         this.bucketTable.first = this.current.next;
/* 272 */         return true;
/*     */       }
/*     */ 
/* 276 */       more = false;
/* 277 */       this.end += 2048;
/* 278 */       for (SubScorer sub = this.scorers; sub != null; sub = sub.next) {
/* 279 */         int subScorerDocID = sub.scorer.docID();
/* 280 */         if (subScorerDocID != 2147483647) {
/* 281 */           more |= sub.scorer.score(sub.collector, this.end, subScorerDocID);
/*     */         }
/*     */       }
/* 284 */       this.current = this.bucketTable.first;
/*     */     }
/* 286 */     while ((this.current != null) || (more));
/*     */ 
/* 288 */     return false;
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 293 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/* 298 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException {
/*     */     boolean more;
/*     */     do {
/* 305 */       while (this.bucketTable.first != null) {
/* 306 */         this.current = this.bucketTable.first;
/* 307 */         this.bucketTable.first = this.current.next;
/*     */ 
/* 310 */         if (((this.current.bits & this.prohibitedMask) == 0) && (this.current.coord >= this.minNrShouldMatch))
/*     */         {
/* 314 */           return this.doc = this.current.doc;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 319 */       more = false;
/* 320 */       this.end += 2048;
/* 321 */       for (SubScorer sub = this.scorers; sub != null; sub = sub.next) {
/* 322 */         int subScorerDocID = sub.scorer.docID();
/* 323 */         if (subScorerDocID != 2147483647)
/* 324 */           more |= sub.scorer.score(sub.collector, this.end, subScorerDocID);
/*     */       }
/*     */     }
/* 327 */     while ((this.bucketTable.first != null) || (more));
/*     */ 
/* 329 */     return this.doc = 2147483647;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */   {
/* 334 */     return this.current.score * this.coordFactors[this.current.coord];
/*     */   }
/*     */ 
/*     */   public void score(Collector collector) throws IOException
/*     */   {
/* 339 */     score(collector, 2147483647, nextDoc());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 344 */     StringBuilder buffer = new StringBuilder();
/* 345 */     buffer.append("boolean(");
/* 346 */     for (SubScorer sub = this.scorers; sub != null; sub = sub.next) {
/* 347 */       buffer.append(sub.scorer.toString());
/* 348 */       buffer.append(" ");
/*     */     }
/* 350 */     buffer.append(")");
/* 351 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   protected void visitSubScorers(Query parent, BooleanClause.Occur relationship, Scorer.ScorerVisitor<Query, Query, Scorer> visitor)
/*     */   {
/* 356 */     super.visitSubScorers(parent, relationship, visitor);
/* 357 */     Query q = this.weight.getQuery();
/* 358 */     SubScorer sub = this.scorers;
/* 359 */     while (sub != null)
/*     */     {
/* 364 */       if (!sub.prohibited) {
/* 365 */         relationship = BooleanClause.Occur.SHOULD;
/*     */       }
/*     */       else
/*     */       {
/* 370 */         relationship = BooleanClause.Occur.MUST_NOT;
/*     */       }
/* 372 */       sub.scorer.visitSubScorers(q, relationship, visitor);
/* 373 */       sub = sub.next;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SubScorer
/*     */   {
/*     */     public Scorer scorer;
/* 169 */     public boolean prohibited = false;
/*     */     public Collector collector;
/*     */     public SubScorer next;
/*     */ 
/*     */     public SubScorer(Scorer scorer, boolean required, boolean prohibited, Collector collector, SubScorer next)
/*     */       throws IOException
/*     */     {
/* 176 */       if (required) {
/* 177 */         throw new IllegalArgumentException("this scorer cannot handle required=true");
/*     */       }
/* 179 */       this.scorer = scorer;
/*     */ 
/* 182 */       this.prohibited = prohibited;
/* 183 */       this.collector = collector;
/* 184 */       this.next = next;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class BucketTable
/*     */   {
/*     */     public static final int SIZE = 2048;
/*     */     public static final int MASK = 2047;
/* 153 */     final BooleanScorer.Bucket[] buckets = new BooleanScorer.Bucket[2048];
/* 154 */     BooleanScorer.Bucket first = null;
/*     */ 
/*     */     public Collector newCollector(int mask)
/*     */     {
/* 159 */       return new BooleanScorer.BooleanScorerCollector(mask, this);
/*     */     }
/*     */     public int size() {
/* 162 */       return 2048;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class Bucket
/*     */   {
/* 141 */     int doc = -1;
/*     */     float score;
/*     */     int bits;
/*     */     int coord;
/*     */     Bucket next;
/*     */   }
/*     */ 
/*     */   private static final class BucketScorer extends Scorer
/*     */   {
/*     */     float score;
/* 118 */     int doc = 2147483647;
/*     */     int freq;
/*     */ 
/*     */     public BucketScorer(Weight weight)
/*     */     {
/* 121 */       super();
/*     */     }
/*     */     public int advance(int target) throws IOException {
/* 124 */       return 2147483647;
/*     */     }
/*     */     public int docID() {
/* 127 */       return this.doc;
/*     */     }
/*     */     public float freq() {
/* 130 */       return this.freq;
/*     */     }
/*     */     public int nextDoc() throws IOException {
/* 133 */       return 2147483647;
/*     */     }
/*     */     public float score() throws IOException {
/* 136 */       return this.score;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class BooleanScorerCollector extends Collector
/*     */   {
/*     */     private BooleanScorer.BucketTable bucketTable;
/*     */     private int mask;
/*     */     private Scorer scorer;
/*     */ 
/*     */     public BooleanScorerCollector(int mask, BooleanScorer.BucketTable bucketTable)
/*     */     {
/*  67 */       this.mask = mask;
/*  68 */       this.bucketTable = bucketTable;
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/*  73 */       BooleanScorer.BucketTable table = this.bucketTable;
/*  74 */       int i = doc & 0x7FF;
/*  75 */       BooleanScorer.Bucket bucket = table.buckets[i];
/*  76 */       if (bucket == null)
/*     */       {
/*     */          tmp36_33 = new BooleanScorer.Bucket(); bucket = tmp36_33; table.buckets[i] = tmp36_33;
/*     */       }
/*  79 */       if (bucket.doc != doc) {
/*  80 */         bucket.doc = doc;
/*  81 */         bucket.score = this.scorer.score();
/*  82 */         bucket.bits = this.mask;
/*  83 */         bucket.coord = 1;
/*     */ 
/*  85 */         bucket.next = table.first;
/*  86 */         table.first = bucket;
/*     */       } else {
/*  88 */         bucket.score += this.scorer.score();
/*  89 */         bucket.bits |= this.mask;
/*  90 */         bucket.coord += 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setNextReader(IndexReader reader, int docBase)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 101 */       this.scorer = scorer;
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 106 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.BooleanScorer
 * JD-Core Version:    0.6.0
 */