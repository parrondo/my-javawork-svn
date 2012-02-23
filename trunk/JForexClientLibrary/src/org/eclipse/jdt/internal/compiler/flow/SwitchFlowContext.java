/*    */ package org.eclipse.jdt.internal.compiler.flow;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*    */ 
/*    */ public class SwitchFlowContext extends FlowContext
/*    */ {
/*    */   public BranchLabel breakLabel;
/* 23 */   public UnconditionalFlowInfo initsOnBreak = FlowInfo.DEAD_END;
/*    */ 
/*    */   public SwitchFlowContext(FlowContext parent, ASTNode associatedNode, BranchLabel breakLabel) {
/* 26 */     super(parent, associatedNode);
/* 27 */     this.breakLabel = breakLabel;
/*    */   }
/*    */ 
/*    */   public BranchLabel breakLabel() {
/* 31 */     return this.breakLabel;
/*    */   }
/*    */ 
/*    */   public String individualToString() {
/* 35 */     StringBuffer buffer = new StringBuffer("Switch flow context");
/* 36 */     buffer.append("[initsOnBreak -").append(this.initsOnBreak.toString()).append(']');
/* 37 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public boolean isBreakable() {
/* 41 */     return true;
/*    */   }
/*    */ 
/*    */   public void recordBreakFrom(FlowInfo flowInfo) {
/* 45 */     if ((this.initsOnBreak.tagBits & 0x1) == 0) {
/* 46 */       this.initsOnBreak = this.initsOnBreak.mergedWith(flowInfo.unconditionalInits());
/*    */     }
/*    */     else
/* 49 */       this.initsOnBreak = flowInfo.unconditionalCopy();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext
 * JD-Core Version:    0.6.0
 */