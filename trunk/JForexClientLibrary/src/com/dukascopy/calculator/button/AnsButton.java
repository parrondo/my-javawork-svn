/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.Error;
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Ans;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class AnsButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public AnsButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 11 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 12 */     setPobject(new Ans());
/* 13 */     setText();
/* 14 */     setTextSize();
/* 15 */     addActionListener(this);
/*    */ 
/* 17 */     setShortcut('a');
/* 18 */     setToolTipKey("sc.calculator.use.to.calculate.with.the.last.result.you.found");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent) {
/* 22 */     synchronized (this.mainCalculatorPanel) {
/* 23 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 24 */         getMainCalculatorPanel().setMode(getPobject());
/* 25 */         getMainCalculatorPanel().requestFocusInWindow();
/* 26 */         return;
/*    */       }
/* 28 */       if (!(getMainCalculatorPanel().getValue() instanceof Error)) {
/* 29 */         ((Ans)getPobject()).setValue(getMainCalculatorPanel().getValue());
/*    */       }
/* 31 */       if (getPobject() == null) {
/* 32 */         return;
/*    */       }
/* 34 */       add(getPobject());
/* 35 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 36 */       if (getMainCalculatorPanel().getShift()) {
/* 37 */         getMainCalculatorPanel().setShift(false);
/*    */       }
/* 39 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.AnsButton
 * JD-Core Version:    0.6.0
 */