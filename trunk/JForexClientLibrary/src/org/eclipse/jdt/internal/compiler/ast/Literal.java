/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public abstract class Literal extends Expression
/*    */ {
/*    */   public Literal(int s, int e)
/*    */   {
/* 22 */     this.sourceStart = s;
/* 23 */     this.sourceEnd = e;
/*    */   }
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*    */   {
/* 31 */     return flowInfo;
/*    */   }
/*    */   public abstract void computeConstant();
/*    */ 
/*    */   public abstract TypeBinding literalType(BlockScope paramBlockScope);
/*    */ 
/*    */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 40 */     return output.append(source());
/*    */   }
/*    */ 
/*    */   public TypeBinding resolveType(BlockScope scope)
/*    */   {
/* 45 */     this.resolvedType = literalType(scope);
/*    */ 
/* 48 */     computeConstant();
/* 49 */     if (this.constant == null) {
/* 50 */       scope.problemReporter().constantOutOfRange(this, this.resolvedType);
/* 51 */       this.constant = Constant.NotAConstant;
/*    */     }
/* 53 */     return this.resolvedType;
/*    */   }
/*    */ 
/*    */   public abstract char[] source();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Literal
 * JD-Core Version:    0.6.0
 */