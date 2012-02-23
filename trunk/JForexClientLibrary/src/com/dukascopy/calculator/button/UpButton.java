/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.function.Up;
/*    */ import com.dukascopy.calculator.utils.ImageHelper;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class UpButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public UpButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 14 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 15 */     setPobject(new Up());
/*    */ 
/* 18 */     setToolTipKey(getPobject().tooltip());
/* 19 */     int size = mainCalculatorPanel.minSize();
/* 20 */     if (size < 3) size = 3;
/* 21 */     if (size > 9) size = 9;
/* 22 */     setIcon(ImageHelper.getImageIcon("uparrow.png", size));
/* 23 */     addActionListener(this);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 35 */     if (getMainCalculatorPanel().getMode() != 0)
/* 36 */       return;
/* 37 */     getMainCalculatorPanel().upHistory();
/* 38 */     getMainCalculatorPanel().updateDisplay(true, true);
/* 39 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.UpButton
 * JD-Core Version:    0.6.0
 */