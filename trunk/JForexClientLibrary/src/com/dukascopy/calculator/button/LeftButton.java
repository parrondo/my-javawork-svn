/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Left;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.utils.ImageHelper;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class LeftButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public LeftButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Left());
/*    */ 
/* 21 */     int size = mainCalculatorPanel.minSize();
/* 22 */     if (size < 3) size = 3;
/* 23 */     if (size > 9) size = 9;
/*    */ 
/* 25 */     setIcon(ImageHelper.getImageIcon("leftarrow.png", size));
/* 26 */     addActionListener(this);
/*    */ 
/* 28 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 39 */     if (getMainCalculatorPanel().getMode() != 0)
/* 40 */       return;
/* 41 */     getMainCalculatorPanel().left();
/* 42 */     getMainCalculatorPanel().updateDisplay(true, true);
/* 43 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.LeftButton
 * JD-Core Version:    0.6.0
 */