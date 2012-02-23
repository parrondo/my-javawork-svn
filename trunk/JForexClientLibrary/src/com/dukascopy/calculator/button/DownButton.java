/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Down;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.utils.ImageHelper;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class DownButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DownButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 17 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 18 */     setPobject(new Down());
/*    */ 
/* 20 */     int size = mainCalculatorPanel.minSize();
/* 21 */     if (size < 3) size = 3;
/* 22 */     if (size > 9) size = 9;
/*    */ 
/* 24 */     setIcon(ImageHelper.getImageIcon("downarrow.png", size));
/* 25 */     addActionListener(this);
/*    */ 
/* 27 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 38 */     if (getMainCalculatorPanel().getMode() != 0)
/* 39 */       return;
/* 40 */     if (getMainCalculatorPanel().downHistory())
/* 41 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 42 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.DownButton
 * JD-Core Version:    0.6.0
 */