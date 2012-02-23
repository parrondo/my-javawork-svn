/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*    */ 
/*    */ public class AnnotationMemberValue extends AnnotationValueImpl
/*    */ {
/*    */   private final MethodBinding _methodBinding;
/*    */ 
/*    */   public AnnotationMemberValue(BaseProcessingEnvImpl env, Object value, MethodBinding methodBinding)
/*    */   {
/* 41 */     super(env, value, methodBinding.returnType);
/* 42 */     this._methodBinding = methodBinding;
/*    */   }
/*    */ 
/*    */   public MethodBinding getMethodBinding()
/*    */   {
/* 49 */     return this._methodBinding;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue
 * JD-Core Version:    0.6.0
 */