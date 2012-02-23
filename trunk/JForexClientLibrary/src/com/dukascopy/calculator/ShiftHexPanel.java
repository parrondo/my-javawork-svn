/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.button.CalculatorButton;
/*    */ import com.dukascopy.calculator.function.Pi;
/*    */ import com.dukascopy.calculator.function.Root;
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ShiftHexPanel extends AbstractCalculatorPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ShiftHexPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 22 */     super(applet, sbt, colour);
/* 23 */     if (sbt != SpecialButtonType.SHIFT_HEX)
/* 24 */       throw new RuntimeException("ShiftHexPanel instantiated wrongly.");
/* 25 */     base(Base.HEXADECIMAL);
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 34 */     ((CalculatorButton)buttons().elementAt(24)).setPObject(new Pi());
/* 35 */     ((CalculatorButton)buttons().elementAt(13)).setPObject(new Root());
/* 36 */     createKeyMap();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ShiftHexPanel
 * JD-Core Version:    0.6.0
 */