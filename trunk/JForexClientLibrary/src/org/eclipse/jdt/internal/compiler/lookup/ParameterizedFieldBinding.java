/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ 
/*    */ public class ParameterizedFieldBinding extends FieldBinding
/*    */ {
/*    */   public FieldBinding originalField;
/*    */ 
/*    */   public ParameterizedFieldBinding(ParameterizedTypeBinding parameterizedDeclaringClass, FieldBinding originalField)
/*    */   {
/* 36 */     super(originalField.name, 
/* 31 */       (originalField.modifiers & 0x8) != 0 ? 
/* 32 */       originalField.type : (originalField.modifiers & 0x4000) != 0 ? 
/* 30 */       parameterizedDeclaringClass : 
/* 33 */       Scope.substitute(parameterizedDeclaringClass, originalField.type), 
/* 34 */       originalField.modifiers, 
/* 35 */       parameterizedDeclaringClass, 
/* 36 */       null);
/* 37 */     this.originalField = originalField;
/* 38 */     this.tagBits = originalField.tagBits;
/* 39 */     this.id = originalField.id;
/*    */   }
/*    */ 
/*    */   public Constant constant()
/*    */   {
/* 46 */     return this.originalField.constant();
/*    */   }
/*    */ 
/*    */   public FieldBinding original()
/*    */   {
/* 53 */     return this.originalField.original();
/*    */   }
/*    */ 
/*    */   public void setConstant(Constant constant)
/*    */   {
/* 60 */     this.originalField.setConstant(constant);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ParameterizedFieldBinding
 * JD-Core Version:    0.6.0
 */