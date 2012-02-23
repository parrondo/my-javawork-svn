/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class LongConstant extends Constant
/*    */ {
/* 15 */   private static final LongConstant ZERO = new LongConstant(0L);
/* 16 */   private static final LongConstant MIN_VALUE = new LongConstant(-9223372036854775808L);
/*    */   private long value;
/*    */ 
/*    */   public static Constant fromValue(long value)
/*    */   {
/* 21 */     if (value == 0L)
/* 22 */       return ZERO;
/* 23 */     if (value == -9223372036854775808L) {
/* 24 */       return MIN_VALUE;
/*    */     }
/* 26 */     return new LongConstant(value);
/*    */   }
/*    */ 
/*    */   private LongConstant(long value) {
/* 30 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public byte byteValue() {
/* 34 */     return (byte)(int)this.value;
/*    */   }
/*    */ 
/*    */   public char charValue() {
/* 38 */     return (char)(int)this.value;
/*    */   }
/*    */ 
/*    */   public double doubleValue() {
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   public float floatValue() {
/* 46 */     return (float)this.value;
/*    */   }
/*    */ 
/*    */   public int intValue() {
/* 50 */     return (int)this.value;
/*    */   }
/*    */ 
/*    */   public long longValue() {
/* 54 */     return this.value;
/*    */   }
/*    */ 
/*    */   public short shortValue() {
/* 58 */     return (short)(int)this.value;
/*    */   }
/*    */ 
/*    */   public String stringValue()
/*    */   {
/* 63 */     return String.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 68 */     return "(long)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 72 */     return 7;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.LongConstant
 * JD-Core Version:    0.6.0
 */