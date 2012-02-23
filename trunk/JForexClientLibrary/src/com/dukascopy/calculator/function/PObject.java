/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.GObject;
/*    */ 
/*    */ public abstract class PObject extends GObject
/*    */ {
/*    */   protected String ftooltip;
/*    */   protected char fshortcut;
/*    */ 
/*    */   public String name()
/*    */   {
/* 30 */     StringBuilder s = new StringBuilder();
/* 31 */     for (String t : name_array()) {
/* 32 */       s.append(t);
/*    */     }
/* 34 */     return s.toString();
/*    */   }
/*    */ 
/*    */   public String shortName()
/*    */   {
/* 42 */     StringBuilder s = new StringBuilder();
/* 43 */     for (String t : name_array()) {
/* 44 */       if (!t.equals(" "))
/* 45 */         s.append(t);
/*    */     }
/* 47 */     return s.toString();
/*    */   }
/*    */ 
/*    */   public abstract String[] name_array();
/*    */ 
/*    */   public String tooltip()
/*    */   {
/* 60 */     return this.ftooltip;
/*    */   }
/*    */ 
/*    */   public char shortcut()
/*    */   {
/* 67 */     return this.fshortcut;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.PObject
 * JD-Core Version:    0.6.0
 */