/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.button.CalculatorButton;
/*    */ import com.dukascopy.calculator.function.Add;
/*    */ import com.dukascopy.calculator.function.And;
/*    */ import com.dukascopy.calculator.function.Divide;
/*    */ import com.dukascopy.calculator.function.E;
/*    */ import com.dukascopy.calculator.function.LParen;
/*    */ import com.dukascopy.calculator.function.Multiply;
/*    */ import com.dukascopy.calculator.function.Numeral;
/*    */ import com.dukascopy.calculator.function.Or;
/*    */ import com.dukascopy.calculator.function.Power;
/*    */ import com.dukascopy.calculator.function.RParen;
/*    */ import com.dukascopy.calculator.function.Subtract;
/*    */ import com.dukascopy.calculator.function.Xor;
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class HexPanel extends AbstractCalculatorPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public HexPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*    */   {
/* 31 */     super(applet, sbt, colour);
/* 32 */     if (sbt != SpecialButtonType.HEX)
/* 33 */       throw new RuntimeException("HexPanel instantiated wrongly.");
/* 34 */     base(Base.HEXADECIMAL);
/*    */   }
/*    */ 
/*    */   protected void setButtons()
/*    */   {
/* 43 */     ((CalculatorButton)this.buttons.elementAt(1)).setPObject(new Numeral('A'));
/* 44 */     ((CalculatorButton)this.buttons.elementAt(2)).setPObject(new Numeral('D'));
/* 45 */     ((CalculatorButton)this.buttons.elementAt(4)).setPObject(new And());
/* 46 */     ((CalculatorButton)this.buttons.elementAt(6)).setPObject(new Numeral('B'));
/* 47 */     ((CalculatorButton)this.buttons.elementAt(7)).setPObject(new Numeral('E'));
/* 48 */     ((CalculatorButton)this.buttons.elementAt(9)).setPObject(new Or());
/* 49 */     ((CalculatorButton)this.buttons.elementAt(11)).setPObject(new Numeral('C'));
/* 50 */     ((CalculatorButton)this.buttons.elementAt(12)).setPObject(new Numeral('F'));
/* 51 */     ((CalculatorButton)this.buttons.elementAt(13)).setPObject(new Power());
/* 52 */     ((CalculatorButton)this.buttons.elementAt(14)).setPObject(new Xor());
/* 53 */     ((CalculatorButton)this.buttons.elementAt(16)).setPObject(new Numeral('7'));
/* 54 */     ((CalculatorButton)this.buttons.elementAt(17)).setPObject(new Numeral('4'));
/* 55 */     ((CalculatorButton)this.buttons.elementAt(18)).setPObject(new Numeral('1'));
/* 56 */     ((CalculatorButton)this.buttons.elementAt(19)).setPObject(new Numeral('0'));
/* 57 */     ((CalculatorButton)this.buttons.elementAt(21)).setPObject(new Numeral('8'));
/* 58 */     ((CalculatorButton)this.buttons.elementAt(22)).setPObject(new Numeral('5'));
/* 59 */     ((CalculatorButton)this.buttons.elementAt(23)).setPObject(new Numeral('2'));
/* 60 */     ((CalculatorButton)this.buttons.elementAt(24)).setPObject(new Numeral('.'));
/* 61 */     ((CalculatorButton)this.buttons.elementAt(26)).setPObject(new Numeral('9'));
/* 62 */     ((CalculatorButton)this.buttons.elementAt(27)).setPObject(new Numeral('6'));
/* 63 */     ((CalculatorButton)this.buttons.elementAt(28)).setPObject(new Numeral('3'));
/* 64 */     ((CalculatorButton)this.buttons.elementAt(29)).setPObject(new E());
/* 65 */     ((CalculatorButton)this.buttons.elementAt(31)).setPObject(new LParen());
/* 66 */     ((CalculatorButton)this.buttons.elementAt(32)).setPObject(new Multiply());
/* 67 */     ((CalculatorButton)this.buttons.elementAt(33)).setPObject(new Add());
/* 68 */     ((CalculatorButton)this.buttons.elementAt(36)).setPObject(new RParen());
/* 69 */     ((CalculatorButton)this.buttons.elementAt(37)).setPObject(new Divide());
/* 70 */     ((CalculatorButton)this.buttons.elementAt(38)).setPObject(new Subtract());
/* 71 */     createKeyMap();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.HexPanel
 * JD-Core Version:    0.6.0
 */