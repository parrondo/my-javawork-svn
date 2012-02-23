/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ public class NullPObject extends PObject
/*    */ {
/* 24 */   private static final String[] fname = { "" };
/*    */ 
/* 28 */   private static NullPObject instance = null;
/*    */ 
/*    */   private NullPObject()
/*    */   {
/* 10 */     this.ftooltip = "";
/* 11 */     this.fshortcut = '\000';
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 15 */     return fname;
/*    */   }
/*    */ 
/*    */   public static NullPObject instance() {
/* 19 */     if (instance == null)
/* 20 */       instance = new NullPObject();
/* 21 */     return instance;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.NullPObject
 * JD-Core Version:    0.6.0
 */