/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import org.apache.lucene.util.ArrayUtil;
/*    */ 
/*    */ public abstract class BaseCharFilter extends CharFilter
/*    */ {
/*    */   private int[] offsets;
/*    */   private int[] diffs;
/* 32 */   private int size = 0;
/*    */ 
/*    */   public BaseCharFilter(CharStream in) {
/* 35 */     super(in);
/*    */   }
/*    */ 
/*    */   protected int correct(int currentOff)
/*    */   {
/* 41 */     if ((this.offsets == null) || (currentOff < this.offsets[0])) {
/* 42 */       return currentOff;
/*    */     }
/*    */ 
/* 45 */     int hi = this.size - 1;
/* 46 */     if (currentOff >= this.offsets[hi]) {
/* 47 */       return currentOff + this.diffs[hi];
/*    */     }
/* 49 */     int lo = 0;
/* 50 */     int mid = -1;
/*    */ 
/* 52 */     while (hi >= lo) {
/* 53 */       mid = lo + hi >>> 1;
/* 54 */       if (currentOff < this.offsets[mid]) {
/* 55 */         hi = mid - 1; continue;
/* 56 */       }if (currentOff > this.offsets[mid]) {
/* 57 */         lo = mid + 1; continue;
/*    */       }
/* 59 */       return currentOff + this.diffs[mid];
/*    */     }
/*    */ 
/* 62 */     if (currentOff < this.offsets[mid]) {
/* 63 */       return mid == 0 ? currentOff : currentOff + this.diffs[(mid - 1)];
/*    */     }
/* 65 */     return currentOff + this.diffs[mid];
/*    */   }
/*    */ 
/*    */   protected int getLastCumulativeDiff() {
/* 69 */     return this.offsets == null ? 0 : this.diffs[(this.size - 1)];
/*    */   }
/*    */ 
/*    */   protected void addOffCorrectMap(int off, int cumulativeDiff)
/*    */   {
/* 74 */     if (this.offsets == null) {
/* 75 */       this.offsets = new int[64];
/* 76 */       this.diffs = new int[64];
/* 77 */     } else if (this.size == this.offsets.length) {
/* 78 */       this.offsets = ArrayUtil.grow(this.offsets);
/* 79 */       this.diffs = ArrayUtil.grow(this.diffs);
/*    */     }
/*    */ 
/* 82 */     this.offsets[this.size] = off;
/* 83 */     this.diffs[(this.size++)] = cumulativeDiff;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.BaseCharFilter
 * JD-Core Version:    0.6.0
 */