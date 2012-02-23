/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.button.CalculatorButton;
/*    */ import com.dukascopy.calculator.function.Add;
/*    */ import com.dukascopy.calculator.function.Combination;
/*    */ import com.dukascopy.calculator.function.Cos;
/*    */ import com.dukascopy.calculator.function.Divide;
/*    */ import com.dukascopy.calculator.function.E;
/*    */ import com.dukascopy.calculator.function.I;
/*    */ import com.dukascopy.calculator.function.Inverse;
/*    */ import com.dukascopy.calculator.function.LParen;
/*    */ import com.dukascopy.calculator.function.Ln;
/*    */ import com.dukascopy.calculator.function.Log;
/*    */ import com.dukascopy.calculator.function.Multiply;
/*    */ import com.dukascopy.calculator.function.Numeral;
/*    */ import com.dukascopy.calculator.function.Power;
/*    */ import com.dukascopy.calculator.function.RParen;
/*    */ import com.dukascopy.calculator.function.Sin;
/*    */ import com.dukascopy.calculator.function.Square;
/*    */ import com.dukascopy.calculator.function.SquareRoot;
/*    */ import com.dukascopy.calculator.function.Subtract;
/*    */ import com.dukascopy.calculator.function.Tan;
/*    */ import com.dukascopy.calculator.function.Variable;
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class PlainPanel extends AbstractCalculatorPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public PlainPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 40 */     super(applet, sbt, colour);
/* 41 */     if ((sbt != SpecialButtonType.NONE) && (sbt != SpecialButtonType.STAT))
/* 42 */       throw new RuntimeException("PlainPanel instantiated wrongly.");
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 51 */     ((CalculatorButton)this.buttons.elementAt(1)).setPObject(new Inverse());
/* 52 */     ((CalculatorButton)this.buttons.elementAt(2)).setPObject(new Sin(AngleType.DEGREES));
/* 53 */     ((CalculatorButton)this.buttons.elementAt(3)).setPObject(new Log());
/* 54 */     ((CalculatorButton)this.buttons.elementAt(4)).setPObject(new Combination());
/* 55 */     ((CalculatorButton)this.buttons.elementAt(6)).setPObject(new Square());
/* 56 */     ((CalculatorButton)this.buttons.elementAt(7)).setPObject(new Cos(AngleType.DEGREES));
/* 57 */     ((CalculatorButton)this.buttons.elementAt(8)).setPObject(new Ln());
/* 58 */     ((CalculatorButton)this.buttons.elementAt(9)).setPObject(new I());
/* 59 */     ((CalculatorButton)this.buttons.elementAt(11)).setPObject(new SquareRoot());
/* 60 */     ((CalculatorButton)this.buttons.elementAt(12)).setPObject(new Tan(AngleType.DEGREES));
/* 61 */     ((CalculatorButton)this.buttons.elementAt(13)).setPObject(new Power());
/* 62 */     ((CalculatorButton)this.buttons.elementAt(14)).setPObject(new Variable('x'));
/* 63 */     ((CalculatorButton)this.buttons.elementAt(16)).setPObject(new Numeral('7'));
/* 64 */     ((CalculatorButton)this.buttons.elementAt(17)).setPObject(new Numeral('4'));
/* 65 */     ((CalculatorButton)this.buttons.elementAt(18)).setPObject(new Numeral('1'));
/* 66 */     ((CalculatorButton)this.buttons.elementAt(19)).setPObject(new Numeral('0'));
/* 67 */     ((CalculatorButton)this.buttons.elementAt(21)).setPObject(new Numeral('8'));
/* 68 */     ((CalculatorButton)this.buttons.elementAt(22)).setPObject(new Numeral('5'));
/* 69 */     ((CalculatorButton)this.buttons.elementAt(23)).setPObject(new Numeral('2'));
/* 70 */     ((CalculatorButton)this.buttons.elementAt(24)).setPObject(new Numeral('.'));
/* 71 */     ((CalculatorButton)this.buttons.elementAt(26)).setPObject(new Numeral('9'));
/* 72 */     ((CalculatorButton)this.buttons.elementAt(27)).setPObject(new Numeral('6'));
/* 73 */     ((CalculatorButton)this.buttons.elementAt(28)).setPObject(new Numeral('3'));
/* 74 */     ((CalculatorButton)this.buttons.elementAt(29)).setPObject(new E());
/* 75 */     ((CalculatorButton)this.buttons.elementAt(31)).setPObject(new LParen());
/* 76 */     ((CalculatorButton)this.buttons.elementAt(32)).setPObject(new Multiply());
/* 77 */     ((CalculatorButton)this.buttons.elementAt(33)).setPObject(new Add());
/* 78 */     ((CalculatorButton)this.buttons.elementAt(36)).setPObject(new RParen());
/* 79 */     ((CalculatorButton)this.buttons.elementAt(37)).setPObject(new Divide());
/* 80 */     ((CalculatorButton)this.buttons.elementAt(38)).setPObject(new Subtract());
/* 81 */     createKeyMap();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.PlainPanel
 * JD-Core Version:    0.6.0
 */