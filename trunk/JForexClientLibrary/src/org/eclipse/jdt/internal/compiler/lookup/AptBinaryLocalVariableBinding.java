/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class AptBinaryLocalVariableBinding extends LocalVariableBinding
/*    */ {
/*    */   AnnotationBinding[] annotationBindings;
/*    */   public MethodBinding methodBinding;
/*    */ 
/*    */   public AptBinaryLocalVariableBinding(char[] name, TypeBinding type, int modifiers, AnnotationBinding[] annotationBindings, MethodBinding methodBinding)
/*    */   {
/* 19 */     super(name, type, modifiers, true);
/* 20 */     this.annotationBindings = (annotationBindings == null ? Binding.NO_ANNOTATIONS : annotationBindings);
/* 21 */     this.methodBinding = methodBinding;
/*    */   }
/*    */ 
/*    */   public AnnotationBinding[] getAnnotations() {
/* 25 */     return this.annotationBindings;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding
 * JD-Core Version:    0.6.0
 */