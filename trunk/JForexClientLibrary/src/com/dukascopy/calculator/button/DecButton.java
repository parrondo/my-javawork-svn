/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Dec;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ 
/*    */ public class DecButton extends EqualsButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DecButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Dec());
/* 19 */     setText();
/* 20 */     setShortcut(getPobject().shortcut());
/* 21 */     setTextSize();
/* 22 */     this.changeBase = EqualsButton.ChangeBase.DECIMAL;
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.DecButton
 * JD-Core Version:    0.6.0
 */