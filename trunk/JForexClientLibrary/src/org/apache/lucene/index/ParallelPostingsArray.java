/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import org.apache.lucene.util.ArrayUtil;
/*    */ 
/*    */ class ParallelPostingsArray
/*    */ {
/*    */   static final int BYTES_PER_POSTING = 12;
/*    */   final int size;
/*    */   final int[] textStarts;
/*    */   final int[] intStarts;
/*    */   final int[] byteStarts;
/*    */ 
/*    */   ParallelPostingsArray(int size)
/*    */   {
/* 32 */     this.size = size;
/* 33 */     this.textStarts = new int[size];
/* 34 */     this.intStarts = new int[size];
/* 35 */     this.byteStarts = new int[size];
/*    */   }
/*    */ 
/*    */   int bytesPerPosting() {
/* 39 */     return 12;
/*    */   }
/*    */ 
/*    */   ParallelPostingsArray newInstance(int size) {
/* 43 */     return new ParallelPostingsArray(size);
/*    */   }
/*    */ 
/*    */   final ParallelPostingsArray grow() {
/* 47 */     int newSize = ArrayUtil.oversize(this.size + 1, bytesPerPosting());
/* 48 */     ParallelPostingsArray newArray = newInstance(newSize);
/* 49 */     copyTo(newArray, this.size);
/* 50 */     return newArray;
/*    */   }
/*    */ 
/*    */   void copyTo(ParallelPostingsArray toArray, int numToCopy) {
/* 54 */     System.arraycopy(this.textStarts, 0, toArray.textStarts, 0, numToCopy);
/* 55 */     System.arraycopy(this.intStarts, 0, toArray.intStarts, 0, numToCopy);
/* 56 */     System.arraycopy(this.byteStarts, 0, toArray.byteStarts, 0, numToCopy);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ParallelPostingsArray
 * JD-Core Version:    0.6.0
 */