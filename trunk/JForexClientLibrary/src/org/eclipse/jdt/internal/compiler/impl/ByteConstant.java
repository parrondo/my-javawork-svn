/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class ByteConstant extends Constant
/*    */ {
/*    */   private byte value;
/*    */ 
/*    */   public static Constant fromValue(byte value)
/*    */   {
/* 18 */     return new ByteConstant(value);
/*    */   }
/*    */ 
/*    */   private ByteConstant(byte value) {
/* 22 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public byte byteValue() {
/* 26 */     return this.value;
/*    */   }
/*    */ 
/*    */   public char charValue() {
/* 30 */     return (char)this.value;
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
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   public long longValue() {
/* 46 */     return this.value;
/*    */   }
/*    */ 
/*    */   public short shortValue() {
/* 50 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String stringValue()
/*    */   {
/* 55 */     return String.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 59 */     return "(byte)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 63 */     return 3;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.ByteConstant
 * JD-Core Version:    0.6.0
 */