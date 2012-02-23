/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Copy;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class CopyButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public CopyButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Copy());
/* 19 */     setText();
/* 20 */     setTextSize();
/* 21 */     setShortcut('\000');
/* 22 */     addActionListener(this);
/*    */ 
/* 24 */     setToolTipKey("sc.calculator.copy.result.to.clipboard.copy");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 34 */     synchronized (this.mainCalculatorPanel) {
/* 35 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 36 */         getMainCalculatorPanel().setMode(getPobject());
/* 37 */         getMainCalculatorPanel().requestFocusInWindow();
/* 38 */         return;
/*    */       }
/* 40 */       if (!getMainCalculatorPanel().getOn())
/* 41 */         return;
/* 42 */       getMainCalculatorPanel().copy();
/* 43 */       getMainCalculatorPanel().setShift(false);
/* 44 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 45 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.CopyButton
 * JD-Core Version:    0.6.0
 */