/*    */ package org.eclipse.jdt.internal.compiler.ast;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public abstract class NameReference extends Reference
/*    */   implements InvocationSite
/*    */ {
/*    */   public Binding binding;
/*    */   public TypeBinding actualReceiverType;
/*    */ 
/*    */   public NameReference()
/*    */   {
/* 29 */     this.bits |= 7;
/*    */   }
/*    */ 
/*    */   public FieldBinding fieldBinding()
/*    */   {
/* 35 */     return (FieldBinding)this.binding;
/*    */   }
/*    */ 
/*    */   public boolean isSuperAccess() {
/* 39 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isTypeAccess()
/*    */   {
/* 44 */     return (this.binding == null) || ((this.binding instanceof ReferenceBinding));
/*    */   }
/*    */ 
/*    */   public boolean isTypeReference() {
/* 48 */     return this.binding instanceof ReferenceBinding;
/*    */   }
/*    */ 
/*    */   public void setActualReceiverType(ReferenceBinding receiverType) {
/* 52 */     if (receiverType == null) return;
/* 53 */     this.actualReceiverType = receiverType;
/*    */   }
/*    */ 
/*    */   public void setDepth(int depth) {
/* 57 */     this.bits &= -8161;
/* 58 */     if (depth > 0)
/* 59 */       this.bits |= (depth & 0xFF) << 5;
/*    */   }
/*    */ 
/*    */   public void setFieldIndex(int index)
/*    */   {
/*    */   }
/*    */ 
/*    */   public abstract String unboundReferenceErrorName();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.NameReference
 * JD-Core Version:    0.6.0
 */