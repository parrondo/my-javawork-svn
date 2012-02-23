/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Mode;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class ModeButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ModeButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Mode());
/* 19 */     setText();
/* 20 */     setTextSize();
/* 21 */     addActionListener(this);
/*    */ 
/* 23 */     setShortcut('?');
/* 24 */     setToolTipKey("sc.calculator.change.mode");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 36 */     synchronized (this.mainCalculatorPanel) {
/* 37 */       if (getMainCalculatorPanel().getOn()) {
/* 38 */         getMainCalculatorPanel().setMode(getMainCalculatorPanel().getMode() + 1);
/* 39 */         getMainCalculatorPanel().setShift(false);
/* 40 */         getMainCalculatorPanel().updateDisplay(true, true);
/*    */       }
/* 42 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.ModeButton
 * JD-Core Version:    0.6.0
 */