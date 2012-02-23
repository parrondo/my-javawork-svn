/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*    */ 
/*    */ public class ImportConflictBinding extends ImportBinding
/*    */ {
/*    */   public ReferenceBinding conflictingTypeBinding;
/*    */ 
/*    */   public ImportConflictBinding(char[][] compoundName, Binding methodBinding, ReferenceBinding conflictingTypeBinding, ImportReference reference)
/*    */   {
/* 20 */     super(compoundName, false, methodBinding, reference);
/* 21 */     this.conflictingTypeBinding = conflictingTypeBinding;
/*    */   }
/*    */   public char[] readableName() {
/* 24 */     return CharOperation.concatWith(this.compoundName, '.');
/*    */   }
/*    */   public String toString() {
/* 27 */     return "method import : " + new String(readableName());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ImportConflictBinding
 * JD-Core Version:    0.6.0
 */