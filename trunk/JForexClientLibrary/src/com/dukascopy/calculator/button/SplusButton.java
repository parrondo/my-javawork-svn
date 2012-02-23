/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.Parser;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import com.dukascopy.calculator.function.AFunction;
/*    */ import com.dukascopy.calculator.function.Ans;
/*    */ import com.dukascopy.calculator.function.DFunction;
/*    */ import com.dukascopy.calculator.function.MFunction;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.function.RFunction;
/*    */ import com.dukascopy.calculator.function.SigmaPlus;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class SplusButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public SplusButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 26 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 27 */     setPobject(new SigmaPlus());
/* 28 */     setText();
/* 29 */     setTextSize();
/* 30 */     setShortcut('M');
/* 31 */     addActionListener(this);
/*    */ 
/* 33 */     setToolTipKey("sc.calculator.adds.current.expression.or.most.recent.result.as.a.number.in.statistics.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 45 */     synchronized (this.mainCalculatorPanel) {
/* 46 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 47 */         getMainCalculatorPanel().setMode(getPobject());
/* 48 */         getMainCalculatorPanel().requestFocusInWindow();
/* 49 */         return;
/*    */       }
/* 51 */       getMainCalculatorPanel().pushHistory();
/* 52 */       OObject o = getMainCalculatorPanel().getValue();
/* 53 */       if (!getMainCalculatorPanel().getParser().isEmpty())
/*    */       {
/* 55 */         PObject p = getMainCalculatorPanel().getParser().getLast();
/* 56 */         if (((o instanceof Complex)) && (((p instanceof RFunction)) || ((p instanceof DFunction)) || ((p instanceof MFunction)) || ((p instanceof AFunction))))
/*    */         {
/* 60 */           Ans ans = new Ans();
/* 61 */           ans.setValue((Complex)o);
/* 62 */           getMainCalculatorPanel().insert(ans);
/* 63 */           getMainCalculatorPanel().updateDisplay(true, true);
/*    */         }
/*    */       }
/*    */       else {
/* 67 */         Ans ans = new Ans();
/* 68 */         ans.setValue((Complex)o);
/* 69 */         getMainCalculatorPanel().insert(ans);
/* 70 */         getMainCalculatorPanel().updateDisplay(true, true);
/*    */       }
/* 72 */       o = getMainCalculatorPanel().getParser().evaluate(getMainCalculatorPanel().getAngleType());
/* 73 */       if ((o instanceof Complex)) {
/* 74 */         Complex d = (Complex)o;
/* 75 */         getMainCalculatorPanel().setValue(getMainCalculatorPanel().statAdd(d));
/* 76 */         getMainCalculatorPanel().updateDisplay(false, true);
/*    */       }
/* 78 */       getMainCalculatorPanel().setShift(false);
/* 79 */       getMainCalculatorPanel().newExpression();
/* 80 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.SplusButton
 * JD-Core Version:    0.6.0
 */