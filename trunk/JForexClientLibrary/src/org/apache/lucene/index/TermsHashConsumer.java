/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.Map;
/*    */ 
/*    */ abstract class TermsHashConsumer
/*    */ {
/*    */   FieldInfos fieldInfos;
/*    */ 
/*    */   abstract TermsHashConsumerPerThread addThread(TermsHashPerThread paramTermsHashPerThread);
/*    */ 
/*    */   abstract void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> paramMap, SegmentWriteState paramSegmentWriteState)
/*    */     throws IOException;
/*    */ 
/*    */   abstract void abort();
/*    */ 
/*    */   void setFieldInfos(FieldInfos fieldInfos)
/*    */   {
/* 32 */     this.fieldInfos = fieldInfos;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHashConsumer
 * JD-Core Version:    0.6.0
 */