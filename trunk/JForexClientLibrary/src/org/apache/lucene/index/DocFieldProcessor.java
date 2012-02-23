/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ final class DocFieldProcessor extends DocConsumer
/*    */ {
/*    */   final DocumentsWriter docWriter;
/*    */   final FieldInfos fieldInfos;
/*    */   final DocFieldConsumer consumer;
/*    */   final StoredFieldsWriter fieldsWriter;
/*    */ 
/*    */   public DocFieldProcessor(DocumentsWriter docWriter, DocFieldConsumer consumer)
/*    */   {
/* 42 */     this.docWriter = docWriter;
/* 43 */     this.consumer = consumer;
/* 44 */     this.fieldInfos = docWriter.getFieldInfos();
/* 45 */     consumer.setFieldInfos(this.fieldInfos);
/* 46 */     this.fieldsWriter = new StoredFieldsWriter(docWriter, this.fieldInfos);
/*    */   }
/*    */ 
/*    */   public void flush(Collection<DocConsumerPerThread> threads, SegmentWriteState state)
/*    */     throws IOException
/*    */   {
/* 52 */     Map childThreadsAndFields = new HashMap();
/* 53 */     for (DocConsumerPerThread thread : threads) {
/* 54 */       DocFieldProcessorPerThread perThread = (DocFieldProcessorPerThread)thread;
/* 55 */       childThreadsAndFields.put(perThread.consumer, perThread.fields());
/* 56 */       perThread.trimFields(state);
/*    */     }
/*    */ 
/* 59 */     this.fieldsWriter.flush(state);
/* 60 */     this.consumer.flush(childThreadsAndFields, state);
/*    */ 
/* 66 */     String fileName = IndexFileNames.segmentFileName(state.segmentName, "fnm");
/* 67 */     this.fieldInfos.write(state.directory, fileName);
/*    */   }
/*    */ 
/*    */   public void abort()
/*    */   {
/*    */     try {
/* 73 */       this.fieldsWriter.abort();
/*    */     } finally {
/* 75 */       this.consumer.abort();
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean freeRAM()
/*    */   {
/* 81 */     return this.consumer.freeRAM();
/*    */   }
/*    */ 
/*    */   public DocConsumerPerThread addThread(DocumentsWriterThreadState threadState) throws IOException
/*    */   {
/* 86 */     return new DocFieldProcessorPerThread(threadState, this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocFieldProcessor
 * JD-Core Version:    0.6.0
 */