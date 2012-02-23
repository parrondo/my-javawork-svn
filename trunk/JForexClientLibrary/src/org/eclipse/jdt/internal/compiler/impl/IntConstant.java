/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class IntConstant extends Constant
/*    */ {
/*    */   int value;
/* 17 */   private static final IntConstant MIN_VALUE = new IntConstant(-2147483648);
/* 18 */   private static final IntConstant MINUS_FOUR = new IntConstant(-4);
/* 19 */   private static final IntConstant MINUS_THREE = new IntConstant(-3);
/* 20 */   private static final IntConstant MINUS_TWO = new IntConstant(-2);
/* 21 */   private static final IntConstant MINUS_ONE = new IntConstant(-1);
/* 22 */   private static final IntConstant ZERO = new IntConstant(0);
/* 23 */   private static final IntConstant ONE = new IntConstant(1);
/* 24 */   private static final IntConstant TWO = new IntConstant(2);
/* 25 */   private static final IntConstant THREE = new IntConstant(3);
/* 26 */   private static final IntConstant FOUR = new IntConstant(4);
/* 27 */   private static final IntConstant FIVE = new IntConstant(5);
/* 28 */   private static final IntConstant SIX = new IntConstant(6);
/* 29 */   private static final IntConstant SEVEN = new IntConstant(7);
/* 30 */   private static final IntConstant EIGHT = new IntConstant(8);
/* 31 */   private static final IntConstant NINE = new IntConstant(9);
/* 32 */   private static final IntConstant TEN = new IntConstant(10);
/*    */ 
/*    */   public static Constant fromValue(int value) {
/* 35 */     switch (value) { case -2147483648:
/* 36 */       return MIN_VALUE;
/*    */     case -4:
/* 37 */       return MINUS_FOUR;
/*    */     case -3:
/* 38 */       return MINUS_THREE;
/*    */     case -2:
/* 39 */       return MINUS_TWO;
/*    */     case -1:
/* 40 */       return MINUS_ONE;
/*    */     case 0:
/* 41 */       return ZERO;
/*    */     case 1:
/* 42 */       return ONE;
/*    */     case 2:
/* 43 */       return TWO;
/*    */     case 3:
/* 44 */       return THREE;
/*    */     case 4:
/* 45 */       return FOUR;
/*    */     case 5:
/* 46 */       return FIVE;
/*    */     case 6:
/* 47 */       return SIX;
/*    */     case 7:
/* 48 */       return SEVEN;
/*    */     case 8:
/* 49 */       return EIGHT;
/*    */     case 9:
/* 50 */       return NINE;
/*    */     case 10:
/* 51 */       return TEN;
/*    */     }
/* 53 */     return new IntConstant(value);
/*    */   }
/*    */ 
/*    */   private IntConstant(int value) {
/* 57 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public byte byteValue() {
/* 61 */     return (byte)this.value;
/*    */   }
/*    */ 
/*    */   public char charValue() {
/* 65 */     return (char)this.value;
/*    */   }
/*    */ 
/*    */   public double doubleValue() {
/* 69 */     return this.value;
/*    */   }
/*    */ 
/*    */   public float floatValue() {
/* 73 */     return this.value;
/*    */   }
/*    */ 
/*    */   public int intValue() {
/* 77 */     return this.value;
/*    */   }
/*    */ 
/*    */   public long longValue() {
/* 81 */     return this.value;
/*    */   }
/*    */ 
/*    */   public short shortValue() {
/* 85 */     return (short)this.value;
/*    */   }
/*    */ 
/*    */   public String stringValue()
/*    */   {
/* 90 */     return String.valueOf(this.value);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 94 */     return "(int)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 98 */     return 10;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.IntConstant
 * JD-Core Version:    0.6.0
 */