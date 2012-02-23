/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public final class NoMergePolicy extends MergePolicy
/*    */ {
/* 38 */   public static final MergePolicy NO_COMPOUND_FILES = new NoMergePolicy(false);
/*    */ 
/* 44 */   public static final MergePolicy COMPOUND_FILES = new NoMergePolicy(true);
/*    */   private final boolean useCompoundFile;
/*    */ 
/*    */   private NoMergePolicy(boolean useCompoundFile)
/*    */   {
/* 50 */     this.useCompoundFile = useCompoundFile;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MergePolicy.MergeSpecification findMerges(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public MergePolicy.MergeSpecification findMergesForOptimize(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfo, Boolean> segmentsToOptimize) throws CorruptIndexException, IOException
/*    */   {
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */   public MergePolicy.MergeSpecification findMergesToExpungeDeletes(SegmentInfos segmentInfos) throws CorruptIndexException, IOException {
/* 67 */     return null;
/*    */   }
/*    */   public boolean useCompoundFile(SegmentInfos segments, SegmentInfo newSegment) {
/* 70 */     return this.useCompoundFile;
/*    */   }
/*    */ 
/*    */   public void setIndexWriter(IndexWriter writer) {
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 77 */     return "NoMergePolicy";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.NoMergePolicy
 * JD-Core Version:    0.6.0
 */