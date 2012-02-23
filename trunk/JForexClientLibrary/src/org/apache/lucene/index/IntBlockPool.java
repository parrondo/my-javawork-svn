/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ final class IntBlockPool
/*    */ {
/* 22 */   public int[][] buffers = new int[10][];
/*    */ 
/* 24 */   int bufferUpto = -1;
/* 25 */   public int intUpto = 8192;
/*    */   public int[] buffer;
/* 28 */   public int intOffset = -8192;
/*    */   private final DocumentsWriter docWriter;
/*    */ 
/*    */   public IntBlockPool(DocumentsWriter docWriter)
/*    */   {
/* 33 */     this.docWriter = docWriter;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 37 */     if (this.bufferUpto != -1) {
/* 38 */       if (this.bufferUpto > 0)
/*    */       {
/* 40 */         this.docWriter.recycleIntBlocks(this.buffers, 1, 1 + this.bufferUpto);
/*    */       }
/*    */ 
/* 43 */       this.bufferUpto = 0;
/* 44 */       this.intUpto = 0;
/* 45 */       this.intOffset = 0;
/* 46 */       this.buffer = this.buffers[0];
/*    */     }
/*    */   }
/*    */ 
/*    */   public void nextBuffer() {
/* 51 */     if (1 + this.bufferUpto == this.buffers.length) {
/* 52 */       int[][] newBuffers = new int[(int)(this.buffers.length * 1.5D)][];
/* 53 */       System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
/* 54 */       this.buffers = newBuffers;
/*    */     }
/* 56 */     this.buffer = (this.buffers[(1 + this.bufferUpto)] =  = this.docWriter.getIntBlock());
/* 57 */     this.bufferUpto += 1;
/*    */ 
/* 59 */     this.intUpto = 0;
/* 60 */     this.intOffset += 8192;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IntBlockPool
 * JD-Core Version:    0.6.0
 */