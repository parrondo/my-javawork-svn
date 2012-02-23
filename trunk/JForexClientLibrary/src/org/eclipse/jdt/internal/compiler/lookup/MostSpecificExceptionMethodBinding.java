/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class MostSpecificExceptionMethodBinding extends MethodBinding
/*    */ {
/*    */   private MethodBinding originalMethod;
/*    */ 
/*    */   public MostSpecificExceptionMethodBinding(MethodBinding originalMethod, ReferenceBinding[] mostSpecificExceptions)
/*    */   {
/* 28 */     super(originalMethod.modifiers, 
/* 24 */       originalMethod.selector, 
/* 25 */       originalMethod.returnType, 
/* 26 */       originalMethod.parameters, 
/* 27 */       mostSpecificExceptions, 
/* 28 */       originalMethod.declaringClass);
/* 29 */     this.originalMethod = originalMethod;
/*    */   }
/*    */ 
/*    */   public MethodBinding original() {
/* 33 */     return this.originalMethod.original();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MostSpecificExceptionMethodBinding
 * JD-Core Version:    0.6.0
 */