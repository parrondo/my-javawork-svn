/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ 
/*    */ public class SyntheticFieldBinding extends FieldBinding
/*    */ {
/*    */   public int index;
/*    */ 
/*    */   public SyntheticFieldBinding(char[] name, TypeBinding type, int modifiers, ReferenceBinding declaringClass, Constant constant, int index)
/*    */   {
/* 20 */     super(name, type, modifiers, declaringClass, constant);
/* 21 */     this.index = index;
/* 22 */     this.tagBits |= 25769803776L;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SyntheticFieldBinding
 * JD-Core Version:    0.6.0
 */