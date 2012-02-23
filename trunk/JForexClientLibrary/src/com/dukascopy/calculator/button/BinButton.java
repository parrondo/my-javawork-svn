/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Bin;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ 
/*    */ public class BinButton extends EqualsButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public BinButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 11 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 12 */     setPobject(new Bin());
/* 13 */     setText();
/* 14 */     setShortcut(getPobject().shortcut());
/* 15 */     setTextSize();
/* 16 */     this.changeBase = EqualsButton.ChangeBase.BINARY;
/* 17 */     addActionListener(this);
/*    */ 
/* 19 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.BinButton
 * JD-Core Version:    0.6.0
 */