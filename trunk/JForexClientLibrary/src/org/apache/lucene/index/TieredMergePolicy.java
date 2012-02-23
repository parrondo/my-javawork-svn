/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.lucene.util.SetOnce;
/*     */ 
/*     */ public class TieredMergePolicy extends MergePolicy
/*     */ {
/*     */   private int maxMergeAtOnce;
/*     */   private long maxMergedSegmentBytes;
/*     */   private int maxMergeAtOnceExplicit;
/*     */   private long floorSegmentBytes;
/*     */   private double segsPerTier;
/*     */   private double expungeDeletesPctAllowed;
/*     */   private boolean useCompoundFile;
/*     */   private double noCFSRatio;
/*     */   private double reclaimDeletesWeight;
/*     */   private final Comparator<SegmentInfo> segmentByteSizeDescending;
/*     */ 
/*     */   public TieredMergePolicy()
/*     */   {
/*  76 */     this.maxMergeAtOnce = 10;
/*  77 */     this.maxMergedSegmentBytes = 5368709120L;
/*  78 */     this.maxMergeAtOnceExplicit = 30;
/*     */ 
/*  80 */     this.floorSegmentBytes = 2097152L;
/*  81 */     this.segsPerTier = 10.0D;
/*  82 */     this.expungeDeletesPctAllowed = 10.0D;
/*  83 */     this.useCompoundFile = true;
/*  84 */     this.noCFSRatio = 0.1D;
/*  85 */     this.reclaimDeletesWeight = 2.0D;
/*     */ 
/* 258 */     this.segmentByteSizeDescending = new SegmentByteSizeDescending(null);
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setMaxMergeAtOnce(int v)
/*     */   {
/*  92 */     if (v < 2) {
/*  93 */       throw new IllegalArgumentException("maxMergeAtOnce must be > 1 (got " + v + ")");
/*     */     }
/*  95 */     this.maxMergeAtOnce = v;
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxMergeAtOnce()
/*     */   {
/* 101 */     return this.maxMergeAtOnce;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setMaxMergeAtOnceExplicit(int v)
/*     */   {
/* 110 */     if (v < 2) {
/* 111 */       throw new IllegalArgumentException("maxMergeAtOnceExplicit must be > 1 (got " + v + ")");
/*     */     }
/* 113 */     this.maxMergeAtOnceExplicit = v;
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxMergeAtOnceExplicit()
/*     */   {
/* 119 */     return this.maxMergeAtOnceExplicit;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setMaxMergedSegmentMB(double v)
/*     */   {
/* 128 */     this.maxMergedSegmentBytes = ()(v * 1024.0D * 1024.0D);
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   public double getMaxMergedSegmentMB()
/*     */   {
/* 134 */     return this.maxMergedSegmentBytes / 1024L / 1024.0D;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setReclaimDeletesWeight(double v)
/*     */   {
/* 142 */     if (v < 0.0D) {
/* 143 */       throw new IllegalArgumentException("reclaimDeletesWeight must be >= 0.0 (got " + v + ")");
/*     */     }
/* 145 */     this.reclaimDeletesWeight = v;
/* 146 */     return this;
/*     */   }
/*     */ 
/*     */   public double getReclaimDeletesWeight()
/*     */   {
/* 151 */     return this.reclaimDeletesWeight;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setFloorSegmentMB(double v)
/*     */   {
/* 160 */     if (v <= 0.0D) {
/* 161 */       throw new IllegalArgumentException("floorSegmentMB must be >= 0.0 (got " + v + ")");
/*     */     }
/* 163 */     this.floorSegmentBytes = ()(v * 1024.0D * 1024.0D);
/* 164 */     return this;
/*     */   }
/*     */ 
/*     */   public double getFloorSegmentMB()
/*     */   {
/* 169 */     return this.floorSegmentBytes / 1024L * 1024.0D;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setExpungeDeletesPctAllowed(double v)
/*     */   {
/* 176 */     if ((v < 0.0D) || (v > 100.0D)) {
/* 177 */       throw new IllegalArgumentException("expungeDeletesPctAllowed must be between 0.0 and 100.0 inclusive (got " + v + ")");
/*     */     }
/* 179 */     this.expungeDeletesPctAllowed = v;
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */   public double getExpungeDeletesPctAllowed()
/*     */   {
/* 185 */     return this.expungeDeletesPctAllowed;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setSegmentsPerTier(double v)
/*     */   {
/* 197 */     if (v < 2.0D) {
/* 198 */       throw new IllegalArgumentException("segmentsPerTier must be >= 2.0 (got " + v + ")");
/*     */     }
/* 200 */     this.segsPerTier = v;
/* 201 */     return this;
/*     */   }
/*     */ 
/*     */   public double getSegmentsPerTier()
/*     */   {
/* 206 */     return this.segsPerTier;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setUseCompoundFile(boolean useCompoundFile)
/*     */   {
/* 213 */     this.useCompoundFile = useCompoundFile;
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean getUseCompoundFile()
/*     */   {
/* 219 */     return this.useCompoundFile;
/*     */   }
/*     */ 
/*     */   public TieredMergePolicy setNoCFSRatio(double noCFSRatio)
/*     */   {
/* 228 */     if ((noCFSRatio < 0.0D) || (noCFSRatio > 1.0D)) {
/* 229 */       throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
/*     */     }
/* 231 */     this.noCFSRatio = noCFSRatio;
/* 232 */     return this;
/*     */   }
/*     */ 
/*     */   public double getNoCFSRatio()
/*     */   {
/* 237 */     return this.noCFSRatio;
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMerges(SegmentInfos infos)
/*     */     throws IOException
/*     */   {
/* 267 */     if (verbose()) {
/* 268 */       message("findMerges: " + infos.size() + " segments");
/*     */     }
/* 270 */     if (infos.size() == 0) {
/* 271 */       return null;
/*     */     }
/* 273 */     Collection merging = ((IndexWriter)this.writer.get()).getMergingSegments();
/* 274 */     Collection toBeMerged = new HashSet();
/*     */ 
/* 276 */     List infosSorted = new ArrayList(infos.asList());
/* 277 */     Collections.sort(infosSorted, this.segmentByteSizeDescending);
/*     */ 
/* 280 */     long totIndexBytes = 0L;
/* 281 */     long minSegmentBytes = 9223372036854775807L;
/* 282 */     for (SegmentInfo info : infosSorted) {
/* 283 */       long segBytes = size(info);
/* 284 */       if (verbose()) {
/* 285 */         String extra = merging.contains(info) ? " [merging]" : "";
/* 286 */         if (segBytes >= this.maxMergedSegmentBytes / 2.0D)
/* 287 */           extra = extra + " [skip: too large]";
/* 288 */         else if (segBytes < this.floorSegmentBytes) {
/* 289 */           extra = extra + " [floored]";
/*     */         }
/* 291 */         message("  seg=" + ((IndexWriter)this.writer.get()).segString(info) + " size=" + String.format("%.3f", new Object[] { Double.valueOf(segBytes / 1024L / 1024.0D) }) + " MB" + extra);
/*     */       }
/*     */ 
/* 294 */       minSegmentBytes = Math.min(segBytes, minSegmentBytes);
/*     */ 
/* 296 */       totIndexBytes += segBytes;
/*     */     }
/*     */ 
/* 301 */     int tooBigCount = 0;
/* 302 */     while ((tooBigCount < infosSorted.size()) && (size((SegmentInfo)infosSorted.get(tooBigCount)) >= this.maxMergedSegmentBytes / 2.0D)) {
/* 303 */       totIndexBytes -= size((SegmentInfo)infosSorted.get(tooBigCount));
/* 304 */       tooBigCount++;
/*     */     }
/*     */ 
/* 307 */     minSegmentBytes = floorSize(minSegmentBytes);
/*     */ 
/* 310 */     long levelSize = minSegmentBytes;
/* 311 */     long bytesLeft = totIndexBytes;
/* 312 */     double allowedSegCount = 0.0D;
/*     */     while (true) {
/* 314 */       double segCountLevel = bytesLeft / levelSize;
/* 315 */       if (segCountLevel < this.segsPerTier) {
/* 316 */         allowedSegCount += Math.ceil(segCountLevel);
/* 317 */         break;
/*     */       }
/* 319 */       allowedSegCount += this.segsPerTier;
/* 320 */       bytesLeft = ()(bytesLeft - this.segsPerTier * levelSize);
/* 321 */       levelSize *= this.maxMergeAtOnce;
/*     */     }
/* 323 */     int allowedSegCountInt = (int)allowedSegCount;
/*     */ 
/* 325 */     MergePolicy.MergeSpecification spec = null;
/*     */     while (true)
/*     */     {
/* 330 */       long mergingBytes = 0L;
/*     */ 
/* 335 */       List eligible = new ArrayList();
/* 336 */       for (int idx = tooBigCount; idx < infosSorted.size(); idx++) {
/* 337 */         SegmentInfo info = (SegmentInfo)infosSorted.get(idx);
/* 338 */         if (merging.contains(info))
/* 339 */           mergingBytes += info.sizeInBytes(true);
/* 340 */         else if (!toBeMerged.contains(info)) {
/* 341 */           eligible.add(info);
/*     */         }
/*     */       }
/*     */ 
/* 345 */       boolean maxMergeIsRunning = mergingBytes >= this.maxMergedSegmentBytes;
/*     */ 
/* 347 */       message("  allowedSegmentCount=" + allowedSegCountInt + " vs count=" + infosSorted.size() + " (eligible count=" + eligible.size() + ") tooBigCount=" + tooBigCount);
/*     */ 
/* 349 */       if (eligible.size() == 0) {
/* 350 */         return spec;
/*     */       }
/*     */ 
/* 353 */       if (eligible.size() >= allowedSegCountInt)
/*     */       {
/* 356 */         MergeScore bestScore = null;
/* 357 */         List best = null;
/* 358 */         boolean bestTooLarge = false;
/* 359 */         long bestMergeBytes = 0L;
/*     */ 
/* 362 */         for (int startIdx = 0; startIdx <= eligible.size() - this.maxMergeAtOnce; startIdx++)
/*     */         {
/* 364 */           long totAfterMergeBytes = 0L;
/*     */ 
/* 366 */           List candidate = new ArrayList();
/* 367 */           boolean hitTooLarge = false;
/* 368 */           for (int idx = startIdx; (idx < eligible.size()) && (candidate.size() < this.maxMergeAtOnce); idx++) {
/* 369 */             SegmentInfo info = (SegmentInfo)eligible.get(idx);
/* 370 */             long segBytes = size(info);
/*     */ 
/* 372 */             if (totAfterMergeBytes + segBytes > this.maxMergedSegmentBytes) {
/* 373 */               hitTooLarge = true;
/*     */             }
/*     */             else
/*     */             {
/* 382 */               candidate.add(info);
/* 383 */               totAfterMergeBytes += segBytes;
/*     */             }
/*     */           }
/* 386 */           MergeScore score = score(candidate, hitTooLarge, mergingBytes);
/* 387 */           message("  maybe=" + ((IndexWriter)this.writer.get()).segString(candidate) + " score=" + score.getScore() + " " + score.getExplanation() + " tooLarge=" + hitTooLarge + " size=" + String.format("%.3f MB", new Object[] { Double.valueOf(totAfterMergeBytes / 1024.0D / 1024.0D) }));
/*     */ 
/* 392 */           if (((bestScore == null) || (score.getScore() < bestScore.getScore())) && ((!hitTooLarge) || (!maxMergeIsRunning))) {
/* 393 */             best = candidate;
/* 394 */             bestScore = score;
/* 395 */             bestTooLarge = hitTooLarge;
/* 396 */             bestMergeBytes = totAfterMergeBytes;
/*     */           }
/*     */         }
/*     */ 
/* 400 */         if (best != null) {
/* 401 */           if (spec == null) {
/* 402 */             spec = new MergePolicy.MergeSpecification();
/*     */           }
/* 404 */           MergePolicy.OneMerge merge = new MergePolicy.OneMerge(best);
/* 405 */           spec.add(merge);
/* 406 */           for (SegmentInfo info : merge.segments) {
/* 407 */             toBeMerged.add(info);
/*     */           }
/*     */ 
/* 410 */           if (verbose())
/* 411 */             message("  add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments) + " size=" + String.format("%.3f MB", new Object[] { Double.valueOf(bestMergeBytes / 1024.0D / 1024.0D) }) + " score=" + String.format("%.3f", new Object[] { Double.valueOf(bestScore.getScore()) }) + " " + bestScore.getExplanation() + (bestTooLarge ? " [max merge]" : ""));
/*     */         }
/*     */         else {
/* 414 */           return spec;
/*     */         }
/*     */       } else {
/* 417 */         return spec;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected MergeScore score(List<SegmentInfo> candidate, boolean hitTooLarge, long mergingBytes) throws IOException
/*     */   {
/* 424 */     long totBeforeMergeBytes = 0L;
/* 425 */     long totAfterMergeBytes = 0L;
/* 426 */     long totAfterMergeBytesFloored = 0L;
/* 427 */     for (SegmentInfo info : candidate) {
/* 428 */       long segBytes = size(info);
/* 429 */       totAfterMergeBytes += segBytes;
/* 430 */       totAfterMergeBytesFloored += floorSize(segBytes);
/* 431 */       totBeforeMergeBytes += info.sizeInBytes(true);
/*     */     }
/*     */     double skew;
/*     */     double skew;
/* 438 */     if (hitTooLarge)
/*     */     {
/* 443 */       skew = 1.0D / this.maxMergeAtOnce;
/*     */     }
/* 445 */     else skew = floorSize(size((SegmentInfo)candidate.get(0))) / totAfterMergeBytesFloored;
/*     */ 
/* 450 */     double mergeScore = skew;
/*     */ 
/* 456 */     mergeScore *= Math.pow(totAfterMergeBytes, 0.05D);
/*     */ 
/* 459 */     double nonDelRatio = totAfterMergeBytes / totBeforeMergeBytes;
/* 460 */     mergeScore *= Math.pow(nonDelRatio, this.reclaimDeletesWeight);
/*     */ 
/* 462 */     double finalMergeScore = mergeScore;
/*     */ 
/* 464 */     return new MergeScore(finalMergeScore, skew, nonDelRatio)
/*     */     {
/*     */       public double getScore()
/*     */       {
/* 468 */         return this.val$finalMergeScore;
/*     */       }
/*     */ 
/*     */       public String getExplanation()
/*     */       {
/* 473 */         return "skew=" + String.format("%.3f", new Object[] { Double.valueOf(this.val$skew) }) + " nonDelRatio=" + String.format("%.3f", new Object[] { Double.valueOf(this.val$nonDelRatio) });
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesForOptimize(SegmentInfos infos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToOptimize) throws IOException {
/* 480 */     if (verbose()) {
/* 481 */       message("findMergesForOptimize maxSegmentCount=" + maxSegmentCount + " infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " segmentsToOptimize=" + segmentsToOptimize);
/*     */     }
/*     */ 
/* 484 */     List eligible = new ArrayList();
/* 485 */     boolean optimizeMergeRunning = false;
/* 486 */     Collection merging = ((IndexWriter)this.writer.get()).getMergingSegments();
/* 487 */     boolean segmentIsOriginal = false;
/* 488 */     for (SegmentInfo info : infos) {
/* 489 */       Boolean isOriginal = (Boolean)segmentsToOptimize.get(info);
/* 490 */       if (isOriginal != null) {
/* 491 */         segmentIsOriginal = isOriginal.booleanValue();
/* 492 */         if (!merging.contains(info))
/* 493 */           eligible.add(info);
/*     */         else {
/* 495 */           optimizeMergeRunning = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 500 */     if (eligible.size() == 0) {
/* 501 */       return null;
/*     */     }
/*     */ 
/* 504 */     if (((maxSegmentCount > 1) && (eligible.size() <= maxSegmentCount)) || ((maxSegmentCount == 1) && (eligible.size() == 1) && ((!segmentIsOriginal) || (isOptimized((SegmentInfo)eligible.get(0))))))
/*     */     {
/* 506 */       if (verbose()) {
/* 507 */         message("already optimized");
/*     */       }
/* 509 */       return null;
/*     */     }
/*     */ 
/* 512 */     Collections.sort(eligible, this.segmentByteSizeDescending);
/*     */ 
/* 514 */     if (verbose()) {
/* 515 */       message("eligible=" + eligible);
/* 516 */       message("optimizeMergeRunning=" + optimizeMergeRunning);
/*     */     }
/*     */ 
/* 519 */     int end = eligible.size();
/*     */ 
/* 521 */     MergePolicy.MergeSpecification spec = null;
/*     */ 
/* 524 */     while (end >= this.maxMergeAtOnceExplicit + maxSegmentCount - 1) {
/* 525 */       if (spec == null) {
/* 526 */         spec = new MergePolicy.MergeSpecification();
/*     */       }
/* 528 */       MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(end - this.maxMergeAtOnceExplicit, end));
/* 529 */       if (verbose()) {
/* 530 */         message("add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments));
/*     */       }
/* 532 */       spec.add(merge);
/* 533 */       end -= this.maxMergeAtOnceExplicit;
/*     */     }
/*     */ 
/* 536 */     if ((spec == null) && (!optimizeMergeRunning))
/*     */     {
/* 538 */       int numToMerge = end - maxSegmentCount + 1;
/* 539 */       MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(end - numToMerge, end));
/* 540 */       if (verbose()) {
/* 541 */         message("add final merge=" + merge.segString(((IndexWriter)this.writer.get()).getDirectory()));
/*     */       }
/* 543 */       spec = new MergePolicy.MergeSpecification();
/* 544 */       spec.add(merge);
/*     */     }
/*     */ 
/* 547 */     return spec;
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesToExpungeDeletes(SegmentInfos infos)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/* 553 */     if (verbose()) {
/* 554 */       message("findMergesToExpungeDeletes infos=" + ((IndexWriter)this.writer.get()).segString(infos) + " expungeDeletesPctAllowed=" + this.expungeDeletesPctAllowed);
/*     */     }
/* 556 */     List eligible = new ArrayList();
/* 557 */     Collection merging = ((IndexWriter)this.writer.get()).getMergingSegments();
/* 558 */     for (SegmentInfo info : infos) {
/* 559 */       double pctDeletes = 100.0D * ((IndexWriter)this.writer.get()).numDeletedDocs(info) / info.docCount;
/* 560 */       if ((pctDeletes > this.expungeDeletesPctAllowed) && (!merging.contains(info))) {
/* 561 */         eligible.add(info);
/*     */       }
/*     */     }
/*     */ 
/* 565 */     if (eligible.size() == 0) {
/* 566 */       return null;
/*     */     }
/*     */ 
/* 569 */     Collections.sort(eligible, this.segmentByteSizeDescending);
/*     */ 
/* 571 */     if (verbose()) {
/* 572 */       message("eligible=" + eligible);
/*     */     }
/*     */ 
/* 575 */     int start = 0;
/* 576 */     MergePolicy.MergeSpecification spec = null;
/*     */ 
/* 578 */     while (start < eligible.size()) {
/* 579 */       long totAfterMergeBytes = 0L;
/* 580 */       int upto = start;
/* 581 */       boolean done = false;
/* 582 */       while (upto < start + this.maxMergeAtOnceExplicit) {
/* 583 */         if (upto == eligible.size()) {
/* 584 */           done = true;
/* 585 */           break;
/*     */         }
/* 587 */         SegmentInfo info = (SegmentInfo)eligible.get(upto);
/* 588 */         long segBytes = size(info);
/* 589 */         if (totAfterMergeBytes + segBytes > this.maxMergedSegmentBytes)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 595 */         totAfterMergeBytes += segBytes;
/* 596 */         upto++;
/*     */       }
/*     */ 
/* 599 */       if (upto == start)
/*     */       {
/* 601 */         start++;
/* 602 */         continue;
/*     */       }
/*     */ 
/* 605 */       if (spec == null) {
/* 606 */         spec = new MergePolicy.MergeSpecification();
/*     */       }
/*     */ 
/* 609 */       MergePolicy.OneMerge merge = new MergePolicy.OneMerge(eligible.subList(start, upto));
/* 610 */       if (verbose()) {
/* 611 */         message("add merge=" + ((IndexWriter)this.writer.get()).segString(merge.segments));
/*     */       }
/* 613 */       spec.add(merge);
/* 614 */       start = upto;
/* 615 */       if (done)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/* 620 */     return spec;
/*     */   }
/*     */ 
/*     */   public boolean useCompoundFile(SegmentInfos infos, SegmentInfo mergedInfo)
/*     */     throws IOException
/*     */   {
/*     */     boolean doCFS;
/*     */     boolean doCFS;
/* 627 */     if (!this.useCompoundFile) {
/* 628 */       doCFS = false;
/*     */     }
/*     */     else
/*     */     {
/*     */       boolean doCFS;
/* 629 */       if (this.noCFSRatio == 1.0D) {
/* 630 */         doCFS = true;
/*     */       } else {
/* 632 */         long totalSize = 0L;
/* 633 */         for (SegmentInfo info : infos) {
/* 634 */           totalSize += size(info);
/*     */         }
/* 636 */         doCFS = size(mergedInfo) <= this.noCFSRatio * totalSize;
/*     */       }
/*     */     }
/* 638 */     return doCFS;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   private boolean isOptimized(SegmentInfo info) throws IOException
/*     */   {
/* 647 */     IndexWriter w = (IndexWriter)this.writer.get();
/* 648 */     assert (w != null);
/* 649 */     boolean hasDeletions = w.numDeletedDocs(info) > 0;
/* 650 */     return (!hasDeletions) && (!info.hasSeparateNorms()) && (info.dir == w.getDirectory()) && ((info.getUseCompoundFile() == this.useCompoundFile) || (this.noCFSRatio < 1.0D));
/*     */   }
/*     */ 
/*     */   private long size(SegmentInfo info)
/*     */     throws IOException
/*     */   {
/* 658 */     long byteSize = info.sizeInBytes(true);
/* 659 */     int delCount = ((IndexWriter)this.writer.get()).numDeletedDocs(info);
/* 660 */     double delRatio = info.docCount <= 0 ? 0.0D : delCount / info.docCount;
/* 661 */     assert (delRatio <= 1.0D);
/* 662 */     return ()(byteSize * (1.0D - delRatio));
/*     */   }
/*     */ 
/*     */   private long floorSize(long bytes) {
/* 666 */     return Math.max(this.floorSegmentBytes, bytes);
/*     */   }
/*     */ 
/*     */   private boolean verbose() {
/* 670 */     IndexWriter w = (IndexWriter)this.writer.get();
/* 671 */     return (w != null) && (w.verbose());
/*     */   }
/*     */ 
/*     */   private void message(String message) {
/* 675 */     if (verbose())
/* 676 */       ((IndexWriter)this.writer.get()).message("TMP: " + message);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 682 */     StringBuilder sb = new StringBuilder("[" + getClass().getSimpleName() + ": ");
/* 683 */     sb.append("maxMergeAtOnce=").append(this.maxMergeAtOnce).append(", ");
/* 684 */     sb.append("maxMergeAtOnceExplicit=").append(this.maxMergeAtOnceExplicit).append(", ");
/* 685 */     sb.append("maxMergedSegmentMB=").append(this.maxMergedSegmentBytes / 1024L / 1024.0D).append(", ");
/* 686 */     sb.append("floorSegmentMB=").append(this.floorSegmentBytes / 1024L / 1024.0D).append(", ");
/* 687 */     sb.append("expungeDeletesPctAllowed=").append(this.expungeDeletesPctAllowed).append(", ");
/* 688 */     sb.append("segmentsPerTier=").append(this.segsPerTier).append(", ");
/* 689 */     sb.append("useCompoundFile=").append(this.useCompoundFile).append(", ");
/* 690 */     sb.append("noCFSRatio=").append(this.noCFSRatio);
/* 691 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected static abstract class MergeScore
/*     */   {
/*     */     abstract double getScore();
/*     */ 
/*     */     abstract String getExplanation();
/*     */   }
/*     */ 
/*     */   private class SegmentByteSizeDescending
/*     */     implements Comparator<SegmentInfo>
/*     */   {
/*     */     private SegmentByteSizeDescending()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int compare(SegmentInfo o1, SegmentInfo o2)
/*     */     {
/*     */       try
/*     */       {
/* 243 */         long sz1 = TieredMergePolicy.this.size(o1);
/* 244 */         long sz2 = TieredMergePolicy.this.size(o2);
/* 245 */         if (sz1 > sz2)
/* 246 */           return -1;
/* 247 */         if (sz2 > sz1) {
/* 248 */           return 1;
/*     */         }
/* 250 */         return o1.name.compareTo(o2.name);
/*     */       } catch (IOException ioe) {
/*     */       }
/* 253 */       throw new RuntimeException(ioe);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TieredMergePolicy
 * JD-Core Version:    0.6.0
 */