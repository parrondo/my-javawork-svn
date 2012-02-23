/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.Error;
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
/*    */ import com.dukascopy.calculator.function.STO;
/*    */ import java.awt.event.ActionEvent;
/*    */ 
/*    */ public class STOButton extends CalculatorButton
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public STOButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 27 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 28 */     setPobject(new STO());
/* 29 */     setText();
/* 30 */     setTextSize();
/* 31 */     setShortcut('S');
/* 32 */     addActionListener(this);
/*    */ 
/* 34 */     setToolTipKey("sc.calculator.evaluates.current.expression.and.stores.in.memory");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 48 */     synchronized (this.mainCalculatorPanel) {
/* 49 */       if (!(getMainCalculatorPanel().getValue() instanceof Complex)) {
/* 50 */         getMainCalculatorPanel().requestFocusInWindow();
/* 51 */         return;
/*    */       }
/* 53 */       getMainCalculatorPanel().pushHistory();
/* 54 */       Complex value = (Complex)(Complex)getMainCalculatorPanel().getValue();
/* 55 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 56 */         getMainCalculatorPanel().setMode(getPobject());
/* 57 */         getMainCalculatorPanel().requestFocusInWindow();
/* 58 */         return;
/*    */       }
/* 60 */       OObject o = getMainCalculatorPanel().getValue();
/* 61 */       if (!getMainCalculatorPanel().getParser().isEmpty()) {
/* 62 */         PObject p = getMainCalculatorPanel().getParser().getLast();
/* 63 */         if ((!(o instanceof Error)) && (((p instanceof RFunction)) || ((p instanceof DFunction)) || ((p instanceof MFunction)) || ((p instanceof AFunction))))
/*    */         {
/* 67 */           Ans ans = new Ans();
/* 68 */           ans.setValue(o);
/* 69 */           getMainCalculatorPanel().insert(ans);
/* 70 */           getMainCalculatorPanel().updateDisplay(true, true);
/*    */         }
/* 72 */         o = getMainCalculatorPanel().getParser().evaluate(getMainCalculatorPanel().getAngleType());
/*    */       } else {
/* 74 */         o = value;
/*    */       }
/* 76 */       getMainCalculatorPanel().setValue(o);
/* 77 */       if (!(o instanceof Error))
/* 78 */         getMainCalculatorPanel().setMemory(o);
/* 79 */       getMainCalculatorPanel().updateDisplay(false, true);
/* 80 */       if (getMainCalculatorPanel().getShift())
/* 81 */         getMainCalculatorPanel().setShift(false);
/* 82 */       getMainCalculatorPanel().newExpression();
/* 83 */       getMainCalculatorPanel().requestFocusInWindow();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.STOButton
 * JD-Core Version:    0.6.0
 */