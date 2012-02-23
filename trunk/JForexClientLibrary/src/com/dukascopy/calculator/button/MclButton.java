/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import com.dukascopy.calculator.function.Mcl;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class MclButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MclButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 18 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 19 */     setPobject(new Mcl());
/* 20 */     setText();
/* 21 */     setTextSize();
/* 22 */     setShortcut('\\');
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey("sc.calculator.clears.the.calculator.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 35 */     synchronized (this.mainCalculatorPanel) {
/* 36 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 37 */         getMainCalculatorPanel().setMode(0);
/* 38 */         getMainCalculatorPanel().setShift(false);
/*    */       }
/* 40 */       getMainCalculatorPanel().setMemory(new Complex());
/* 41 */       getMainCalculatorPanel().setShift(false);
/* 42 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 43 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.MclButton
 * JD-Core Version:    0.6.0
 */