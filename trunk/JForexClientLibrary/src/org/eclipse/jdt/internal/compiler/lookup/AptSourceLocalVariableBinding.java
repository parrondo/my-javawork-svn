/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class AptSourceLocalVariableBinding extends LocalVariableBinding
/*    */ {
/*    */   public MethodBinding methodBinding;
/*    */ 
/*    */   public AptSourceLocalVariableBinding(LocalVariableBinding localVariableBinding, MethodBinding methodBinding)
/*    */   {
/* 19 */     super(localVariableBinding.name, localVariableBinding.type, localVariableBinding.modifiers, true);
/* 20 */     this.constant = localVariableBinding.constant;
/* 21 */     this.declaration = localVariableBinding.declaration;
/* 22 */     this.declaringScope = localVariableBinding.declaringScope;
/* 23 */     this.id = localVariableBinding.id;
/* 24 */     this.resolvedPosition = localVariableBinding.resolvedPosition;
/* 25 */     this.tagBits = localVariableBinding.tagBits;
/* 26 */     this.useFlag = localVariableBinding.useFlag;
/* 27 */     this.initializationCount = localVariableBinding.initializationCount;
/* 28 */     this.initializationPCs = localVariableBinding.initializationPCs;
/* 29 */     this.methodBinding = methodBinding;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding
 * JD-Core Version:    0.6.0
 */