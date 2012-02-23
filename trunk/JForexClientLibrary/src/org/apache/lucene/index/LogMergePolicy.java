/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.util.SetOnce;
/*     */ 
/*     */ public abstract class LogMergePolicy extends MergePolicy
/*     */ {
/*     */   public static final double LEVEL_LOG_SPAN = 0.75D;
/*     */   public static final int DEFAULT_MERGE_FACTOR = 10;
/*     */   public static final int DEFAULT_MAX_MERGE_DOCS = 2147483647;
/*     */   public static final double DEFAULT_NO_CFS_RATIO = 0.1D;
/*  65 */   protected int mergeFactor = 10;
/*     */   protected long minMergeSize;
/*     */   protected long maxMergeSize;
/*  71 */   protected long maxMergeSizeForOptimize = 9223372036854775807L;
/*  72 */   protected int maxMergeDocs = 2147483647;
/*     */ 
/*  74 */   protected double noCFSRatio = 0.1D;
/*     */ 
/*  76 */   protected boolean calibrateSizeByDeletes = true;
/*     */ 
/*  78 */   protected boolean useCompoundFile = true;
/*     */ 
/*     */   protected boolean verbose()
/*     */   {
/*  85 */     IndexWriter w = (IndexWriter)this.writer.get();
/*  86 */     return (w != null) && (w.verbose());
/*     */   }
/*     */ 
/*     */   public double getNoCFSRatio()
/*     */   {
/*  91 */     return this.noCFSRatio;
/*     */   }
/*     */ 
/*     */   public void setNoCFSRatio(double noCFSRatio)
/*     */   {
/* 100 */     if ((noCFSRatio < 0.0D) || (noCFSRatio > 1.0D)) {
/* 101 */       throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
/*     */     }
/* 103 */     this.noCFSRatio = noCFSRatio;
/*     */   }
/*     */ 
/*     */   protected void message(String message) {
/* 107 */     if (verbose())
/* 108 */       ((IndexWriter)this.writer.get()).message("LMP: " + message);
/*     */   }
/*     */ 
/*     */   public int getMergeFactor()
/*     */   {
/* 115 */     return this.mergeFactor;
/*     */   }
/*     */ 
/*     */   public void setMergeFactor(int mergeFactor)
/*     */   {
/* 128 */     if (mergeFactor < 2)
/* 129 */       throw new IllegalArgumentException("mergeFactor cannot be less than 2");
/* 130 */     this.mergeFactor = mergeFactor;
/*     */   }
/*     */ 
/*     */   public boolean useCompoundFile(SegmentInfos infos, SegmentInfo mergedInfo)
/*     */     throws IOException
/*     */   {
/*     */     boolean doCFS;
/*     */     boolean doCFS;
/* 138 */     if (!this.useCompoundFile) {
/* 139 */       doCFS = false;
/*     */     }
/*     */     else
/*     */     {
/*     */       boolean doCFS;
/* 140 */       if (this.noCFSRatio == 1.0D) {
/* 141 */         doCFS = true;
/*     */       } else {
/* 143 */         long totalSize = 0L;
/* 144 */         for (SegmentInfo info : infos) {
/* 145 */           totalSize += size(info);
/*     */         }
/* 147 */         doCFS = size(mergedInfo) <= this.noCFSRatio * totalSize;
/*     */       }
/*     */     }
/* 149 */     return doCFS;
/*     */   }
/*     */ 
/*     */   public void setUseCompoundFile(boolean useCompoundFile)
/*     */   {
/* 155 */     this.useCompoundFile = useCompoundFile;
/*     */   }
/*     */ 
/*     */   public boolean getUseCompoundFile()
/*     */   {
/* 162 */     return this.useCompoundFile;
/*     */   }
/*     */ 
/*     */   public void setCalibrateSizeByDeletes(boolean calibrateSizeByDeletes)
/*     */   {
/* 168 */     this.calibrateSizeByDeletes = calibrateSizeByDeletes;
/*     */   }
/*     */ 
/*     */   public boolean getCalibrateSizeByDeletes()
/*     */   {
/* 174 */     return this.calibrateSizeByDeletes;
/*     */   }
/*     */   public void close() {
/*     */   }
/*     */ 
/*     */   protected abstract long size(SegmentInfo paramSegmentInfo) throws IOException;
/*     */ 
/*     */   protected long sizeDocs(SegmentInfo info) throws IOException {
/* 183 */     if (this.calibrateSizeByDeletes) {
/* 184 */       int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
/* 185 */       assert (delCount <= info.docCount);
/* 186 */       return info.docCount - delCount;
/*     */     }
/* 188 */     return info.docCount;
/*     */   }
/*     */ 
/*     */   protected long sizeBytes(SegmentInfo info) throws IOException
/*     */   {
/* 193 */     long byteSize = info.sizeInBytes(true);
/* 194 */     if (this.calibrateSizeByDeletes) {
/* 195 */       int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
/* 196 */       double delRatio = info.docCount <= 0 ? 0.0F : delCount / info.docCount;
/* 197 */       assert (delRatio <= 1.0D);
/* 198 */       return info.docCount <= 0 ? byteSize : ()(byteSize * (1.0D - delRatio));
/*     */     }
/* 200 */     return byteSize;
/*     */   }
/*     */ 
/*     */   protected boolean isOptimized(SegmentInfos infos, int maxNumSegments, Map<SegmentInfo, Boolean> segmentsToOptimize) throws IOException
/*     */   {
/* 205 */     int numSegments = infos.size();
/* 206 */     int numToOptimize = 0;
/* 207 */     SegmentInfo optimizeInfo = null;
/* 208 */     boolean segmentIsOriginal = false;
/* 209 */     for (int i = 0; (i < numSegments) && (numToOptimize <= maxNumSegments); i++) {
/* 210 */       SegmentInfo info = infos.info(i);
/* 211 */       Boolean isOriginal = (Boolean)segmentsToOptimize.get(info);
/* 212 */       if (isOriginal != null) {
/* 213 */         segmentIsOriginal = isOriginal.booleanValue();
/* 214 */         numToOptimize++;
/* 215 */         optimizeInfo = info;
/*     */       }
/*     */     }
/*     */ 
/* 219 */     return (numToOptimize <= maxNumSegments) && ((numToOptimize != 1) || (!segmentIsOriginal) || (isOptimized(optimizeInfo)));
/*     */   }
/*     */ 
/*     */   protected boolean isOptimized(SegmentInfo info)
/*     */     throws IOException
/*     */   {
/* 228 */     IndexWriter w = (IndexWriter)this.writer.get();
/* 229 */     assert (w != null);
/* 230 */     boolean hasDeletions = w.numDeletedDocs(info) > 0;
/* 231 */     return (!hasDeletions) && (!info.hasSeparateNorms()) && (info.dir == w.getDirectory()) && ((info.getUseCompoundFile() == this.useCompoundFile) || (this.noCFSRatio < 1.0D));
/*     */   }
/*     */ 
/*     */   private MergePolicy.MergeSpecification findMergesForOptimizeSizeLimit(SegmentInfos infos, int maxNumSegments, int last)
/*     */     throws IOException
/*     */   {
/* 247 */     MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
/* 248 */     List segments = infos.asList();
/*     */ 
/* 250 */     int start = last - 1;
/* 251 */     while (start >= 0) {
/* 252 */       SegmentInfo info = infos.info(start);
/* 253 */       if ((size(info) > this.maxMergeSizeForOptimize) || (sizeDocs(info) > this.maxMergeDocs)) {
/* 254 */         if (verbose()) {
/* 255 */           message("optimize: skip segment=" + info + ": size is > maxMergeSize (" + this.maxMergeSizeForOptimize + ") or sizeDocs is > maxMergeDocs (" + this.maxMergeDocs + ")");
/*     */         }
/*     */ 
/* 259 */         if ((last - start - 1 > 1) || ((start != last - 1) && (!isOptimized(infos.info(start + 1)))))
/*     */         {
/* 261 */           spec.add(new MergePolicy.OneMerge(segments.subList(start + 1, last)));
/*     */         }
/* 263 */         last = start;
/* 264 */       } else if (last - start == this.mergeFactor)
/*     */       {
/* 266 */         spec.add(new MergePolicy.OneMerge(segments.subList(start, last)));
/* 267 */         last = start;
/*     */       }
/* 269 */       start--;
/*     */     }
/*     */ 
/* 273 */     if (last > 0) { start++; if ((start + 1 < last) || (!isOptimized(infos.info(start)))) {
/* 274 */         spec.add(new MergePolicy.OneMerge(segments.subList(start, last)));
/*     */       }
/*     */     }
/* 277 */     return spec.merges.size() == 0 ? null : spec;
/*     */   }
/*     */ 
/*     */   private MergePolicy.MergeSpecification findMergesForOptimizeMaxNumSegments(SegmentInfos infos, int maxNumSegments, int last)
/*     */     throws IOException
/*     */   {
/* 286 */     MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
/* 287 */     List segments = infos.asList();
/*     */ 
/* 291 */     while (last - maxNumSegments + 1 >= this.mergeFactor) {
/* 292 */       spec.add(new MergePolicy.OneMerge(segments.subList(last - this.mergeFactor, last)));
/* 293 */       last -= this.mergeFactor;
/*     */     }
/*     */ 
/* 298 */     if (0 == spec.merges.size()) {
/* 299 */       if (maxNumSegments == 1)
/*     */       {
/* 303 */         if ((last > 1) || (!isOptimized(infos.info(0))))
/* 304 */           spec.add(new MergePolicy.OneMerge(segments.subList(0, last)));
/*     */       }
/* 306 */       else if (last > maxNumSegments)
/*     */       {
/* 317 */         int finalMergeSize = last - maxNumSegments + 1;
/*     */ 
/* 320 */         long bestSize = 0L;
/* 321 */         int bestStart = 0;
/*     */ 
/* 323 */         for (int i = 0; i < last - finalMergeSize + 1; i++) {
/* 324 */           long sumSize = 0L;
/* 325 */           for (int j = 0; j < finalMergeSize; j++)
/* 326 */             sumSize += size(infos.info(j + i));
/* 327 */           if ((i == 0) || ((sumSize < 2L * size(infos.info(i - 1))) && (sumSize < bestSize))) {
/* 328 */             bestStart = i;
/* 329 */             bestSize = sumSize;
/*     */           }
/*     */         }
/*     */ 
/* 333 */         spec.add(new MergePolicy.OneMerge(segments.subList(bestStart, bestStart + finalMergeSize)));
/*     */       }
/*     */     }
/* 336 */     return spec.merges.size() == 0 ? null : spec;
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesForOptimize(SegmentInfos infos, int maxNumSegments, Map<SegmentInfo, Boolean> segmentsToOptimize)
/*     */     throws IOException
/*     */   {
/* 354 */     assert (maxNumSegments > 0);
/* 355 */     if (verbose()) {
/* 356 */       message("findMergesForOptimize: maxNumSegs=" + maxNumSegments + " segsToOptimize=" + segmentsToOptimize);
/*     */     }
/*     */ 
/* 361 */     if (isOptimized(infos, maxNumSegments, segmentsToOptimize)) return null;
/*     */ 
/* 366 */     int last = infos.size();
/* 367 */     while (last > 0) {
/* 368 */       last--; SegmentInfo info = infos.info(last);
/* 369 */       if (segmentsToOptimize.get(info) != null) {
/* 370 */         last++;
/* 371 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 375 */     if (last == 0) return null;
/*     */ 
/* 378 */     if ((maxNumSegments == 1) && (last == 1) && (isOptimized(infos.info(0)))) return null;
/*     */ 
/* 381 */     boolean anyTooLarge = false;
/* 382 */     for (int i = 0; i < last; i++) {
/* 383 */       SegmentInfo info = infos.info(i);
/* 384 */       if ((size(info) > this.maxMergeSizeForOptimize) || (sizeDocs(info) > this.maxMergeDocs)) {
/* 385 */         anyTooLarge = true;
/* 386 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 390 */     if (anyTooLarge) {
/* 391 */       return findMergesForOptimizeSizeLimit(infos, maxNumSegments, last);
/*     */     }
/* 393 */     return findMergesForOptimizeMaxNumSegments(infos, maxNumSegments, last);
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesToExpungeDeletes(SegmentInfos segmentInfos)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 405 */     List segments = segmentInfos.asList();
/* 406 */     int numSegments = segments.size();
/*     */ 
/* 408 */     if (verbose()) {
/* 409 */       message("findMergesToExpungeDeletes: " + numSegments + " segments");
/*     */     }
/* 411 */     MergePolicy.MergeSpecification spec = new MergePolicy.MergeSpecification();
/* 412 */     int firstSegmentWithDeletions = -1;
/* 413 */     IndexWriter w = (IndexWriter)this.writer.get();
/* 414 */     assert (w != null);
/* 415 */     for (int i = 0; i < numSegments; i++) {
/* 416 */       SegmentInfo info = segmentInfos.info(i);
/* 417 */       int delCount = w.numDeletedDocs(info);
/* 418 */       if (delCount > 0) {
/* 419 */         if (verbose())
/* 420 */           message("  segment " + info.name + " has deletions");
/* 421 */         if (firstSegmentWithDeletions == -1) {
/* 422 */           firstSegmentWithDeletions = i; } else {
/* 423 */           if (i - firstSegmentWithDeletions != this.mergeFactor) {
/*     */             continue;
/*     */           }
/* 426 */           if (verbose())
/* 427 */             message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive");
/* 428 */           spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, i)));
/* 429 */           firstSegmentWithDeletions = i;
/*     */         }
/*     */       } else {
/* 431 */         if (firstSegmentWithDeletions == -1)
/*     */         {
/*     */           continue;
/*     */         }
/* 435 */         if (verbose())
/* 436 */           message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive");
/* 437 */         spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, i)));
/* 438 */         firstSegmentWithDeletions = -1;
/*     */       }
/*     */     }
/*     */ 
/* 442 */     if (firstSegmentWithDeletions != -1) {
/* 443 */       if (verbose())
/* 444 */         message("  add merge " + firstSegmentWithDeletions + " to " + (numSegments - 1) + " inclusive");
/* 445 */       spec.add(new MergePolicy.OneMerge(segments.subList(firstSegmentWithDeletions, numSegments)));
/*     */     }
/*     */ 
/* 448 */     return spec;
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMerges(SegmentInfos infos)
/*     */     throws IOException
/*     */   {
/* 483 */     int numSegments = infos.size();
/* 484 */     if (verbose()) {
/* 485 */       message("findMerges: " + numSegments + " segments");
/*     */     }
/*     */ 
/* 489 */     List levels = new ArrayList();
/* 490 */     float norm = (float)Math.log(this.mergeFactor);
/*     */ 
/* 492 */     Collection mergingSegments = ((IndexWriter)this.writer.get()).getMergingSegments();
/*     */ 
/* 494 */     for (int i = 0; i < numSegments; i++) {
/* 495 */       SegmentInfo info = infos.info(i);
/* 496 */       long size = size(info);
/*     */ 
/* 499 */       if (size < 1L) {
/* 500 */         size = 1L;
/*     */       }
/*     */ 
/* 503 */       SegmentInfoAndLevel infoLevel = new SegmentInfoAndLevel(info, (float)Math.log(size) / norm, i);
/* 504 */       levels.add(infoLevel);
/*     */ 
/* 506 */       if (verbose()) {
/* 507 */         long segBytes = sizeBytes(info);
/* 508 */         String extra = mergingSegments.contains(info) ? " [merging]" : "";
/* 509 */         if (size >= this.maxMergeSize) {
/* 510 */           extra = extra + " [skip: too large]";
/*     */         }
/* 512 */         message("seg=" + ((IndexWriter)this.writer.get()).segString(info) + " level=" + infoLevel.level + " size=" + String.format("%.3f MB", new Object[] { Double.valueOf(segBytes / 1024L / 1024.0D) }) + extra);
/*     */       }
/*     */     }
/*     */     float levelFloor;
/*     */     float levelFloor;
/* 517 */     if (this.minMergeSize <= 0L)
/* 518 */       levelFloor = 0.0F;
/*     */     else {
/* 520 */       levelFloor = (float)(Math.log(this.minMergeSize) / norm);
/*     */     }
/*     */ 
/* 529 */     MergePolicy.MergeSpecification spec = null;
/*     */ 
/* 531 */     int numMergeableSegments = levels.size();
/*     */ 
/* 533 */     int start = 0;
/* 534 */     while (start < numMergeableSegments)
/*     */     {
/* 538 */       float maxLevel = ((SegmentInfoAndLevel)levels.get(start)).level;
/* 539 */       for (int i = 1 + start; i < numMergeableSegments; i++) {
/* 540 */         float level = ((SegmentInfoAndLevel)levels.get(i)).level;
/* 541 */         if (level > maxLevel)
/* 542 */           maxLevel = level;
/*     */       }
/*     */       float levelBottom;
/*     */       float levelBottom;
/* 548 */       if (maxLevel <= levelFloor)
/*     */       {
/* 550 */         levelBottom = -1.0F;
/*     */       } else {
/* 552 */         levelBottom = (float)(maxLevel - 0.75D);
/*     */ 
/* 555 */         if ((levelBottom < levelFloor) && (maxLevel >= levelFloor)) {
/* 556 */           levelBottom = levelFloor;
/*     */         }
/*     */       }
/* 559 */       int upto = numMergeableSegments - 1;
/* 560 */       while ((upto >= start) && 
/* 561 */         (((SegmentInfoAndLevel)levels.get(upto)).level < levelBottom))
/*     */       {
/* 564 */         upto--;
/*     */       }
/* 566 */       if (verbose()) {
/* 567 */         message("  level " + levelBottom + " to " + maxLevel + ": " + (1 + upto - start) + " segments");
/*     */       }
/*     */ 
/* 570 */       int end = start + this.mergeFactor;
/* 571 */       while (end <= 1 + upto) {
/* 572 */         boolean anyTooLarge = false;
/* 573 */         boolean anyMerging = false;
/* 574 */         for (int i = start; i < end; i++) {
/* 575 */           SegmentInfo info = ((SegmentInfoAndLevel)levels.get(i)).info;
/* 576 */           anyTooLarge |= ((size(info) >= this.maxMergeSize) || (sizeDocs(info) >= this.maxMergeDocs));
/* 577 */           if (mergingSegments.contains(info)) {
/* 578 */             anyMerging = true;
/* 579 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 583 */         if (!anyMerging)
/*     */         {
/* 585 */           if (!anyTooLarge) {
/* 586 */             if (spec == null)
/* 587 */               spec = new MergePolicy.MergeSpecification();
/* 588 */             List mergeInfos = new ArrayList();
/* 589 */             for (int i = start; i < end; i++) {
/* 590 */               mergeInfos.add(((SegmentInfoAndLevel)levels.get(i)).info);
/* 591 */               assert (infos.contains(((SegmentInfoAndLevel)levels.get(i)).info));
/*     */             }
/* 593 */             if (verbose()) {
/* 594 */               message("  add merge=" + ((IndexWriter)this.writer.get()).segString(mergeInfos) + " start=" + start + " end=" + end);
/*     */             }
/* 596 */             spec.add(new MergePolicy.OneMerge(mergeInfos));
/* 597 */           } else if (verbose()) {
/* 598 */             message("    " + start + " to " + end + ": contains segment over maxMergeSize or maxMergeDocs; skipping");
/*     */           }
/*     */         }
/* 601 */         start = end;
/* 602 */         end = start + this.mergeFactor;
/*     */       }
/*     */ 
/* 605 */       start = 1 + upto;
/*     */     }
/*     */ 
/* 608 */     return spec;
/*     */   }
/*     */ 
/*     */   public void setMaxMergeDocs(int maxMergeDocs)
/*     */   {
/* 627 */     this.maxMergeDocs = maxMergeDocs;
/*     */   }
/*     */ 
/*     */   public int getMaxMergeDocs()
/*     */   {
/* 634 */     return this.maxMergeDocs;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 639 */     StringBuilder sb = new StringBuilder("[" + getClass().getSimpleName() + ": ");
/* 640 */     sb.append("minMergeSize=").append(this.minMergeSize).append(", ");
/* 641 */     sb.append("mergeFactor=").append(this.mergeFactor).append(", ");
/* 642 */     sb.append("maxMergeSize=").append(this.maxMergeSize).append(", ");
/* 643 */     sb.append("maxMergeSizeForOptimize=").append(this.maxMergeSizeForOptimize).append(", ");
/* 644 */     sb.append("calibrateSizeByDeletes=").append(this.calibrateSizeByDeletes).append(", ");
/* 645 */     sb.append("maxMergeDocs=").append(this.maxMergeDocs).append(", ");
/* 646 */     sb.append("useCompoundFile=").append(this.useCompoundFile).append(", ");
/* 647 */     sb.append("noCFSRatio=").append(this.noCFSRatio);
/* 648 */     sb.append("]");
/* 649 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private static class SegmentInfoAndLevel
/*     */     implements Comparable<SegmentInfoAndLevel>
/*     */   {
/*     */     SegmentInfo info;
/*     */     float level;
/*     */     int index;
/*     */ 
/*     */     public SegmentInfoAndLevel(SegmentInfo info, float level, int index)
/*     */     {
/* 457 */       this.info = info;
/* 458 */       this.level = level;
/* 459 */       this.index = index;
/*     */     }
/*     */ 
/*     */     public int compareTo(SegmentInfoAndLevel other)
/*     */     {
/* 464 */       if (this.level < other.level)
/* 465 */         return 1;
/* 466 */       if (this.level > other.level) {
/* 467 */         return -1;
/*     */       }
/* 469 */       return 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.LogMergePolicy
 * JD-Core Version:    0.6.0
 */