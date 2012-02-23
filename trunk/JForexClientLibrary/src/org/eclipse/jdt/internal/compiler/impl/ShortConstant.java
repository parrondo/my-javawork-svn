/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class ShortConstant extends Constant
/*    */ {
/*    */   private short value;
/*    */ 
/*    */   public static Constant fromValue(short value)
/*    */   {
/* 18 */     return new ShortConstant(value);
/*    */   }
/*    */ 
/*    */   private ShortConstant(short value) {
/* 22 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public byte byteValue() {
/* 26 */     return (byte)this.value;
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
/*    */   public String toString()
/*    */   {
/* 60 */     return "(short)" + this.value;
/*    */   }
/*    */ 
/*    */   public int typeID() {
/* 64 */     return 4;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.ShortConstant
 * JD-Core Version:    0.6.0
 */