/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class JavadocSingleNameReference extends SingleNameReference
/*    */ {
/*    */   public int tagSourceStart;
/*    */   public int tagSourceEnd;
/*    */ 
/*    */   public JavadocSingleNameReference(char[] source, long pos, int tagStart, int tagEnd)
/*    */   {
/* 21 */     super(source, pos);
/* 22 */     this.tagSourceStart = tagStart;
/* 23 */     this.tagSourceEnd = tagEnd;
/* 24 */     this.bits |= 32768;
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope) {
/* 28 */     resolve(scope, true, scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference);
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope, boolean warn, boolean considerParamRefAsUsage)
/*    */   {
/* 36 */     LocalVariableBinding variableBinding = scope.findVariable(this.token);
/* 37 */     if ((variableBinding != null) && (variableBinding.isValidBinding()) && ((variableBinding.tagBits & 0x400) != 0L)) {
/* 38 */       this.binding = variableBinding;
/* 39 */       if (considerParamRefAsUsage) {
/* 40 */         variableBinding.useFlag = 1;
/*    */       }
/* 42 */       return;
/*    */     }
/* 44 */     if (warn)
/*    */       try {
/* 46 */         MethodScope methScope = (MethodScope)scope;
/* 47 */         scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, methScope.referenceMethod().modifiers);
/*    */       }
/*    */       catch (Exception localException) {
/* 50 */         scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, -1);
/*    */       }
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 60 */     visitor.visit(this, scope);
/* 61 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*    */   {
/* 68 */     visitor.visit(this, scope);
/* 69 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference
 * JD-Core Version:    0.6.0
 */