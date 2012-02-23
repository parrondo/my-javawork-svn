/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import org.apache.lucene.util.UnicodeUtil.UTF8Result;
/*    */ 
/*    */ final class TermVectorsTermsWriterPerThread extends TermsHashConsumerPerThread
/*    */ {
/*    */   final TermVectorsTermsWriter termsWriter;
/*    */   final TermsHashPerThread termsHashPerThread;
/*    */   final DocumentsWriter.DocState docState;
/*    */   TermVectorsTermsWriter.PerDoc doc;
/* 37 */   final ByteSliceReader vectorSliceReader = new ByteSliceReader();
/*    */ 
/* 39 */   final UnicodeUtil.UTF8Result[] utf8Results = { new UnicodeUtil.UTF8Result(), new UnicodeUtil.UTF8Result() };
/*    */   String lastVectorFieldName;
/*    */ 
/*    */   public TermVectorsTermsWriterPerThread(TermsHashPerThread termsHashPerThread, TermVectorsTermsWriter termsWriter)
/*    */   {
/* 31 */     this.termsWriter = termsWriter;
/* 32 */     this.termsHashPerThread = termsHashPerThread;
/* 33 */     this.docState = termsHashPerThread.docState;
/*    */   }
/*    */ 
/*    */   public void startDocument()
/*    */   {
/* 44 */     assert (clearLastVectorFieldName());
/* 45 */     if (this.doc != null) {
/* 46 */       this.doc.reset();
/* 47 */       this.doc.docID = this.docState.docID;
/*    */     }
/*    */   }
/*    */ 
/*    */   public DocumentsWriter.DocWriter finishDocument()
/*    */   {
/*    */     try {
/* 54 */       localPerDoc = this.doc;
/*    */     }
/*    */     finally
/*    */     {
/*    */       TermVectorsTermsWriter.PerDoc localPerDoc;
/* 56 */       this.doc = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public TermsHashConsumerPerField addField(TermsHashPerField termsHashPerField, FieldInfo fieldInfo)
/*    */   {
/* 62 */     return new TermVectorsTermsWriterPerField(termsHashPerField, this, fieldInfo);
/*    */   }
/*    */ 
/*    */   public void abort()
/*    */   {
/* 67 */     if (this.doc != null) {
/* 68 */       this.doc.abort();
/* 69 */       this.doc = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   final boolean clearLastVectorFieldName()
/*    */   {
/* 75 */     this.lastVectorFieldName = null;
/* 76 */     return true;
/*    */   }
/*    */ 
/*    */   final boolean vectorFieldsInOrder(FieldInfo fi)
/*    */   {
/*    */     try
/*    */     {
/* 83 */       if (this.lastVectorFieldName != null) {
/* 84 */         i = this.lastVectorFieldName.compareTo(fi.name) < 0 ? 1 : 0; jsr 18;
/*    */       }
/* 86 */       i = 1;
/*    */     }
/*    */     finally
/*    */     {
/*    */       int i;
/* 88 */       this.lastVectorFieldName = fi.name;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermVectorsTermsWriterPerThread
 * JD-Core Version:    0.6.0
 */