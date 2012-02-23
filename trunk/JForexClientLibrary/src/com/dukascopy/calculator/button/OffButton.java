/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Off;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class OffButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public OffButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 19 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 20 */     setPobject(new Off());
/* 21 */     setText();
/* 22 */     setTextSize();
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setShortcut('Q');
/* 26 */     setToolTipKey("sc.calculator.switch.off");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 36 */     synchronized (this.mainCalculatorPanel) {
/* 37 */       getMainCalculatorPanel().setOn(false);
/* 38 */       getMainCalculatorPanel().clearHistory();
/* 39 */       getMainCalculatorPanel().setShift(false);
/* 40 */       getMainCalculatorPanel().updateDisplay(true, true);
/*    */     }
/* 42 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.OffButton
 * JD-Core Version:    0.6.0
 */