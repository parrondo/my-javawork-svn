/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class FloatConstant extends Constant
/*    */ {
/*    */   float value;
/*    */ 
/*    */   public static Constant fromValue(float value)
/*    */   {
/* 18 */     return new FloatConstant(value);
/*    */   }
/*    */ 
/*    */   private FloatConstant(float value) {
/* 22 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public byte byteValue() {
/* 26 */     return (byte)(int)this.value;
/*    */   }
/*    */ 
/*    */   public char charValue() {
/* 30 */     return (char)(int)this.value;
/*    */   }
/*    */ 
/*    */   public double doubleValue() {
/* 34 */     return this.value;
/*    */   }
/*    */ 
/*    */   public float floatValue() {
/* 38 */     return this.value;
/*    */   }
/*    */ 
/*    */   public int intValue() {
/* 42 */     return (int)this.value;
/*    */   }
/*    */ 
/*    */   public long longValue() {
/* 46 */     return ()this.value;
/*    */   }
/*    */ 
/*    */   public short shortValue() {
/* 50 */     return (short)(int)this.value;
/*    */   }
/*    */ 
/*    */   public String stringValue() {
/* 54 */     return String.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 58 */     return "(float)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 62 */     return 9;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.FloatConstant
 * JD-Core Version:    0.6.0
 */