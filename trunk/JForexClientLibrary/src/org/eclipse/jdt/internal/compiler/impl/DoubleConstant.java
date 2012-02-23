/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class DoubleConstant extends Constant
/*    */ {
/*    */   private double value;
/*    */ 
/*    */   public static Constant fromValue(double value)
/*    */   {
/* 18 */     return new DoubleConstant(value);
/*    */   }
/*    */ 
/*    */   private DoubleConstant(double value) {
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
/* 38 */     return (float)this.value;
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
/* 58 */     if (this == NotAConstant)
/* 59 */       return "(Constant) NotAConstant";
/* 60 */     return "(double)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 64 */     return 8;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.DoubleConstant
 * JD-Core Version:    0.6.0
 */