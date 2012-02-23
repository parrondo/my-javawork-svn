/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.function.Cplx;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class CplxButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public CplxButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 19 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 20 */     setPobject(new Cplx());
/* 21 */     setText();
/*    */ 
/* 23 */     setShortcut(getPobject().shortcut());
/* 24 */     setTextSize();
/* 25 */     addActionListener(this);
/*    */ 
/* 27 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent) {
/* 31 */     synchronized (this.mainCalculatorPanel) {
/* 32 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 33 */         getMainCalculatorPanel().setMode(getPobject());
/* 34 */         getMainCalculatorPanel().requestFocusInWindow();
/* 35 */         return;
/*    */       }
/* 37 */       getMainCalculatorPanel().getNotation().toggle(4);
/*    */ 
/* 39 */       getMainCalculatorPanel().setShift(false);
/* 40 */       getMainCalculatorPanel().updateDisplay(false, true);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.CplxButton
 * JD-Core Version:    0.6.0
 */