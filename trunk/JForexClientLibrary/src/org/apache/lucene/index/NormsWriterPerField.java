/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import org.apache.lucene.search.Similarity;
/*    */ import org.apache.lucene.util.ArrayUtil;
/*    */ 
/*    */ final class NormsWriterPerField extends InvertedDocEndConsumerPerField
/*    */   implements Comparable<NormsWriterPerField>
/*    */ {
/*    */   final NormsWriterPerThread perThread;
/*    */   final FieldInfo fieldInfo;
/*    */   final DocumentsWriter.DocState docState;
/* 35 */   int[] docIDs = new int[1];
/* 36 */   byte[] norms = new byte[1];
/*    */   int upto;
/*    */   final FieldInvertState fieldState;
/*    */ 
/*    */   public void reset()
/*    */   {
/* 43 */     this.docIDs = ArrayUtil.shrink(this.docIDs, this.upto);
/* 44 */     this.norms = ArrayUtil.shrink(this.norms, this.upto);
/* 45 */     this.upto = 0;
/*    */   }
/*    */ 
/*    */   public NormsWriterPerField(DocInverterPerField docInverterPerField, NormsWriterPerThread perThread, FieldInfo fieldInfo) {
/* 49 */     this.perThread = perThread;
/* 50 */     this.fieldInfo = fieldInfo;
/* 51 */     this.docState = perThread.docState;
/* 52 */     this.fieldState = docInverterPerField.fieldState;
/*    */   }
/*    */ 
/*    */   void abort()
/*    */   {
/* 57 */     this.upto = 0;
/*    */   }
/*    */ 
/*    */   public int compareTo(NormsWriterPerField other) {
/* 61 */     return this.fieldInfo.name.compareTo(other.fieldInfo.name);
/*    */   }
/*    */ 
/*    */   void finish()
/*    */   {
/* 66 */     if ((this.fieldInfo.isIndexed) && (!this.fieldInfo.omitNorms)) {
/* 67 */       if (this.docIDs.length <= this.upto) {
/* 68 */         assert (this.docIDs.length == this.upto);
/* 69 */         this.docIDs = ArrayUtil.grow(this.docIDs, 1 + this.upto);
/*    */       }
/* 71 */       if (this.norms.length <= this.upto) {
/* 72 */         assert (this.norms.length == this.upto);
/* 73 */         this.norms = ArrayUtil.grow(this.norms, 1 + this.upto);
/*    */       }
/* 75 */       float norm = this.docState.similarity.computeNorm(this.fieldInfo.name, this.fieldState);
/* 76 */       this.norms[this.upto] = this.docState.similarity.encodeNormValue(norm);
/* 77 */       this.docIDs[this.upto] = this.docState.docID;
/* 78 */       this.upto += 1;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.NormsWriterPerField
 * JD-Core Version:    0.6.0
 */