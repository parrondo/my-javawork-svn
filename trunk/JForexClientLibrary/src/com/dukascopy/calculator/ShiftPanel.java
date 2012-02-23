/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.button.CalculatorButton;
/*    */ import com.dukascopy.calculator.function.ACos;
/*    */ import com.dukascopy.calculator.function.ASin;
/*    */ import com.dukascopy.calculator.function.ATan;
/*    */ import com.dukascopy.calculator.function.Conjugate;
/*    */ import com.dukascopy.calculator.function.Cube;
/*    */ import com.dukascopy.calculator.function.CubeRoot;
/*    */ import com.dukascopy.calculator.function.Exp;
/*    */ import com.dukascopy.calculator.function.Factorial;
/*    */ import com.dukascopy.calculator.function.Permutation;
/*    */ import com.dukascopy.calculator.function.Pi;
/*    */ import com.dukascopy.calculator.function.Root;
/*    */ import com.dukascopy.calculator.function.TenX;
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ShiftPanel extends AbstractCalculatorPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ShiftPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 32 */     super(applet, sbt, colour);
/* 33 */     if ((sbt != SpecialButtonType.SHIFT) && (sbt != SpecialButtonType.SHIFT_STAT))
/* 34 */       throw new RuntimeException("ShiftPanel instantiated wrongly.");
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 43 */     ((CalculatorButton)buttons().elementAt(1)).setPObject(new Factorial());
/* 44 */     ((CalculatorButton)buttons().elementAt(2)).setPObject(new ASin(AngleType.DEGREES));
/* 45 */     ((CalculatorButton)buttons().elementAt(3)).setPObject(new TenX());
/* 46 */     ((CalculatorButton)buttons().elementAt(4)).setPObject(new Permutation());
/* 47 */     ((CalculatorButton)buttons().elementAt(6)).setPObject(new Cube());
/* 48 */     ((CalculatorButton)buttons().elementAt(7)).setPObject(new ACos(AngleType.DEGREES));
/* 49 */     ((CalculatorButton)buttons().elementAt(8)).setPObject(new Exp());
/* 50 */     ((CalculatorButton)buttons().elementAt(9)).setPObject(new Conjugate());
/* 51 */     ((CalculatorButton)buttons().elementAt(11)).setPObject(new CubeRoot());
/* 52 */     ((CalculatorButton)buttons().elementAt(12)).setPObject(new ATan(AngleType.DEGREES));
/* 53 */     ((CalculatorButton)buttons().elementAt(13)).setPObject(new Root());
/* 54 */     ((CalculatorButton)buttons().elementAt(24)).setPObject(new Pi());
/* 55 */     createKeyMap();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ShiftPanel
 * JD-Core Version:    0.6.0
 */