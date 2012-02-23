/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class DoubleSpinnerModel extends SpinnerNumberModel
/*    */ {
/*    */   public DoubleSpinnerModel(double value, double minimum, double maximum, double step)
/*    */   {
/* 18 */     super(value, minimum, maximum, step);
/*    */   }
/*    */ 
/*    */   public Object getNextValue()
/*    */   {
/* 25 */     double value = BigDecimal.valueOf(((Double)getValue()).doubleValue()).add(BigDecimal.valueOf(getStepSize().doubleValue())).doubleValue();
/* 26 */     Comparable maximum = getMaximum();
/* 27 */     if ((maximum != null) && (maximum.compareTo(Double.valueOf(value)) < 0)) {
/* 28 */       return null;
/*    */     }
/* 30 */     return Double.valueOf(value);
/*    */   }
/*    */ 
/*    */   public Object getPreviousValue()
/*    */   {
/* 38 */     double value = BigDecimal.valueOf(((Double)getValue()).doubleValue()).subtract(BigDecimal.valueOf(getStepSize().doubleValue())).doubleValue();
/* 39 */     Comparable minimum = getMinimum();
/* 40 */     if ((minimum != null) && (minimum.compareTo(Double.valueOf(value)) > 0)) {
/* 41 */       return null;
/*    */     }
/* 43 */     return Double.valueOf(value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.DoubleSpinnerModel
 * JD-Core Version:    0.6.0
 */