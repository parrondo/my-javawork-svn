/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.document.Fieldable;
/*    */ import org.apache.lucene.store.IndexOutput;
/*    */ import org.apache.lucene.store.RAMOutputStream;
/*    */ 
/*    */ final class StoredFieldsWriterPerThread
/*    */ {
/*    */   final FieldsWriter localFieldsWriter;
/*    */   final StoredFieldsWriter storedFieldsWriter;
/*    */   final DocumentsWriter.DocState docState;
/*    */   StoredFieldsWriter.PerDoc doc;
/*    */ 
/*    */   public StoredFieldsWriterPerThread(DocumentsWriter.DocState docState, StoredFieldsWriter storedFieldsWriter)
/*    */     throws IOException
/*    */   {
/* 33 */     this.storedFieldsWriter = storedFieldsWriter;
/* 34 */     this.docState = docState;
/* 35 */     this.localFieldsWriter = new FieldsWriter((IndexOutput)null, (IndexOutput)null, storedFieldsWriter.fieldInfos);
/*    */   }
/*    */ 
/*    */   public void startDocument() {
/* 39 */     if (this.doc != null)
/*    */     {
/* 43 */       this.doc.reset();
/* 44 */       this.doc.docID = this.docState.docID;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addField(Fieldable field, FieldInfo fieldInfo) throws IOException {
/* 49 */     if (this.doc == null) {
/* 50 */       this.doc = this.storedFieldsWriter.getPerDoc();
/* 51 */       this.doc.docID = this.docState.docID;
/* 52 */       this.localFieldsWriter.setFieldsStream(this.doc.fdt);
/* 53 */       assert (this.doc.numStoredFields == 0) : ("doc.numStoredFields=" + this.doc.numStoredFields);
/* 54 */       assert (0L == this.doc.fdt.length());
/* 55 */       assert (0L == this.doc.fdt.getFilePointer());
/*    */     }
/*    */ 
/* 58 */     this.localFieldsWriter.writeField(fieldInfo, field);
/* 59 */     assert (this.docState.testPoint("StoredFieldsWriterPerThread.processFields.writeField"));
/* 60 */     this.doc.numStoredFields += 1;
/*    */   }
/*    */ 
/*    */   public DocumentsWriter.DocWriter finishDocument()
/*    */   {
/*    */     try
/*    */     {
/* 67 */       localPerDoc = this.doc;
/*    */     }
/*    */     finally
/*    */     {
/*    */       StoredFieldsWriter.PerDoc localPerDoc;
/* 69 */       this.doc = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void abort() {
/* 74 */     if (this.doc != null) {
/* 75 */       this.doc.abort();
/* 76 */       this.doc = null;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.StoredFieldsWriterPerThread
 * JD-Core Version:    0.6.0
 */