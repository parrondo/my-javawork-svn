/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.store.IndexOutput;
/*    */ 
/*    */ final class FormatPostingsTermsWriter extends FormatPostingsTermsConsumer
/*    */   implements Closeable
/*    */ {
/*    */   final FormatPostingsFieldsWriter parent;
/*    */   final FormatPostingsDocsWriter docsWriter;
/*    */   final TermInfosWriter termsOut;
/*    */   FieldInfo fieldInfo;
/*    */   char[] currentTerm;
/*    */   int currentTermStart;
/*    */   long freqStart;
/*    */   long proxStart;
/*    */ 
/*    */   FormatPostingsTermsWriter(SegmentWriteState state, FormatPostingsFieldsWriter parent)
/*    */     throws IOException
/*    */   {
/* 31 */     this.parent = parent;
/* 32 */     this.termsOut = parent.termsOut;
/* 33 */     this.docsWriter = new FormatPostingsDocsWriter(state, this);
/*    */   }
/*    */ 
/*    */   void setField(FieldInfo fieldInfo) {
/* 37 */     this.fieldInfo = fieldInfo;
/* 38 */     this.docsWriter.setField(fieldInfo);
/*    */   }
/*    */ 
/*    */   FormatPostingsDocsConsumer addTerm(char[] text, int start)
/*    */   {
/* 50 */     this.currentTerm = text;
/* 51 */     this.currentTermStart = start;
/*    */ 
/* 56 */     this.freqStart = this.docsWriter.out.getFilePointer();
/* 57 */     if (this.docsWriter.posWriter.out != null) {
/* 58 */       this.proxStart = this.docsWriter.posWriter.out.getFilePointer();
/*    */     }
/* 60 */     this.parent.skipListWriter.resetSkip();
/*    */ 
/* 62 */     return this.docsWriter;
/*    */   }
/*    */ 
/*    */   void finish()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 71 */     this.docsWriter.close();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsTermsWriter
 * JD-Core Version:    0.6.0
 */