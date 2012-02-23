/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.function.Pol;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class PolButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public PolButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 19 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 20 */     setPobject(new Pol());
/* 21 */     setText();
/* 22 */     setShortcut(getPobject().shortcut());
/* 23 */     setTextSize();
/* 24 */     addActionListener(this);
/*    */ 
/* 26 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent) {
/* 30 */     synchronized (this.mainCalculatorPanel) {
/* 31 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 32 */         getMainCalculatorPanel().setMode(getPobject());
/* 33 */         getMainCalculatorPanel().requestFocusInWindow();
/* 34 */         return;
/*    */       }
/* 36 */       getMainCalculatorPanel().getNotation().toggle(2);
/*    */ 
/* 38 */       getMainCalculatorPanel().setShift(false);
/* 39 */       getMainCalculatorPanel().updateDisplay(false, true);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.PolButton
 * JD-Core Version:    0.6.0
 */