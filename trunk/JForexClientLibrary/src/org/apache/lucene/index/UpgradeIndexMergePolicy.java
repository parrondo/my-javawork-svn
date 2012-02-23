/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.Constants;
/*     */ import org.apache.lucene.util.SetOnce;
/*     */ 
/*     */ public class UpgradeIndexMergePolicy extends MergePolicy
/*     */ {
/*     */   protected final MergePolicy base;
/*     */ 
/*     */   public UpgradeIndexMergePolicy(MergePolicy base)
/*     */   {
/*  58 */     this.base = base;
/*     */   }
/*     */ 
/*     */   protected boolean shouldUpgradeSegment(SegmentInfo si)
/*     */   {
/*  67 */     return !Constants.LUCENE_MAIN_VERSION.equals(si.getVersion());
/*     */   }
/*     */ 
/*     */   public void setIndexWriter(IndexWriter writer)
/*     */   {
/*  72 */     super.setIndexWriter(writer);
/*  73 */     this.base.setIndexWriter(writer);
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException
/*     */   {
/*  78 */     return this.base.findMerges(segmentInfos);
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesForOptimize(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToOptimize)
/*     */     throws CorruptIndexException, IOException
/*     */   {
/*  84 */     Map oldSegments = new HashMap();
/*  85 */     for (SegmentInfo si : segmentInfos) {
/*  86 */       Boolean v = (Boolean)segmentsToOptimize.get(si);
/*  87 */       if ((v != null) && (shouldUpgradeSegment(si))) {
/*  88 */         oldSegments.put(si, v);
/*     */       }
/*     */     }
/*     */ 
/*  92 */     if (verbose()) message("findMergesForOptimize: segmentsToUpgrade=" + oldSegments);
/*     */ 
/*  94 */     if (oldSegments.isEmpty()) {
/*  95 */       return null;
/*     */     }
/*  97 */     MergePolicy.MergeSpecification spec = this.base.findMergesForOptimize(segmentInfos, maxSegmentCount, oldSegments);
/*     */ 
/*  99 */     if (spec != null)
/*     */     {
/* 103 */       for (MergePolicy.OneMerge om : spec.merges) {
/* 104 */         oldSegments.keySet().removeAll(om.segments);
/*     */       }
/*     */     }
/*     */ 
/* 108 */     if (!oldSegments.isEmpty()) {
/* 109 */       if (verbose()) {
/* 110 */         message("findMergesForOptimize: " + this.base.getClass().getSimpleName() + " does not want to merge all old segments, merge remaining ones into new segment: " + oldSegments);
/*     */       }
/* 112 */       List newInfos = new ArrayList();
/* 113 */       for (SegmentInfo si : segmentInfos) {
/* 114 */         if (oldSegments.containsKey(si)) {
/* 115 */           newInfos.add(si);
/*     */         }
/*     */       }
/*     */ 
/* 119 */       if (spec == null) {
/* 120 */         spec = new MergePolicy.MergeSpecification();
/*     */       }
/* 122 */       spec.add(new MergePolicy.OneMerge(newInfos));
/*     */     }
/*     */ 
/* 125 */     return spec;
/*     */   }
/*     */ 
/*     */   public MergePolicy.MergeSpecification findMergesToExpungeDeletes(SegmentInfos segmentInfos) throws CorruptIndexException, IOException
/*     */   {
/* 130 */     return this.base.findMergesToExpungeDeletes(segmentInfos);
/*     */   }
/*     */ 
/*     */   public boolean useCompoundFile(SegmentInfos segments, SegmentInfo newSegment) throws IOException
/*     */   {
/* 135 */     return this.base.useCompoundFile(segments, newSegment);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 140 */     this.base.close();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 145 */     return "[" + getClass().getSimpleName() + "->" + this.base + "]";
/*     */   }
/*     */ 
/*     */   private boolean verbose() {
/* 149 */     IndexWriter w = (IndexWriter)this.writer.get();
/* 150 */     return (w != null) && (w.verbose());
/*     */   }
/*     */ 
/*     */   private void message(String message) {
/* 154 */     if (verbose())
/* 155 */       ((IndexWriter)this.writer.get()).message("UPGMP: " + message);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.UpgradeIndexMergePolicy
 * JD-Core Version:    0.6.0
 */