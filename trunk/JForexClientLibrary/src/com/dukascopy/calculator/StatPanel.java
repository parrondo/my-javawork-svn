/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class StatPanel extends PlainPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public StatPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 19 */     super(applet, sbt, colour);
/* 20 */     if (sbt != SpecialButtonType.STAT)
/* 21 */       throw new RuntimeException("StatPanel instantiated wrongly.");
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 30 */     super.setButtons();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.StatPanel
 * JD-Core Version:    0.6.0
 */