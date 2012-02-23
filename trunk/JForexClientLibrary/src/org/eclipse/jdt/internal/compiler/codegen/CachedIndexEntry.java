/*    */ package org.eclipse.jdt.internal.compiler.codegen;
/*    */ 
/*    */ public class CachedIndexEntry
/*    */ {
/*    */   public char[] signature;
/*    */   public int index;
/*    */ 
/*    */   public CachedIndexEntry(char[] signature, int index)
/*    */   {
/* 18 */     this.signature = signature;
/* 19 */     this.index = index;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.CachedIndexEntry
 * JD-Core Version:    0.6.0
 */