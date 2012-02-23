/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*    */ import org.apache.lucene.util.AttributeSource;
/*    */ 
/*    */ final class DocInverterPerThread extends DocFieldConsumerPerThread
/*    */ {
/*    */   final DocInverter docInverter;
/*    */   final InvertedDocConsumerPerThread consumer;
/*    */   final InvertedDocEndConsumerPerThread endConsumer;
/* 34 */   final SingleTokenAttributeSource singleToken = new SingleTokenAttributeSource(null);
/*    */   final DocumentsWriter.DocState docState;
/* 53 */   final FieldInvertState fieldState = new FieldInvertState();
/*    */ 
/* 56 */   final ReusableStringReader stringReader = new ReusableStringReader();
/*    */ 
/*    */   public DocInverterPerThread(DocFieldProcessorPerThread docFieldProcessorPerThread, DocInverter docInverter) {
/* 59 */     this.docInverter = docInverter;
/* 60 */     this.docState = docFieldProcessorPerThread.docState;
/* 61 */     this.consumer = docInverter.consumer.addThread(this);
/* 62 */     this.endConsumer = docInverter.endConsumer.addThread(this);
/*    */   }
/*    */ 
/*    */   public void startDocument() throws IOException
/*    */   {
/* 67 */     this.consumer.startDocument();
/* 68 */     this.endConsumer.startDocument();
/*    */   }
/*    */ 
/*    */   public DocumentsWriter.DocWriter finishDocument()
/*    */     throws IOException
/*    */   {
/* 75 */     this.endConsumer.finishDocument();
/* 76 */     return this.consumer.finishDocument();
/*    */   }
/*    */ 
/*    */   void abort()
/*    */   {
/*    */     try {
/* 82 */       this.consumer.abort();
/*    */     } finally {
/* 84 */       this.endConsumer.abort();
/*    */     }
/*    */   }
/*    */ 
/*    */   public DocFieldConsumerPerField addField(FieldInfo fi)
/*    */   {
/* 90 */     return new DocInverterPerField(this, fi);
/*    */   }
/*    */ 
/*    */   static class SingleTokenAttributeSource extends AttributeSource
/*    */   {
/*    */     final CharTermAttribute termAttribute;
/*    */     final OffsetAttribute offsetAttribute;
/*    */ 
/*    */     private SingleTokenAttributeSource()
/*    */     {
/* 41 */       this.termAttribute = ((CharTermAttribute)addAttribute(CharTermAttribute.class));
/* 42 */       this.offsetAttribute = ((OffsetAttribute)addAttribute(OffsetAttribute.class));
/*    */     }
/*    */ 
/*    */     public void reinit(String stringValue, int startOffset, int endOffset) {
/* 46 */       this.termAttribute.setEmpty().append(stringValue);
/* 47 */       this.offsetAttribute.setOffset(startOffset, endOffset);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocInverterPerThread
 * JD-Core Version:    0.6.0
 */