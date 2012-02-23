/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public class TopDocs
/*     */   implements Serializable
/*     */ {
/*     */   public int totalHits;
/*     */   public ScoreDoc[] scoreDocs;
/*     */   private float maxScore;
/*     */ 
/*     */   public float getMaxScore()
/*     */   {
/*  43 */     return this.maxScore;
/*     */   }
/*     */ 
/*     */   public void setMaxScore(float maxScore)
/*     */   {
/*  48 */     this.maxScore = maxScore;
/*     */   }
/*     */ 
/*     */   TopDocs(int totalHits, ScoreDoc[] scoreDocs)
/*     */   {
/*  53 */     this(totalHits, scoreDocs, (0.0F / 0.0F));
/*     */   }
/*     */ 
/*     */   public TopDocs(int totalHits, ScoreDoc[] scoreDocs, float maxScore) {
/*  57 */     this.totalHits = totalHits;
/*  58 */     this.scoreDocs = scoreDocs;
/*  59 */     this.maxScore = maxScore;
/*     */   }
/*     */ 
/*     */   public static TopDocs merge(Sort sort, int topN, TopDocs[] shardHits)
/*     */     throws IOException
/*     */   {
/*     */     PriorityQueue queue;
/*     */     PriorityQueue queue;
/* 208 */     if (sort == null)
/* 209 */       queue = new ScoreMergeSortQueue(shardHits);
/*     */     else {
/* 211 */       queue = new MergeSortQueue(sort, shardHits);
/*     */     }
/*     */ 
/* 214 */     int totalHitCount = 0;
/* 215 */     int availHitCount = 0;
/* 216 */     float maxScore = 1.4E-45F;
/* 217 */     for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++) {
/* 218 */       TopDocs shard = shardHits[shardIDX];
/* 219 */       if ((shard.scoreDocs != null) && (shard.scoreDocs.length > 0)) {
/* 220 */         totalHitCount += shard.totalHits;
/* 221 */         availHitCount += shard.scoreDocs.length;
/* 222 */         queue.add(new ShardRef(shardIDX));
/* 223 */         maxScore = Math.max(maxScore, shard.getMaxScore());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 228 */     ScoreDoc[] hits = new ScoreDoc[Math.min(topN, availHitCount)];
/*     */ 
/* 230 */     int hitUpto = 0;
/* 231 */     while (hitUpto < hits.length) {
/* 232 */       assert (queue.size() > 0);
/* 233 */       ShardRef ref = (ShardRef)queue.pop();
/* 234 */       ScoreDoc hit = shardHits[ref.shardIndex].scoreDocs[(ref.hitIndex++)];
/* 235 */       if (sort == null)
/* 236 */         hits[hitUpto] = new ScoreDoc(hit.doc, hit.score, ref.shardIndex);
/*     */       else {
/* 238 */         hits[hitUpto] = new FieldDoc(hit.doc, hit.score, ((FieldDoc)hit).fields, ref.shardIndex);
/*     */       }
/*     */ 
/* 244 */       hitUpto++;
/*     */ 
/* 246 */       if (ref.hitIndex < shardHits[ref.shardIndex].scoreDocs.length)
/*     */       {
/* 248 */         queue.add(ref);
/*     */       }
/*     */     }
/*     */ 
/* 252 */     if (sort == null) {
/* 253 */       return new TopDocs(totalHitCount, hits, maxScore);
/*     */     }
/* 255 */     return new TopFieldDocs(totalHitCount, hits, sort.getSort(), maxScore);
/*     */   }
/*     */ 
/*     */   private static class MergeSortQueue extends PriorityQueue<TopDocs.ShardRef>
/*     */   {
/*     */     final ScoreDoc[][] shardHits;
/*     */     final FieldComparator[] comparators;
/*     */     final int[] reverseMul;
/*     */ 
/*     */     public MergeSortQueue(Sort sort, TopDocs[] shardHits)
/*     */       throws IOException
/*     */     {
/* 126 */       initialize(shardHits.length);
/* 127 */       this.shardHits = new ScoreDoc[shardHits.length][];
/* 128 */       for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++) {
/* 129 */         ScoreDoc[] shard = shardHits[shardIDX].scoreDocs;
/*     */ 
/* 131 */         if (shard != null) {
/* 132 */           this.shardHits[shardIDX] = shard;
/*     */ 
/* 134 */           for (int hitIDX = 0; hitIDX < shard.length; hitIDX++) {
/* 135 */             ScoreDoc sd = shard[hitIDX];
/* 136 */             if (!(sd instanceof FieldDoc)) {
/* 137 */               throw new IllegalArgumentException("shard " + shardIDX + " was not sorted by the provided Sort (expected FieldDoc but got ScoreDoc)");
/*     */             }
/* 139 */             FieldDoc fd = (FieldDoc)sd;
/* 140 */             if (fd.fields == null) {
/* 141 */               throw new IllegalArgumentException("shard " + shardIDX + " did not set sort field values (FieldDoc.fields is null); you must pass fillFields=true to IndexSearcher.search on each shard");
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 147 */       SortField[] sortFields = sort.getSort();
/* 148 */       this.comparators = new FieldComparator[sortFields.length];
/* 149 */       this.reverseMul = new int[sortFields.length];
/* 150 */       for (int compIDX = 0; compIDX < sortFields.length; compIDX++) {
/* 151 */         SortField sortField = sortFields[compIDX];
/* 152 */         this.comparators[compIDX] = sortField.getComparator(1, compIDX);
/* 153 */         this.reverseMul[compIDX] = (sortField.getReverse() ? -1 : 1);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean lessThan(TopDocs.ShardRef first, TopDocs.ShardRef second)
/*     */     {
/* 160 */       assert (first != second);
/* 161 */       FieldDoc firstFD = (FieldDoc)this.shardHits[first.shardIndex][first.hitIndex];
/* 162 */       FieldDoc secondFD = (FieldDoc)this.shardHits[second.shardIndex][second.hitIndex];
/*     */ 
/* 165 */       for (int compIDX = 0; compIDX < this.comparators.length; compIDX++) {
/* 166 */         FieldComparator comp = this.comparators[compIDX];
/*     */ 
/* 169 */         int cmp = this.reverseMul[compIDX] * comp.compareValues(firstFD.fields[compIDX], secondFD.fields[compIDX]);
/*     */ 
/* 171 */         if (cmp != 0)
/*     */         {
/* 173 */           return cmp < 0;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 178 */       if (first.shardIndex < second.shardIndex)
/*     */       {
/* 180 */         return true;
/* 181 */       }if (first.shardIndex > second.shardIndex)
/*     */       {
/* 183 */         return false;
/*     */       }
/*     */ 
/* 188 */       assert (first.hitIndex != second.hitIndex);
/* 189 */       return first.hitIndex < second.hitIndex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ScoreMergeSortQueue extends PriorityQueue<TopDocs.ShardRef>
/*     */   {
/*     */     final ScoreDoc[][] shardHits;
/*     */ 
/*     */     public ScoreMergeSortQueue(TopDocs[] shardHits)
/*     */     {
/*  86 */       initialize(shardHits.length);
/*  87 */       this.shardHits = new ScoreDoc[shardHits.length][];
/*  88 */       for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++)
/*  89 */         this.shardHits[shardIDX] = shardHits[shardIDX].scoreDocs;
/*     */     }
/*     */ 
/*     */     public boolean lessThan(TopDocs.ShardRef first, TopDocs.ShardRef second)
/*     */     {
/*  95 */       assert (first != second);
/*  96 */       float firstScore = this.shardHits[first.shardIndex][first.hitIndex].score;
/*  97 */       float secondScore = this.shardHits[second.shardIndex][second.hitIndex].score;
/*     */ 
/*  99 */       if (firstScore < secondScore)
/* 100 */         return false;
/* 101 */       if (firstScore > secondScore) {
/* 102 */         return true;
/*     */       }
/*     */ 
/* 105 */       if (first.shardIndex < second.shardIndex)
/* 106 */         return true;
/* 107 */       if (first.shardIndex > second.shardIndex) {
/* 108 */         return false;
/*     */       }
/*     */ 
/* 112 */       assert (first.hitIndex != second.hitIndex);
/* 113 */       return first.hitIndex < second.hitIndex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ShardRef
/*     */   {
/*     */     final int shardIndex;
/*     */     int hitIndex;
/*     */ 
/*     */     public ShardRef(int shardIndex)
/*     */     {
/*  71 */       this.shardIndex = shardIndex;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  76 */       return "ShardRef(shardIndex=" + this.shardIndex + " hitIndex=" + this.hitIndex + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopDocs
 * JD-Core Version:    0.6.0
 */