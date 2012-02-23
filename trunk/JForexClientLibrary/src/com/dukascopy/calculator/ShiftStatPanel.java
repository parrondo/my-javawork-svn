/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.button.CalculatorButton;
/*    */ import com.dukascopy.calculator.function.Mean;
/*    */ import com.dukascopy.calculator.function.PopStDev;
/*    */ import com.dukascopy.calculator.function.StDev;
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ShiftStatPanel extends ShiftPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ShiftStatPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 23 */     super(applet, sbt, colour);
/* 24 */     if (sbt != SpecialButtonType.SHIFT_STAT)
/* 25 */       throw new RuntimeException("ShiftStatPanel instantiated wrongly.");
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 34 */     ((CalculatorButton)this.buttons.elementAt(18)).setPObject(new Mean());
/* 35 */     ((CalculatorButton)this.buttons.elementAt(23)).setPObject(new PopStDev());
/* 36 */     ((CalculatorButton)this.buttons.elementAt(28)).setPObject(new StDev());
/* 37 */     super.setButtons();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ShiftStatPanel
 * JD-Core Version:    0.6.0
 */