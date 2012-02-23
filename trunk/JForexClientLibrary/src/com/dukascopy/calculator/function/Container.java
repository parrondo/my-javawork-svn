/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public abstract class Container extends PObject
/*    */ {
/*    */   protected double d;
/*    */   protected OObject c;
/*    */   protected boolean error;
/*    */ 
/*    */   public Container()
/*    */   {
/* 18 */     this.error = false;
/* 19 */     this.d = new Double(0.0D).doubleValue();
/* 20 */     this.c = new Complex();
/*    */   }
/*    */ 
/*    */   public double dvalue()
/*    */   {
/* 28 */     return this.d;
/*    */   }
/*    */ 
/*    */   public OObject value()
/*    */   {
/* 35 */     return this.c;
/*    */   }
/*    */ 
/*    */   public boolean error()
/*    */   {
/* 44 */     return this.error;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Container
 * JD-Core Version:    0.6.0
 */