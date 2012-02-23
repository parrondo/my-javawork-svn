/*    */ package org.eclipse.jdt.internal.compiler.flow;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*    */ 
/*    */ public class LabelFlowContext extends SwitchFlowContext
/*    */ {
/*    */   public char[] labelName;
/*    */ 
/*    */   public LabelFlowContext(FlowContext parent, ASTNode associatedNode, char[] labelName, BranchLabel breakLabel, BlockScope scope)
/*    */   {
/* 27 */     super(parent, associatedNode, breakLabel);
/* 28 */     this.labelName = labelName;
/* 29 */     checkLabelValidity(scope);
/*    */   }
/*    */ 
/*    */   void checkLabelValidity(BlockScope scope)
/*    */   {
/* 34 */     FlowContext current = this.parent;
/* 35 */     while (current != null)
/*    */     {
/*    */       char[] currentLabelName;
/* 37 */       if (((currentLabelName = current.labelName()) != null) && 
/* 38 */         (CharOperation.equals(currentLabelName, this.labelName))) {
/* 39 */         scope.problemReporter().alreadyDefinedLabel(this.labelName, this.associatedNode);
/*    */       }
/* 41 */       current = current.parent;
/*    */     }
/*    */   }
/*    */ 
/*    */   public String individualToString() {
/* 46 */     return "Label flow context [label:" + String.valueOf(this.labelName) + "]";
/*    */   }
/*    */ 
/*    */   public char[] labelName() {
/* 50 */     return this.labelName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.LabelFlowContext
 * JD-Core Version:    0.6.0
 */