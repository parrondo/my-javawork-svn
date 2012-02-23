/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*    */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public abstract class Reference extends Expression
/*    */ {
/*    */   public abstract FlowInfo analyseAssignment(BlockScope paramBlockScope, FlowContext paramFlowContext, FlowInfo paramFlowInfo, Assignment paramAssignment, boolean paramBoolean);
/*    */ 
/*    */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*    */   {
/* 34 */     return flowInfo;
/*    */   }
/*    */ 
/*    */   public FieldBinding fieldBinding()
/*    */   {
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   public void fieldStore(Scope currentScope, CodeStream codeStream, FieldBinding fieldBinding, MethodBinding syntheticWriteAccessor, TypeBinding receiverType, boolean isImplicitThisReceiver, boolean valueRequired) {
/* 44 */     int pc = codeStream.position;
/* 45 */     if (fieldBinding.isStatic()) {
/* 46 */       if (valueRequired) {
/* 47 */         switch (fieldBinding.type.id) {
/*    */         case 7:
/*    */         case 8:
/* 50 */           codeStream.dup2();
/* 51 */           break;
/*    */         default:
/* 53 */           codeStream.dup();
/*    */         }
/*    */       }
/*    */ 
/* 57 */       if (syntheticWriteAccessor == null) {
/* 58 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
/* 59 */         codeStream.fieldAccess(-77, fieldBinding, constantPoolDeclaringClass);
/*    */       } else {
/* 61 */         codeStream.invoke(-72, syntheticWriteAccessor, null);
/*    */       }
/*    */     } else {
/* 64 */       if (valueRequired) {
/* 65 */         switch (fieldBinding.type.id) {
/*    */         case 7:
/*    */         case 8:
/* 68 */           codeStream.dup2_x1();
/* 69 */           break;
/*    */         default:
/* 71 */           codeStream.dup_x1();
/*    */         }
/*    */       }
/*    */ 
/* 75 */       if (syntheticWriteAccessor == null) {
/* 76 */         TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
/* 77 */         codeStream.fieldAccess(-75, fieldBinding, constantPoolDeclaringClass);
/*    */       } else {
/* 79 */         codeStream.invoke(-72, syntheticWriteAccessor, null);
/*    */       }
/*    */     }
/* 82 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*    */   }
/*    */ 
/*    */   public abstract void generateAssignment(BlockScope paramBlockScope, CodeStream paramCodeStream, Assignment paramAssignment, boolean paramBoolean);
/*    */ 
/*    */   public abstract void generateCompoundAssignment(BlockScope paramBlockScope, CodeStream paramCodeStream, Expression paramExpression, int paramInt1, int paramInt2, boolean paramBoolean);
/*    */ 
/*    */   public abstract void generatePostIncrement(BlockScope paramBlockScope, CodeStream paramCodeStream, CompoundAssignment paramCompoundAssignment, boolean paramBoolean);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Reference
 * JD-Core Version:    0.6.0
 */