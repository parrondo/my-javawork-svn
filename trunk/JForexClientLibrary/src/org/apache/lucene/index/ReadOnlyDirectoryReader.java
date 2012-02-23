/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.Map;
/*    */ import org.apache.lucene.store.Directory;
/*    */ 
/*    */ class ReadOnlyDirectoryReader extends DirectoryReader
/*    */ {
/*    */   ReadOnlyDirectoryReader(Directory directory, SegmentInfos sis, IndexDeletionPolicy deletionPolicy, int termInfosIndexDivisor, Collection<IndexReader.ReaderFinishedListener> readerFinishedListeners)
/*    */     throws IOException
/*    */   {
/* 29 */     super(directory, sis, deletionPolicy, true, termInfosIndexDivisor, readerFinishedListeners);
/*    */   }
/*    */ 
/*    */   ReadOnlyDirectoryReader(Directory directory, SegmentInfos infos, SegmentReader[] oldReaders, int[] oldStarts, Map<String, byte[]> oldNormsCache, boolean doClone, int termInfosIndexDivisor, Collection<IndexReader.ReaderFinishedListener> readerFinishedListeners) throws IOException
/*    */   {
/* 34 */     super(directory, infos, oldReaders, oldStarts, oldNormsCache, true, doClone, termInfosIndexDivisor, readerFinishedListeners);
/*    */   }
/*    */ 
/*    */   ReadOnlyDirectoryReader(IndexWriter writer, SegmentInfos infos, int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException {
/* 38 */     super(writer, infos, termInfosIndexDivisor, applyAllDeletes);
/*    */   }
/*    */ 
/*    */   protected void acquireWriteLock()
/*    */   {
/* 43 */     ReadOnlySegmentReader.noWrite();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ReadOnlyDirectoryReader
 * JD-Core Version:    0.6.0
 */