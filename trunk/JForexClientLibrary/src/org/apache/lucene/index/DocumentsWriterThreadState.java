/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ final class DocumentsWriterThreadState
/*    */ {
/* 28 */   boolean isIdle = true;
/* 29 */   int numThreads = 1;
/*    */   final DocConsumerPerThread consumer;
/*    */   final DocumentsWriter.DocState docState;
/*    */   final DocumentsWriter docWriter;
/*    */ 
/*    */   public DocumentsWriterThreadState(DocumentsWriter docWriter)
/*    */     throws IOException
/*    */   {
/* 36 */     this.docWriter = docWriter;
/* 37 */     this.docState = new DocumentsWriter.DocState();
/* 38 */     this.docState.maxFieldLength = docWriter.maxFieldLength;
/* 39 */     this.docState.infoStream = docWriter.infoStream;
/* 40 */     this.docState.similarity = docWriter.similarity;
/* 41 */     this.docState.docWriter = docWriter;
/* 42 */     this.consumer = docWriter.consumer.addThread(this);
/*    */   }
/*    */ 
/*    */   void doAfterFlush() {
/* 46 */     this.numThreads = 0;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocumentsWriterThreadState
 * JD-Core Version:    0.6.0
 */