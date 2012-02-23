/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.lucene.store.Directory;
/*    */ import org.apache.lucene.util.BitVector;
/*    */ 
/*    */ public class SegmentWriteState
/*    */ {
/*    */   public final PrintStream infoStream;
/*    */   public final Directory directory;
/*    */   public final String segmentName;
/*    */   public final FieldInfos fieldInfos;
/*    */   public final int numDocs;
/*    */   public boolean hasVectors;
/*    */   public final BufferedDeletes segDeletes;
/*    */   public BitVector deletedDocs;
/*    */   public final int termIndexInterval;
/* 58 */   public final int skipInterval = 16;
/*    */ 
/* 63 */   public final int maxSkipLevels = 10;
/*    */ 
/*    */   public SegmentWriteState(PrintStream infoStream, Directory directory, String segmentName, FieldInfos fieldInfos, int numDocs, int termIndexInterval, BufferedDeletes segDeletes)
/*    */   {
/* 67 */     this.infoStream = infoStream;
/* 68 */     this.segDeletes = segDeletes;
/* 69 */     this.directory = directory;
/* 70 */     this.segmentName = segmentName;
/* 71 */     this.fieldInfos = fieldInfos;
/* 72 */     this.numDocs = numDocs;
/* 73 */     this.termIndexInterval = termIndexInterval;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentWriteState
 * JD-Core Version:    0.6.0
 */