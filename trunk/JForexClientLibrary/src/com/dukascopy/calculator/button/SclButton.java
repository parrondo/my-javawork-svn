/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Scl;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class SclButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public SclButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Scl());
/* 19 */     setText();
/* 20 */     setTextSize();
/* 21 */     setShortcut('\\');
/* 22 */     addActionListener(this);
/*    */ 
/* 24 */     setToolTipKey("sc.calculator.clears.the.calculator.statistics.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 34 */     synchronized (this.mainCalculatorPanel) {
/* 35 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 36 */         getMainCalculatorPanel().setMode(0);
/* 37 */         getMainCalculatorPanel().setShift(false);
/*    */       }
/* 39 */       getMainCalculatorPanel().clearStatMemory();
/* 40 */       getMainCalculatorPanel().setShift(false);
/* 41 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 42 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.SclButton
 * JD-Core Version:    0.6.0
 */