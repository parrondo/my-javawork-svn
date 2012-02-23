/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*    */ 
/*    */ public class ImportBinding extends Binding
/*    */ {
/*    */   public char[][] compoundName;
/*    */   public boolean onDemand;
/*    */   public ImportReference reference;
/*    */   public Binding resolvedImport;
/*    */ 
/*    */   public ImportBinding(char[][] compoundName, boolean isOnDemand, Binding binding, ImportReference reference)
/*    */   {
/* 24 */     this.compoundName = compoundName;
/* 25 */     this.onDemand = isOnDemand;
/* 26 */     this.resolvedImport = binding;
/* 27 */     this.reference = reference;
/*    */   }
/*    */ 
/*    */   public final int kind()
/*    */   {
/* 34 */     return 32;
/*    */   }
/*    */   public boolean isStatic() {
/* 37 */     return (this.reference != null) && (this.reference.isStatic());
/*    */   }
/*    */   public char[] readableName() {
/* 40 */     if (this.onDemand) {
/* 41 */       return CharOperation.concat(CharOperation.concatWith(this.compoundName, '.'), ".*".toCharArray());
/*    */     }
/* 43 */     return CharOperation.concatWith(this.compoundName, '.');
/*    */   }
/*    */   public String toString() {
/* 46 */     return "import : " + new String(readableName());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ImportBinding
 * JD-Core Version:    0.6.0
 */