/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ 
/*    */ public abstract class BranchStatement extends Statement
/*    */ {
/*    */   public char[] label;
/*    */   public BranchLabel targetLabel;
/*    */   public SubRoutineStatement[] subroutines;
/* 21 */   public int initStateIndex = -1;
/*    */ 
/*    */   public BranchStatement(char[] label, int sourceStart, int sourceEnd)
/*    */   {
/* 27 */     this.label = label;
/* 28 */     this.sourceStart = sourceStart;
/* 29 */     this.sourceEnd = sourceEnd;
/*    */   }
/*    */ 
/*    */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*    */   {
/* 38 */     if ((this.bits & 0x80000000) == 0) {
/* 39 */       return;
/*    */     }
/* 41 */     int pc = codeStream.position;
/*    */ 
/* 45 */     if (this.subroutines != null) {
/* 46 */       int i = 0; for (int max = this.subroutines.length; i < max; i++) {
/* 47 */         SubRoutineStatement sub = this.subroutines[i];
/* 48 */         boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, this.targetLabel, this.initStateIndex, null);
/* 49 */         if (didEscape) {
/* 50 */           codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 51 */           SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
/* 52 */           if (this.initStateIndex != -1) {
/* 53 */             codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/* 54 */             codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/*    */           }
/* 56 */           return;
/*    */         }
/*    */       }
/*    */     }
/* 60 */     codeStream.goto_(this.targetLabel);
/* 61 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 62 */     SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
/* 63 */     if (this.initStateIndex != -1) {
/* 64 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/* 65 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void resolve(BlockScope scope)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.BranchStatement
 * JD-Core Version:    0.6.0
 */