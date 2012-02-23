/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import com.dukascopy.calculator.function.On;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class OnButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public OnButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 22 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 23 */     setPobject(new On());
/* 24 */     setText();
/* 25 */     setTextSize();
/* 26 */     addActionListener(this);
/*    */ 
/* 28 */     setShortcut('o');
/* 29 */     setToolTipKey("sc.calculator.switches.the.calculator.on.and.clears.the.display");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 39 */     synchronized (this.mainCalculatorPanel) {
/* 40 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 41 */         getMainCalculatorPanel().setMode(0);
/* 42 */         getMainCalculatorPanel().setShift(false);
/*    */       }
/* 44 */       getMainCalculatorPanel().setOn(true);
/* 45 */       getMainCalculatorPanel().clear();
/* 46 */       getMainCalculatorPanel().setValue(new Complex(0.0D));
/* 47 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 48 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.OnButton
 * JD-Core Version:    0.6.0
 */