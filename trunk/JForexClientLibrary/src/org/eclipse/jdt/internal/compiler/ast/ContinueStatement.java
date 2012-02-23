/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class ContinueStatement extends BranchStatement
/*    */ {
/*    */   public ContinueStatement(char[] label, int sourceStart, int sourceEnd)
/*    */   {
/* 20 */     super(label, sourceStart, sourceEnd);
/*    */   }
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*    */   {
/* 29 */     FlowContext targetContext = this.label == null ? 
/* 30 */       flowContext.getTargetContextForDefaultContinue() : 
/* 31 */       flowContext.getTargetContextForContinueLabel(this.label);
/*    */ 
/* 33 */     if (targetContext == null) {
/* 34 */       if (this.label == null)
/* 35 */         currentScope.problemReporter().invalidContinue(this);
/*    */       else {
/* 37 */         currentScope.problemReporter().undefinedLabel(this);
/*    */       }
/* 39 */       return flowInfo;
/*    */     }
/*    */ 
/* 42 */     if (targetContext == FlowContext.NotContinuableContext) {
/* 43 */       currentScope.problemReporter().invalidContinue(this);
/* 44 */       return flowInfo;
/*    */     }
/* 46 */     this.initStateIndex = 
/* 47 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*    */ 
/* 49 */     this.targetLabel = targetContext.continueLabel();
/* 50 */     FlowContext traversedContext = flowContext;
/* 51 */     int subCount = 0;
/* 52 */     this.subroutines = new SubRoutineStatement[5];
/*    */     do
/*    */     {
/*    */       SubRoutineStatement sub;
/* 56 */       if ((sub = traversedContext.subroutine()) != null) {
/* 57 */         if (subCount == this.subroutines.length) {
/* 58 */           System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount * 2], 0, subCount);
/*    */         }
/* 60 */         this.subroutines[(subCount++)] = sub;
/* 61 */         if (sub.isSubRoutineEscaping()) {
/*    */           break;
/*    */         }
/*    */       }
/* 65 */       traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
/*    */ 
/* 67 */       if ((traversedContext instanceof InsideSubRoutineFlowContext)) {
/* 68 */         ASTNode node = traversedContext.associatedNode;
/* 69 */         if ((node instanceof TryStatement)) {
/* 70 */           TryStatement tryStatement = (TryStatement)node;
/* 71 */           flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
/*    */         }
/*    */       } else {
/* 73 */         if (traversedContext != targetContext)
/*    */           continue;
/* 75 */         targetContext.recordContinueFrom(flowContext, flowInfo);
/* 76 */         break;
/*    */       }
/*    */     }
/* 78 */     while ((traversedContext = traversedContext.parent) != null);
/*    */ 
/* 81 */     if (subCount != this.subroutines.length) {
/* 82 */       System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount], 0, subCount);
/*    */     }
/* 84 */     return FlowInfo.DEAD_END;
/*    */   }
/*    */ 
/*    */   public StringBuffer printStatement(int tab, StringBuffer output) {
/* 88 */     printIndent(tab, output).append("continue ");
/* 89 */     if (this.label != null) output.append(this.label);
/* 90 */     return output.append(';');
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 94 */     visitor.visit(this, blockScope);
/* 95 */     visitor.endVisit(this, blockScope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ContinueStatement
 * JD-Core Version:    0.6.0
 */