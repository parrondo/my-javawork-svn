/*    */ package org.eclipse.jdt.internal.compiler.flow;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
/*    */ 
/*    */ public class InsideSubRoutineFlowContext extends FlowContext
/*    */ {
/*    */   public UnconditionalFlowInfo initsOnReturn;
/*    */ 
/*    */   public InsideSubRoutineFlowContext(FlowContext parent, ASTNode associatedNode)
/*    */   {
/* 27 */     super(parent, associatedNode);
/* 28 */     this.initsOnReturn = FlowInfo.DEAD_END;
/*    */   }
/*    */ 
/*    */   public String individualToString() {
/* 32 */     StringBuffer buffer = new StringBuffer("Inside SubRoutine flow context");
/* 33 */     buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
/* 34 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public UnconditionalFlowInfo initsOnReturn() {
/* 38 */     return this.initsOnReturn;
/*    */   }
/*    */ 
/*    */   public boolean isNonReturningContext() {
/* 42 */     return ((SubRoutineStatement)this.associatedNode).isSubRoutineEscaping();
/*    */   }
/*    */ 
/*    */   public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
/* 46 */     if ((flowInfo.tagBits & 0x1) == 0)
/* 47 */       if (this.initsOnReturn == FlowInfo.DEAD_END)
/* 48 */         this.initsOnReturn = ((UnconditionalFlowInfo)flowInfo.copy());
/*    */       else
/* 50 */         this.initsOnReturn = this.initsOnReturn.mergedWith(flowInfo);
/*    */   }
/*    */ 
/*    */   public SubRoutineStatement subroutine()
/*    */   {
/* 56 */     return (SubRoutineStatement)this.associatedNode;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext
 * JD-Core Version:    0.6.0
 */