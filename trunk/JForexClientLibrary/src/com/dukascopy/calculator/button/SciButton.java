/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.function.Sci;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class SciButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public SciButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 19 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 20 */     setPobject(new Sci());
/* 21 */     setText();
/* 22 */     setShortcut(getPobject().shortcut());
/* 23 */     setTextSize();
/* 24 */     setToolTipKey(getPobject().tooltip());
/* 25 */     addActionListener(this);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent) {
/* 29 */     synchronized (this.mainCalculatorPanel) {
/* 30 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 31 */         getMainCalculatorPanel().setMode(getPobject());
/* 32 */         getMainCalculatorPanel().requestFocusInWindow();
/* 33 */         return;
/*    */       }
/* 35 */       getMainCalculatorPanel().getNotation().toggle(1);
/*    */ 
/* 37 */       getMainCalculatorPanel().setShift(false);
/* 38 */       getMainCalculatorPanel().updateDisplay(false, true);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.SciButton
 * JD-Core Version:    0.6.0
 */