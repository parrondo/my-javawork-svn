/*    */ package org.eclipse.jdt.internal.compiler.flow;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class InitializationFlowContext extends ExceptionHandlingFlowContext
/*    */ {
/*    */   public int exceptionCount;
/* 25 */   public TypeBinding[] thrownExceptions = new TypeBinding[5];
/* 26 */   public ASTNode[] exceptionThrowers = new ASTNode[5];
/* 27 */   public FlowInfo[] exceptionThrowerFlowInfos = new FlowInfo[5];
/*    */   public FlowInfo initsBeforeContext;
/*    */ 
/*    */   public InitializationFlowContext(FlowContext parent, ASTNode associatedNode, FlowInfo initsBeforeContext, FlowContext initializationParent, BlockScope scope)
/*    */   {
/* 37 */     super(parent, associatedNode, Binding.NO_EXCEPTIONS, 
/* 35 */       initializationParent, 
/* 36 */       scope, 
/* 37 */       FlowInfo.DEAD_END);
/* 38 */     this.initsBeforeContext = initsBeforeContext;
/*    */   }
/*    */ 
/*    */   public void checkInitializerExceptions(BlockScope currentScope, FlowContext initializerContext, FlowInfo flowInfo)
/*    */   {
/* 45 */     for (int i = 0; i < this.exceptionCount; i++)
/* 46 */       initializerContext.checkExceptionHandlers(
/* 47 */         this.thrownExceptions[i], 
/* 48 */         this.exceptionThrowers[i], 
/* 49 */         this.exceptionThrowerFlowInfos[i], 
/* 50 */         currentScope);
/*    */   }
/*    */ 
/*    */   public String individualToString()
/*    */   {
/* 56 */     StringBuffer buffer = new StringBuffer("Initialization flow context");
/* 57 */     for (int i = 0; i < this.exceptionCount; i++) {
/* 58 */       buffer.append('[').append(this.thrownExceptions[i].readableName());
/* 59 */       buffer.append('-').append(this.exceptionThrowerFlowInfos[i].toString()).append(']');
/*    */     }
/* 61 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public void recordHandlingException(ReferenceBinding exceptionType, UnconditionalFlowInfo flowInfo, TypeBinding raisedException, ASTNode invocationSite, boolean wasMasked)
/*    */   {
/* 72 */     int size = this.thrownExceptions.length;
/* 73 */     if (this.exceptionCount == size) {
/* 74 */       System.arraycopy(
/* 75 */         this.thrownExceptions, 
/* 76 */         0, 
/* 77 */         this.thrownExceptions = new TypeBinding[size * 2], 
/* 78 */         0, 
/* 79 */         size);
/* 80 */       System.arraycopy(
/* 81 */         this.exceptionThrowers, 
/* 82 */         0, 
/* 83 */         this.exceptionThrowers = new ASTNode[size * 2], 
/* 84 */         0, 
/* 85 */         size);
/* 86 */       System.arraycopy(
/* 87 */         this.exceptionThrowerFlowInfos, 
/* 88 */         0, 
/* 89 */         this.exceptionThrowerFlowInfos = new FlowInfo[size * 2], 
/* 90 */         0, 
/* 91 */         size);
/*    */     }
/* 93 */     this.thrownExceptions[this.exceptionCount] = raisedException;
/* 94 */     this.exceptionThrowers[this.exceptionCount] = invocationSite;
/* 95 */     this.exceptionThrowerFlowInfos[(this.exceptionCount++)] = flowInfo.copy();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext
 * JD-Core Version:    0.6.0
 */