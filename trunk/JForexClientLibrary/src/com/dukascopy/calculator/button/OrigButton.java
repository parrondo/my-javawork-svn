/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Shift;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class OrigButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public OrigButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 18 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 19 */     setPobject(new Shift());
/* 20 */     setText();
/* 21 */     setTextSize();
/* 22 */     setShortcut(' ');
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey("sc.calculator.use.to.select.original.function.on.all.keys");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 36 */     synchronized (this.mainCalculatorPanel) {
/* 37 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 38 */         getMainCalculatorPanel().setMode(getPobject());
/* 39 */         getMainCalculatorPanel().requestFocusInWindow();
/* 40 */         return;
/*    */       }
/* 42 */       if (!getMainCalculatorPanel().getOn()) {
/* 43 */         getMainCalculatorPanel().requestFocusInWindow();
/* 44 */         return;
/*    */       }
/* 46 */       getMainCalculatorPanel().setShift(!getMainCalculatorPanel().getShift());
/* 47 */       getMainCalculatorPanel().updateDisplay(false, true);
/*    */     }
/* 49 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.OrigButton
 * JD-Core Version:    0.6.0
 */