/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Graph;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class GraphButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public GraphButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 16 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 17 */     setPobject(new Graph());
/* 18 */     setText();
/* 19 */     setTextSize();
/* 20 */     addActionListener(this);
/*    */ 
/* 22 */     setShortcut('G');
/* 23 */     setToolTipKey("sc.calculator.use.to.display.a.graph");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 33 */     this.mainCalculatorPanel.displayGraph();
/* 34 */     getMainCalculatorPanel().setShift(false);
/* 35 */     getMainCalculatorPanel().updateDisplay(false, true);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.GraphButton
 * JD-Core Version:    0.6.0
 */