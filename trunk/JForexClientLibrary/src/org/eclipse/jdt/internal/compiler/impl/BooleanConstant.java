/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class BooleanConstant extends Constant
/*    */ {
/*    */   private boolean value;
/* 17 */   private static final BooleanConstant TRUE = new BooleanConstant(true);
/* 18 */   private static final BooleanConstant FALSE = new BooleanConstant(false);
/*    */ 
/*    */   public static BooleanConstant fromValue(boolean value) {
/* 21 */     return value ? TRUE : FALSE;
/*    */   }
/*    */ 
/*    */   private BooleanConstant(boolean value) {
/* 25 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public boolean booleanValue() {
/* 29 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String stringValue()
/*    */   {
/* 34 */     return String.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 38 */     return "(boolean)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 42 */     return 5;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.BooleanConstant
 * JD-Core Version:    0.6.0
 */