/*     */ package com.dukascopy.calculator.button;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Error;
/*     */ import com.dukascopy.calculator.MainCalculatorPanel;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.Parser;
/*     */ import com.dukascopy.calculator.function.AFunction;
/*     */ import com.dukascopy.calculator.function.Ans;
/*     */ import com.dukascopy.calculator.function.DFunction;
/*     */ import com.dukascopy.calculator.function.Equals;
/*     */ import com.dukascopy.calculator.function.MFunction;
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import com.dukascopy.calculator.function.RFunction;
/*     */ import java.awt.event.ActionEvent;
/*     */ 
/*     */ public class EqualsButton extends CalculatorButton
/*     */ {
/*     */   protected ChangeBase changeBase;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   protected EqualsButton()
/*     */   {
/*     */   }
/*     */ 
/*     */   public EqualsButton(MainCalculatorPanel mainCalculatorPanel)
/*     */   {
/*  38 */     this.mainCalculatorPanel = mainCalculatorPanel;
/*  39 */     setPobject(new Equals());
/*  40 */     setText();
/*  41 */     setTextSize();
/*  42 */     this.changeBase = ChangeBase.NONE;
/*  43 */     addActionListener(this);
/*     */ 
/*  45 */     setShortcut('=');
/*  46 */     setToolTipKey("sc.calculator.evaluates.the.expression.you.have.just.typed");
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent actionEvent)
/*     */   {
/*  59 */     synchronized (this.mainCalculatorPanel) {
/*  60 */       if (getMainCalculatorPanel().getMode() != 0) {
/*  61 */         getMainCalculatorPanel().setMode(getPobject());
/*  62 */         getMainCalculatorPanel().requestFocusInWindow();
/*  63 */         return;
/*     */       }
/*  65 */       if (!getMainCalculatorPanel().getParser().isEmpty()) {
/*  66 */         PObject p = getMainCalculatorPanel().getParser().getLast();
/*  67 */         OObject o = getMainCalculatorPanel().getValue();
/*  68 */         if ((!(o instanceof Error)) && (((p instanceof RFunction)) || ((p instanceof DFunction)) || ((p instanceof MFunction)) || ((p instanceof AFunction))))
/*     */         {
/*  72 */           Ans ans = new Ans();
/*  73 */           ans.setValue(o);
/*  74 */           getMainCalculatorPanel().insert(ans);
/*  75 */           getMainCalculatorPanel().updateDisplay(true, true);
/*     */         }
/*  77 */         getMainCalculatorPanel().pushHistory();
/*  78 */         OObject value = getMainCalculatorPanel().getParser().evaluate(getMainCalculatorPanel().getAngleType());
/*  79 */         getMainCalculatorPanel().clear();
/*  80 */         getMainCalculatorPanel().setValue(value);
/*     */       }
/*  82 */       getMainCalculatorPanel().setShift(false);
/*  83 */       getMainCalculatorPanel().newExpression();
/*  84 */       setBase();
/*  85 */       getMainCalculatorPanel().updateDisplay(true, true);
/*  86 */       getMainCalculatorPanel().requestFocusInWindow();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setBase()
/*     */   {
/*  97 */     switch (1.$SwitchMap$com$dukascopy$calculator$button$EqualsButton$ChangeBase[this.changeBase.ordinal()]) {
/*     */     case 1:
/*  99 */       getMainCalculatorPanel().setBase(Base.DECIMAL);
/* 100 */       break;
/*     */     case 2:
/* 102 */       getMainCalculatorPanel().setBase(Base.BINARY);
/* 103 */       break;
/*     */     case 3:
/* 105 */       getMainCalculatorPanel().setBase(Base.OCTAL);
/* 106 */       break;
/*     */     case 4:
/* 108 */       getMainCalculatorPanel().setBase(Base.HEXADECIMAL);
/* 109 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static enum ChangeBase
/*     */   {
/* 124 */     BINARY, OCTAL, DECIMAL, HEXADECIMAL, NONE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.EqualsButton
 * JD-Core Version:    0.6.0
 */