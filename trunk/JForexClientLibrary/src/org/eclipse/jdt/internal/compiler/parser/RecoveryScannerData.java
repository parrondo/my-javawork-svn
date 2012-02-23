/*    */ package org.eclipse.jdt.internal.compiler.parser;
/*    */ 
/*    */ public class RecoveryScannerData
/*    */ {
/* 15 */   public int insertedTokensPtr = -1;
/*    */   public int[][] insertedTokens;
/*    */   public int[] insertedTokensPosition;
/*    */   public boolean[] insertedTokenUsed;
/* 20 */   public int replacedTokensPtr = -1;
/*    */   public int[][] replacedTokens;
/*    */   public int[] replacedTokensStart;
/*    */   public int[] replacedTokensEnd;
/*    */   public boolean[] replacedTokenUsed;
/* 26 */   public int removedTokensPtr = -1;
/*    */   public int[] removedTokensStart;
/*    */   public int[] removedTokensEnd;
/*    */   public boolean[] removedTokenUsed;
/*    */ 
/*    */   public RecoveryScannerData removeUnused()
/*    */   {
/* 32 */     if (this.insertedTokens != null) {
/* 33 */       int newInsertedTokensPtr = -1;
/* 34 */       for (int i = 0; i <= this.insertedTokensPtr; i++) {
/* 35 */         if (this.insertedTokenUsed[i] != 0) {
/* 36 */           newInsertedTokensPtr++;
/* 37 */           this.insertedTokens[newInsertedTokensPtr] = this.insertedTokens[i];
/* 38 */           this.insertedTokensPosition[newInsertedTokensPtr] = this.insertedTokensPosition[i];
/* 39 */           this.insertedTokenUsed[newInsertedTokensPtr] = this.insertedTokenUsed[i];
/*    */         }
/*    */       }
/* 42 */       this.insertedTokensPtr = newInsertedTokensPtr;
/*    */     }
/*    */ 
/* 45 */     if (this.replacedTokens != null) {
/* 46 */       int newReplacedTokensPtr = -1;
/* 47 */       for (int i = 0; i <= this.replacedTokensPtr; i++) {
/* 48 */         if (this.replacedTokenUsed[i] != 0) {
/* 49 */           newReplacedTokensPtr++;
/* 50 */           this.replacedTokens[newReplacedTokensPtr] = this.replacedTokens[i];
/* 51 */           this.replacedTokensStart[newReplacedTokensPtr] = this.replacedTokensStart[i];
/* 52 */           this.replacedTokensEnd[newReplacedTokensPtr] = this.replacedTokensEnd[i];
/* 53 */           this.replacedTokenUsed[newReplacedTokensPtr] = this.replacedTokenUsed[i];
/*    */         }
/*    */       }
/* 56 */       this.replacedTokensPtr = newReplacedTokensPtr;
/*    */     }
/* 58 */     if (this.removedTokensStart != null) {
/* 59 */       int newRemovedTokensPtr = -1;
/* 60 */       for (int i = 0; i <= this.removedTokensPtr; i++) {
/* 61 */         if (this.removedTokenUsed[i] != 0) {
/* 62 */           newRemovedTokensPtr++;
/* 63 */           this.removedTokensStart[newRemovedTokensPtr] = this.removedTokensStart[i];
/* 64 */           this.removedTokensEnd[newRemovedTokensPtr] = this.removedTokensEnd[i];
/* 65 */           this.removedTokenUsed[newRemovedTokensPtr] = this.removedTokenUsed[i];
/*    */         }
/*    */       }
/* 68 */       this.removedTokensPtr = newRemovedTokensPtr;
/*    */     }
/*    */ 
/* 71 */     return this;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveryScannerData
 * JD-Core Version:    0.6.0
 */