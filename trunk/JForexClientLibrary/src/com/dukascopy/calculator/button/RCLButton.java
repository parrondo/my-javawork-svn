/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.RCL;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class RCLButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public RCLButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 18 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 19 */     setPobject(new RCL());
/* 20 */     setText();
/* 21 */     setTextSize();
/* 22 */     setShortcut('R');
/* 23 */     addActionListener(this);
/*    */ 
/* 25 */     setToolTipKey("sc.calculator.use.to.recall.the.value.in.memory");
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
/* 42 */       ((RCL)getPobject()).setValue(getMainCalculatorPanel().getMemory());
/* 43 */       if (getPobject() == null)
/* 44 */         return;
/* 45 */       add(getPobject());
/* 46 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 47 */       if (getMainCalculatorPanel().getShift())
/* 48 */         getMainCalculatorPanel().setShift(false);
/*    */     }
/* 50 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.RCLButton
 * JD-Core Version:    0.6.0
 */