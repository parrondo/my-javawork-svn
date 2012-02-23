/*    */ package org.eclipse.jdt.internal.compiler.parser;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*    */ 
/*    */ public class RecoveredImport extends RecoveredElement
/*    */ {
/*    */   public ImportReference importReference;
/*    */ 
/*    */   public RecoveredImport(ImportReference importReference, RecoveredElement parent, int bracketBalance)
/*    */   {
/* 23 */     super(parent, bracketBalance);
/* 24 */     this.importReference = importReference;
/*    */   }
/*    */ 
/*    */   public ASTNode parseTree()
/*    */   {
/* 30 */     return this.importReference;
/*    */   }
/*    */ 
/*    */   public int sourceEnd()
/*    */   {
/* 36 */     return this.importReference.declarationSourceEnd;
/*    */   }
/*    */   public String toString(int tab) {
/* 39 */     return tabString(tab) + "Recovered import: " + this.importReference.toString();
/*    */   }
/*    */ 
/*    */   public ImportReference updatedImportReference() {
/* 43 */     return this.importReference;
/*    */   }
/*    */   public void updateParseTree() {
/* 46 */     updatedImportReference();
/*    */   }
/*    */ 
/*    */   public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd)
/*    */   {
/* 52 */     if (this.importReference.declarationSourceEnd == 0) {
/* 53 */       this.importReference.declarationSourceEnd = bodyEnd;
/* 54 */       this.importReference.declarationEnd = bodyEnd;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredImport
 * JD-Core Version:    0.6.0
 */