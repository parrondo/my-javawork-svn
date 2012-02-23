/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.store.Directory;
/*    */ import org.apache.lucene.util.IOUtils;
/*    */ 
/*    */ final class FormatPostingsFieldsWriter extends FormatPostingsFieldsConsumer
/*    */ {
/*    */   final Directory dir;
/*    */   final String segment;
/*    */   TermInfosWriter termsOut;
/*    */   final FieldInfos fieldInfos;
/*    */   FormatPostingsTermsWriter termsWriter;
/*    */   final DefaultSkipListWriter skipListWriter;
/*    */   final int totalNumDocs;
/*    */ 
/*    */   public FormatPostingsFieldsWriter(SegmentWriteState state, FieldInfos fieldInfos)
/*    */     throws IOException
/*    */   {
/* 36 */     this.dir = state.directory;
/* 37 */     this.segment = state.segmentName;
/* 38 */     this.totalNumDocs = state.numDocs;
/* 39 */     this.fieldInfos = fieldInfos;
/* 40 */     boolean success = false;
/*    */     try {
/* 42 */       this.termsOut = new TermInfosWriter(this.dir, this.segment, fieldInfos, state.termIndexInterval);
/*    */ 
/* 48 */       this.skipListWriter = new DefaultSkipListWriter(this.termsOut.skipInterval, this.termsOut.maxSkipLevels, this.totalNumDocs, null, null);
/*    */ 
/* 51 */       this.termsWriter = new FormatPostingsTermsWriter(state, this);
/* 52 */       success = true;
/*    */     } finally {
/* 54 */       if (!success)
/* 55 */         IOUtils.closeWhileHandlingException(new Closeable[] { this.termsOut, this.termsWriter });
/*    */     }
/*    */   }
/*    */ 
/*    */   FormatPostingsTermsConsumer addField(FieldInfo field)
/*    */   {
/* 63 */     this.termsWriter.setField(field);
/* 64 */     return this.termsWriter;
/*    */   }
/*    */ 
/*    */   void finish()
/*    */     throws IOException
/*    */   {
/* 70 */     IOUtils.close(new Closeable[] { this.termsOut, this.termsWriter });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsFieldsWriter
 * JD-Core Version:    0.6.0
 */