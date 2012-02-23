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
/*    */ public class BreakStatement extends BranchStatement
/*    */ {
/*    */   public BreakStatement(char[] label, int sourceStart, int e)
/*    */   {
/* 20 */     super(label, sourceStart, e);
/*    */   }
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*    */   {
/* 29 */     FlowContext targetContext = this.label == null ? 
/* 30 */       flowContext.getTargetContextForDefaultBreak() : 
/* 31 */       flowContext.getTargetContextForBreakLabel(this.label);
/*    */ 
/* 33 */     if (targetContext == null) {
/* 34 */       if (this.label == null)
/* 35 */         currentScope.problemReporter().invalidBreak(this);
/*    */       else {
/* 37 */         currentScope.problemReporter().undefinedLabel(this);
/*    */       }
/* 39 */       return flowInfo;
/*    */     }
/*    */ 
/* 42 */     this.initStateIndex = 
/* 43 */       currentScope.methodScope().recordInitializationStates(flowInfo);
/*    */ 
/* 45 */     this.targetLabel = targetContext.breakLabel();
/* 46 */     FlowContext traversedContext = flowContext;
/* 47 */     int subCount = 0;
/* 48 */     this.subroutines = new SubRoutineStatement[5];
/*    */     do
/*    */     {
/*    */       SubRoutineStatement sub;
/* 52 */       if ((sub = traversedContext.subroutine()) != null) {
/* 53 */         if (subCount == this.subroutines.length) {
/* 54 */           System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount * 2], 0, subCount);
/*    */         }
/* 56 */         this.subroutines[(subCount++)] = sub;
/* 57 */         if (sub.isSubRoutineEscaping()) {
/*    */           break;
/*    */         }
/*    */       }
/* 61 */       traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
/* 62 */       traversedContext.recordBreakTo(targetContext);
/*    */ 
/* 64 */       if ((traversedContext instanceof InsideSubRoutineFlowContext)) {
/* 65 */         ASTNode node = traversedContext.associatedNode;
/* 66 */         if ((node instanceof TryStatement)) {
/* 67 */           TryStatement tryStatement = (TryStatement)node;
/* 68 */           flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
/*    */         }
/*    */       } else {
/* 70 */         if (traversedContext != targetContext)
/*    */           continue;
/* 72 */         targetContext.recordBreakFrom(flowInfo);
/* 73 */         break;
/*    */       }
/*    */     }
/* 75 */     while ((traversedContext = traversedContext.parent) != null);
/*    */ 
/* 78 */     if (subCount != this.subroutines.length) {
/* 79 */       System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount], 0, subCount);
/*    */     }
/* 81 */     return FlowInfo.DEAD_END;
/*    */   }
/*    */ 
/*    */   public StringBuffer printStatement(int tab, StringBuffer output) {
/* 85 */     printIndent(tab, output).append("break ");
/* 86 */     if (this.label != null) output.append(this.label);
/* 87 */     return output.append(';');
/*    */   }
/*    */ 
/*    */   public void traverse(ASTVisitor visitor, BlockScope blockscope) {
/* 91 */     visitor.visit(this, blockscope);
/* 92 */     visitor.endVisit(this, blockscope);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.BreakStatement
 * JD-Core Version:    0.6.0
 */