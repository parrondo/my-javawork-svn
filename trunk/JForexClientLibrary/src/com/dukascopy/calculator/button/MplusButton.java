/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.Error;
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.Parser;
/*    */ import com.dukascopy.calculator.function.AFunction;
/*    */ import com.dukascopy.calculator.function.Ans;
/*    */ import com.dukascopy.calculator.function.DFunction;
/*    */ import com.dukascopy.calculator.function.MFunction;
/*    */ import com.dukascopy.calculator.function.Mplus;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import com.dukascopy.calculator.function.RFunction;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class MplusButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MplusButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 24 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 25 */     setPobject(new Mplus());
/* 26 */     setText();
/* 27 */     setTextSize();
/*    */ 
/* 29 */     addActionListener(this);
/*    */ 
/* 31 */     setShortcut('M');
/* 32 */     setToolTipKey("sc.calculator.adds.current.expression.or.most.recent.result.value.to.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 42 */     synchronized (this.mainCalculatorPanel) {
/* 43 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 44 */         getMainCalculatorPanel().setMode(getPobject());
/* 45 */         getMainCalculatorPanel().requestFocusInWindow();
/* 46 */         return;
/*    */       }
/* 48 */       OObject m = getMainCalculatorPanel().getMemory();
/* 49 */       getMainCalculatorPanel().pushHistory();
/* 50 */       OObject o = getMainCalculatorPanel().getValue();
/* 51 */       if (!getMainCalculatorPanel().getParser().isEmpty())
/*    */       {
/* 53 */         PObject p = getMainCalculatorPanel().getParser().getLast();
/* 54 */         if ((!(o instanceof Error)) && (((p instanceof RFunction)) || ((p instanceof DFunction)) || ((p instanceof MFunction)) || ((p instanceof AFunction))))
/*    */         {
/* 58 */           Ans ans = new Ans();
/* 59 */           ans.setValue(o);
/* 60 */           getMainCalculatorPanel().insert(ans);
/* 61 */           getMainCalculatorPanel().updateDisplay(true, true);
/*    */         }
/*    */       }
/*    */       else {
/* 65 */         Ans ans = new Ans();
/* 66 */         ans.setValue(o);
/* 67 */         getMainCalculatorPanel().insert(ans);
/* 68 */         getMainCalculatorPanel().updateDisplay(true, true);
/*    */       }
/* 70 */       o = getMainCalculatorPanel().getParser().evaluate(getMainCalculatorPanel().getAngleType());
/* 71 */       if (!(o instanceof Error)) {
/* 72 */         OObject q = m.add(o);
/* 73 */         if (!(q instanceof Error))
/* 74 */           getMainCalculatorPanel().setMemory(q);
/* 75 */         getMainCalculatorPanel().updateDisplay(false, true);
/*    */       }
/* 77 */       getMainCalculatorPanel().setShift(false);
/* 78 */       getMainCalculatorPanel().newExpression();
/* 79 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.MplusButton
 * JD-Core Version:    0.6.0
 */