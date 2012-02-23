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
/*    */ import com.dukascopy.calculator.function.SigmaMinus;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class SminusButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public SminusButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 26 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 27 */     setPobject(new SigmaMinus());
/* 28 */     setText();
/* 29 */     setTextSize();
/* 30 */     setShortcut('M');
/* 31 */     addActionListener(this);
/*    */ 
/* 33 */     setToolTipKey("sc.calculator.removes.current.expression.or.most.recent.result.from.statistics.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 46 */     synchronized (this.mainCalculatorPanel) {
/* 47 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 48 */         getMainCalculatorPanel().setMode(getPobject());
/* 49 */         getMainCalculatorPanel().requestFocusInWindow();
/* 50 */         return;
/*    */       }
/* 52 */       getMainCalculatorPanel().pushHistory();
/* 53 */       OObject o = getMainCalculatorPanel().getValue();
/* 54 */       if (!getMainCalculatorPanel().getParser().isEmpty())
/*    */       {
/* 56 */         PObject p = getMainCalculatorPanel().getParser().getLast();
/* 57 */         if (((o instanceof Complex)) && (((p instanceof RFunction)) || ((p instanceof DFunction)) || ((p instanceof MFunction)) || ((p instanceof AFunction))))
/*    */         {
/* 61 */           Ans ans = new Ans();
/* 62 */           ans.setValue((Complex)o);
/* 63 */           getMainCalculatorPanel().insert(ans);
/* 64 */           getMainCalculatorPanel().updateDisplay(true, true);
/*    */         }
/*    */       }
/*    */       else {
/* 68 */         Ans ans = new Ans();
/* 69 */         ans.setValue((Complex)o);
/* 70 */         getMainCalculatorPanel().insert(ans);
/* 71 */         getMainCalculatorPanel().updateDisplay(true, true);
/*    */       }
/* 73 */       o = getMainCalculatorPanel().getParser().evaluate(getMainCalculatorPanel().getAngleType());
/* 74 */       Complex d = (Complex)o;
/* 75 */       getMainCalculatorPanel().setValue(getMainCalculatorPanel().statSub(d));
/* 76 */       getMainCalculatorPanel().updateDisplay(false, true);
/* 77 */       getMainCalculatorPanel().setShift(false);
/* 78 */       getMainCalculatorPanel().newExpression();
/* 79 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.SminusButton
 * JD-Core Version:    0.6.0
 */