/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import org.apache.lucene.util.ArrayUtil;
/*    */ import org.apache.lucene.util.RamUsageEstimator;
/*    */ 
/*    */ final class CharBlockPool
/*    */ {
/* 25 */   public char[][] buffers = new char[10][];
/*    */   int numBuffer;
/* 28 */   int bufferUpto = -1;
/* 29 */   public int charUpto = 16384;
/*    */   public char[] buffer;
/* 32 */   public int charOffset = -16384;
/*    */   private final DocumentsWriter docWriter;
/*    */ 
/*    */   public CharBlockPool(DocumentsWriter docWriter)
/*    */   {
/* 36 */     this.docWriter = docWriter;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 40 */     this.docWriter.recycleCharBlocks(this.buffers, 1 + this.bufferUpto);
/* 41 */     this.bufferUpto = -1;
/* 42 */     this.charUpto = 16384;
/* 43 */     this.charOffset = -16384;
/*    */   }
/*    */ 
/*    */   public void nextBuffer() {
/* 47 */     if (1 + this.bufferUpto == this.buffers.length) {
/* 48 */       char[][] newBuffers = new char[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/*    */ 
/* 50 */       System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
/* 51 */       this.buffers = newBuffers;
/*    */     }
/* 53 */     this.buffer = (this.buffers[(1 + this.bufferUpto)] =  = this.docWriter.getCharBlock());
/* 54 */     this.bufferUpto += 1;
/*    */ 
/* 56 */     this.charUpto = 0;
/* 57 */     this.charOffset += 16384;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.CharBlockPool
 * JD-Core Version:    0.6.0
 */