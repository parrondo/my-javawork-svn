/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*    */ 
/*    */ public class ImportReference extends ASTNode
/*    */ {
/*    */   public char[][] tokens;
/*    */   public long[] sourcePositions;
/*    */   public int declarationEnd;
/*    */   public int declarationSourceStart;
/*    */   public int declarationSourceEnd;
/*    */   public int modifiers;
/*    */   public Annotation[] annotations;
/*    */ 
/*    */   public ImportReference(char[][] tokens, long[] sourcePositions, boolean onDemand, int modifiers)
/*    */   {
/* 33 */     this.tokens = tokens;
/* 34 */     this.sourcePositions = sourcePositions;
/* 35 */     if (onDemand) {
/* 36 */       this.bits |= 131072;
/*    */     }
/* 38 */     this.sourceEnd = (int)(sourcePositions[(sourcePositions.length - 1)] & 0xFFFFFFFF);
/* 39 */     this.sourceStart = (int)(sourcePositions[0] >>> 32);
/* 40 */     this.modifiers = modifiers;
/*    */   }
/*    */ 
/*    */   public boolean isStatic() {
/* 44 */     return (this.modifiers & 0x8) != 0;
/*    */   }
/*    */ 
/*    */   public char[][] getImportName()
/*    */   {
/* 52 */     return this.tokens;
/*    */   }
/*    */ 
/*    */   public StringBuffer print(int indent, StringBuffer output)
/*    */   {
/* 57 */     return print(indent, output, true);
/*    */   }
/*    */ 
/*    */   public StringBuffer print(int tab, StringBuffer output, boolean withOnDemand)
/*    */   {
/* 63 */     for (int i = 0; i < this.tokens.length; i++) {
/* 64 */       if (i > 0) output.append('.');
/* 65 */       output.append(this.tokens[i]);
/*    */     }
/* 67 */     if ((withOnDemand) && ((this.bits & 0x20000) != 0)) {
/* 68 */       output.append(".*");
/*    */     }
/* 70 */     return output;
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, CompilationUnitScope scope)
/*    */   {
/* 75 */     visitor.visit(this, scope);
/* 76 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ImportReference
 * JD-Core Version:    0.6.0
 */