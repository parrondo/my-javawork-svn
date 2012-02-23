/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class EmptyStatement extends Statement
/*    */ {
/*    */   public EmptyStatement(int startPosition, int endPosition)
/*    */   {
/* 23 */     this.sourceStart = startPosition;
/* 24 */     this.sourceEnd = endPosition;
/*    */   }
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/* 28 */     return flowInfo;
/*    */   }
/*    */ 
/*    */   public int complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, int complaintLevel)
/*    */   {
/* 34 */     if (scope.compilerOptions().complianceLevel < 3145728L) {
/* 35 */       return complaintLevel;
/*    */     }
/* 37 */     return super.complainIfUnreachable(flowInfo, scope, complaintLevel);
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*    */   {
/*    */   }
/*    */ 
/*    */   public StringBuffer printStatement(int tab, StringBuffer output) {
/* 45 */     return printIndent(tab, output).append(';');
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope) {
/* 49 */     if ((this.bits & 0x1) == 0)
/* 50 */       scope.problemReporter().superfluousSemicolon(this.sourceStart, this.sourceEnd);
/*    */     else
/* 52 */       scope.problemReporter().emptyControlFlowStatement(this.sourceStart, this.sourceEnd);
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*    */   {
/* 57 */     visitor.visit(this, scope);
/* 58 */     visitor.endVisit(this, scope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.EmptyStatement
 * JD-Core Version:    0.6.0
 */