/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ final class DocInverter extends DocFieldConsumer
/*    */ {
/*    */   final InvertedDocConsumer consumer;
/*    */   final InvertedDocEndConsumer endConsumer;
/*    */ 
/*    */   public DocInverter(InvertedDocConsumer consumer, InvertedDocEndConsumer endConsumer)
/*    */   {
/* 38 */     this.consumer = consumer;
/* 39 */     this.endConsumer = endConsumer;
/*    */   }
/*    */ 
/*    */   void setFieldInfos(FieldInfos fieldInfos)
/*    */   {
/* 44 */     super.setFieldInfos(fieldInfos);
/* 45 */     this.consumer.setFieldInfos(fieldInfos);
/* 46 */     this.endConsumer.setFieldInfos(fieldInfos);
/*    */   }
/*    */ 
/*    */   void flush(Map<DocFieldConsumerPerThread, Collection<DocFieldConsumerPerField>> threadsAndFields, SegmentWriteState state)
/*    */     throws IOException
/*    */   {
/* 52 */     Map childThreadsAndFields = new HashMap();
/* 53 */     Map endChildThreadsAndFields = new HashMap();
/*    */ 
/* 55 */     for (Map.Entry entry : threadsAndFields.entrySet()) {
/* 56 */       DocInverterPerThread perThread = (DocInverterPerThread)entry.getKey();
/*    */ 
/* 58 */       Collection childFields = new HashSet();
/* 59 */       Collection endChildFields = new HashSet();
/* 60 */       for (DocFieldConsumerPerField field : (Collection)entry.getValue()) {
/* 61 */         DocInverterPerField perField = (DocInverterPerField)field;
/* 62 */         childFields.add(perField.consumer);
/* 63 */         endChildFields.add(perField.endConsumer);
/*    */       }
/*    */ 
/* 66 */       childThreadsAndFields.put(perThread.consumer, childFields);
/* 67 */       endChildThreadsAndFields.put(perThread.endConsumer, endChildFields);
/*    */     }
/*    */ 
/* 70 */     this.consumer.flush(childThreadsAndFields, state);
/* 71 */     this.endConsumer.flush(endChildThreadsAndFields, state);
/*    */   }
/*    */ 
/*    */   void abort()
/*    */   {
/*    */     try {
/* 77 */       this.consumer.abort();
/*    */     } finally {
/* 79 */       this.endConsumer.abort();
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean freeRAM()
/*    */   {
/* 85 */     return this.consumer.freeRAM();
/*    */   }
/*    */ 
/*    */   public DocFieldConsumerPerThread addThread(DocFieldProcessorPerThread docFieldProcessorPerThread)
/*    */   {
/* 90 */     return new DocInverterPerThread(docFieldProcessorPerThread, this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocInverter
 * JD-Core Version:    0.6.0
 */