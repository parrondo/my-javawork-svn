/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Del;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class DelButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DelButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Del());
/* 19 */     setText();
/* 20 */     setTextSize();
/* 21 */     setShortcut('\000');
/* 22 */     addActionListener(this);
/*    */ 
/* 24 */     setToolTipKey("sc.calculator.deletes.the.last.part.of.the.expressio.you.are.typing.backspace");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 35 */     synchronized (this.mainCalculatorPanel) {
/* 36 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 37 */         getMainCalculatorPanel().setMode(getPobject());
/* 38 */         getMainCalculatorPanel().requestFocusInWindow();
/* 39 */         return;
/*    */       }
/* 41 */       if (!getMainCalculatorPanel().getOn())
/* 42 */         return;
/* 43 */       getMainCalculatorPanel().delete();
/* 44 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 45 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.DelButton
 * JD-Core Version:    0.6.0
 */