/*    */ package org.eclipse.jdt.internal.compiler.env;
/*    */ 
/*    */ public class ClassSignature
/*    */ {
/*    */   char[] className;
/*    */ 
/*    */   public ClassSignature(char[] className)
/*    */   {
/* 22 */     this.className = className;
/*    */   }
/*    */ 
/*    */   public char[] getTypeName()
/*    */   {
/* 29 */     return this.className;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 33 */     StringBuffer buffer = new StringBuffer();
/* 34 */     buffer.append(this.className);
/* 35 */     buffer.append(".class");
/* 36 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.ClassSignature
 * JD-Core Version:    0.6.0
 */