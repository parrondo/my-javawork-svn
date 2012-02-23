/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class LogByteSizeMergePolicy extends LogMergePolicy
/*     */ {
/*     */   public static final double DEFAULT_MIN_MERGE_MB = 1.6D;
/*     */   public static final double DEFAULT_MAX_MERGE_MB = 2048.0D;
/*     */   public static final double DEFAULT_MAX_MERGE_MB_FOR_OPTIMIZE = 9.223372036854776E+018D;
/*     */ 
/*     */   public LogByteSizeMergePolicy()
/*     */   {
/*  38 */     this.minMergeSize = 1677721L;
/*  39 */     this.maxMergeSize = 2147483648L;
/*  40 */     this.maxMergeSizeForOptimize = 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   protected long size(SegmentInfo info) throws IOException
/*     */   {
/*  45 */     return sizeBytes(info);
/*     */   }
/*     */ 
/*     */   public void setMaxMergeMB(double mb)
/*     */   {
/*  60 */     this.maxMergeSize = ()(mb * 1024.0D * 1024.0D);
/*     */   }
/*     */ 
/*     */   public double getMaxMergeMB()
/*     */   {
/*  68 */     return this.maxMergeSize / 1024.0D / 1024.0D;
/*     */   }
/*     */ 
/*     */   public void setMaxMergeMBForOptimize(double mb)
/*     */   {
/*  77 */     this.maxMergeSizeForOptimize = ()(mb * 1024.0D * 1024.0D);
/*     */   }
/*     */ 
/*     */   public double getMaxMergeMBForOptimize()
/*     */   {
/*  85 */     return this.maxMergeSizeForOptimize / 1024.0D / 1024.0D;
/*     */   }
/*     */ 
/*     */   public void setMinMergeMB(double mb)
/*     */   {
/*  98 */     this.minMergeSize = ()(mb * 1024.0D * 1024.0D);
/*     */   }
/*     */ 
/*     */   public double getMinMergeMB()
/*     */   {
/* 105 */     return this.minMergeSize / 1024.0D / 1024.0D;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.LogByteSizeMergePolicy
 * JD-Core Version:    0.6.0
 */