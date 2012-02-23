/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.store.Directory;
/*    */ import org.apache.lucene.store.IndexOutput;
/*    */ import org.apache.lucene.util.IOUtils;
/*    */ 
/*    */ final class FormatPostingsPositionsWriter extends FormatPostingsPositionsConsumer
/*    */   implements Closeable
/*    */ {
/*    */   final FormatPostingsDocsWriter parent;
/*    */   final IndexOutput out;
/*    */   boolean omitTermFreqAndPositions;
/*    */   boolean storePayloads;
/* 35 */   int lastPayloadLength = -1;
/*    */   int lastPosition;
/*    */ 
/*    */   FormatPostingsPositionsWriter(SegmentWriteState state, FormatPostingsDocsWriter parent)
/*    */     throws IOException
/*    */   {
/* 38 */     this.parent = parent;
/* 39 */     this.omitTermFreqAndPositions = parent.omitTermFreqAndPositions;
/* 40 */     if (parent.parent.parent.fieldInfos.hasProx())
/*    */     {
/* 43 */       this.out = parent.parent.parent.dir.createOutput(IndexFileNames.segmentFileName(parent.parent.parent.segment, "prx"));
/* 44 */       parent.skipListWriter.setProxOutput(this.out);
/*    */     }
/*    */     else {
/* 47 */       this.out = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   void addPosition(int position, byte[] payload, int payloadOffset, int payloadLength)
/*    */     throws IOException
/*    */   {
/* 55 */     assert (!this.omitTermFreqAndPositions) : "omitTermFreqAndPositions is true";
/* 56 */     assert (this.out != null);
/*    */ 
/* 58 */     int delta = position - this.lastPosition;
/* 59 */     this.lastPosition = position;
/*    */ 
/* 61 */     if (this.storePayloads) {
/* 62 */       if (payloadLength != this.lastPayloadLength) {
/* 63 */         this.lastPayloadLength = payloadLength;
/* 64 */         this.out.writeVInt(delta << 1 | 0x1);
/* 65 */         this.out.writeVInt(payloadLength);
/*    */       } else {
/* 67 */         this.out.writeVInt(delta << 1);
/* 68 */       }if (payloadLength > 0)
/* 69 */         this.out.writeBytes(payload, payloadLength);
/*    */     } else {
/* 71 */       this.out.writeVInt(delta);
/*    */     }
/*    */   }
/*    */ 
/*    */   void setField(FieldInfo fieldInfo) {
/* 75 */     this.omitTermFreqAndPositions = (fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY);
/* 76 */     this.storePayloads = (this.omitTermFreqAndPositions ? false : fieldInfo.storePayloads);
/*    */   }
/*    */ 
/*    */   void finish()
/*    */   {
/* 82 */     this.lastPosition = 0;
/* 83 */     this.lastPayloadLength = -1;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 87 */     IOUtils.close(new Closeable[] { this.out });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsPositionsWriter
 * JD-Core Version:    0.6.0
 */