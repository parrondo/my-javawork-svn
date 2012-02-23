/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class ThrowStatement extends Statement
/*    */ {
/*    */   public Expression exception;
/*    */   public TypeBinding exceptionType;
/*    */ 
/*    */   public ThrowStatement(Expression exception, int sourceStart, int sourceEnd)
/*    */   {
/* 28 */     this.exception = exception;
/* 29 */     this.sourceStart = sourceStart;
/* 30 */     this.sourceEnd = sourceEnd;
/*    */   }
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/* 34 */     this.exception.analyseCode(currentScope, flowContext, flowInfo);
/* 35 */     this.exception.checkNPE(currentScope, flowContext, flowInfo);
/*    */ 
/* 37 */     flowContext.checkExceptionHandlers(this.exceptionType, this, flowInfo, currentScope);
/* 38 */     return FlowInfo.DEAD_END;
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*    */   {
/* 48 */     if ((this.bits & 0x80000000) == 0)
/* 49 */       return;
/* 50 */     int pc = codeStream.position;
/* 51 */     this.exception.generateCode(currentScope, codeStream, true);
/* 52 */     codeStream.athrow();
/* 53 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */ 
/*    */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 57 */     printIndent(indent, output).append("throw ");
/* 58 */     this.exception.printExpression(0, output);
/* 59 */     return output.append(';');
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope) {
/* 63 */     this.exceptionType = this.exception.resolveType(scope);
/* 64 */     if ((this.exceptionType != null) && (this.exceptionType.isValidBinding())) {
/* 65 */       if (this.exceptionType == TypeBinding.NULL) {
/* 66 */         if (scope.compilerOptions().complianceLevel <= 3080192L)
/*    */         {
/* 68 */           scope.problemReporter().cannotThrowNull(this.exception);
/*    */         }
/* 70 */       } else if (this.exceptionType.findSuperTypeOriginatingFrom(21, true) == null) {
/* 71 */         scope.problemReporter().cannotThrowType(this.exception, this.exceptionType);
/*    */       }
/* 73 */       this.exception.computeConversion(scope, this.exceptionType, this.exceptionType);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 78 */     if (visitor.visit(this, blockScope))
/* 79 */       this.exception.traverse(visitor, blockScope);
/* 80 */     visitor.endVisit(this, blockScope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 * JD-Core Version:    0.6.0
 */