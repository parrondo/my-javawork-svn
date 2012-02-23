/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.List;
/*     */ 
/*     */ final class MergeDocIDRemapper
/*     */ {
/*     */   int[] starts;
/*     */   int[] newStarts;
/*     */   int[][] docMaps;
/*     */   int minDocID;
/*     */   int maxDocID;
/*     */   int docShift;
/*     */ 
/*     */   public MergeDocIDRemapper(SegmentInfos infos, int[][] docMaps, int[] delCounts, MergePolicy.OneMerge merge, int mergedDocCount)
/*     */   {
/*  34 */     this.docMaps = docMaps;
/*  35 */     SegmentInfo firstSegment = (SegmentInfo)merge.segments.get(0);
/*  36 */     int i = 0;
/*     */     while (true) {
/*  38 */       SegmentInfo info = infos.info(i);
/*  39 */       if (info.equals(firstSegment))
/*     */         break;
/*  41 */       this.minDocID += info.docCount;
/*  42 */       i++;
/*     */     }
/*     */ 
/*  45 */     int numDocs = 0;
/*  46 */     for (int j = 0; j < docMaps.length; j++) {
/*  47 */       numDocs += infos.info(i).docCount;
/*  48 */       assert (infos.info(i).equals(merge.segments.get(j)));
/*     */ 
/*  46 */       i++;
/*     */     }
/*     */ 
/*  50 */     this.maxDocID = (this.minDocID + numDocs);
/*     */ 
/*  52 */     this.starts = new int[docMaps.length];
/*  53 */     this.newStarts = new int[docMaps.length];
/*     */ 
/*  55 */     this.starts[0] = this.minDocID;
/*  56 */     this.newStarts[0] = this.minDocID;
/*  57 */     for (i = 1; i < docMaps.length; i++) {
/*  58 */       int lastDocCount = ((SegmentInfo)merge.segments.get(i - 1)).docCount;
/*  59 */       this.starts[i] = (this.starts[(i - 1)] + lastDocCount);
/*  60 */       this.newStarts[i] = (this.newStarts[(i - 1)] + lastDocCount - delCounts[(i - 1)]);
/*     */     }
/*  62 */     this.docShift = (numDocs - mergedDocCount);
/*     */ 
/*  72 */     assert (this.docShift == this.maxDocID - (this.newStarts[(docMaps.length - 1)] + ((SegmentInfo)merge.segments.get(docMaps.length - 1)).docCount - delCounts[(docMaps.length - 1)]));
/*     */   }
/*     */ 
/*     */   public int remap(int oldDocID) {
/*  76 */     if (oldDocID < this.minDocID)
/*     */     {
/*  78 */       return oldDocID;
/*  79 */     }if (oldDocID >= this.maxDocID)
/*     */     {
/*  81 */       return oldDocID - this.docShift;
/*     */     }
/*     */ 
/*  84 */     int lo = 0;
/*  85 */     int hi = this.docMaps.length - 1;
/*     */ 
/*  87 */     while (hi >= lo) {
/*  88 */       int mid = lo + hi >>> 1;
/*  89 */       int midValue = this.starts[mid];
/*  90 */       if (oldDocID < midValue) {
/*  91 */         hi = mid - 1;
/*  92 */       } else if (oldDocID > midValue) {
/*  93 */         lo = mid + 1;
/*     */       } else {
/*  95 */         while ((mid + 1 < this.docMaps.length) && (this.starts[(mid + 1)] == midValue)) {
/*  96 */           mid++;
/*     */         }
/*  98 */         if (this.docMaps[mid] != null) {
/*  99 */           return this.newStarts[mid] + this.docMaps[mid][(oldDocID - this.starts[mid])];
/*     */         }
/* 101 */         return this.newStarts[mid] + oldDocID - this.starts[mid];
/*     */       }
/*     */     }
/* 104 */     if (this.docMaps[hi] != null) {
/* 105 */       return this.newStarts[hi] + this.docMaps[hi][(oldDocID - this.starts[hi])];
/*     */     }
/* 107 */     return this.newStarts[hi] + oldDocID - this.starts[hi];
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MergeDocIDRemapper
 * JD-Core Version:    0.6.0
 */