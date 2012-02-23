/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Oct;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ 
/*    */ public class OctButton extends EqualsButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public OctButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Oct());
/* 19 */     setText();
/* 20 */     setShortcut(getPobject().shortcut());
/* 21 */     setTextSize();
/* 22 */     this.changeBase = EqualsButton.ChangeBase.OCTAL;
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.OctButton
 * JD-Core Version:    0.6.0
 */