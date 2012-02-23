/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class StringConstant extends Constant
/*    */ {
/*    */   private String value;
/*    */ 
/*    */   public static Constant fromValue(String value)
/*    */   {
/* 18 */     return new StringConstant(value);
/*    */   }
/*    */ 
/*    */   private StringConstant(String value) {
/* 22 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public String stringValue()
/*    */   {
/* 29 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 36 */     return "(String)\"" + this.value + "\"";
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 40 */     return 11;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.StringConstant
 * JD-Core Version:    0.6.0
 */