/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Hex;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ 
/*    */ public class HexButton extends EqualsButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public HexButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 16 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 17 */     setPobject(new Hex());
/* 18 */     setText();
/*    */ 
/* 20 */     setShortcut(getPobject().shortcut());
/* 21 */     setTextSize();
/* 22 */     this.changeBase = EqualsButton.ChangeBase.HEXADECIMAL;
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.HexButton
 * JD-Core Version:    0.6.0
 */