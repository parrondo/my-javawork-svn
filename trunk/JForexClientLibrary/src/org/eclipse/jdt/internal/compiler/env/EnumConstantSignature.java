/*    */ package org.eclipse.jdt.internal.compiler.env;
/*    */ 
/*    */ public class EnumConstantSignature
/*    */ {
/*    */   char[] typeName;
/*    */   char[] constName;
/*    */ 
/*    */   public EnumConstantSignature(char[] typeName, char[] constName)
/*    */   {
/* 23 */     this.typeName = typeName;
/* 24 */     this.constName = constName;
/*    */   }
/*    */ 
/*    */   public char[] getTypeName()
/*    */   {
/* 31 */     return this.typeName;
/*    */   }
/*    */ 
/*    */   public char[] getEnumConstantName()
/*    */   {
/* 38 */     return this.constName;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 42 */     StringBuffer buffer = new StringBuffer();
/* 43 */     buffer.append(this.typeName);
/* 44 */     buffer.append('.');
/* 45 */     buffer.append(this.constName);
/* 46 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.EnumConstantSignature
 * JD-Core Version:    0.6.0
 */