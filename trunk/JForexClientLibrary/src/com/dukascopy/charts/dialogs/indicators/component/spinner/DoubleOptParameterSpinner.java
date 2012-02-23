/*    */ package com.dukascopy.charts.dialogs.indicators.component.spinner;
/*    */ 
/*    */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import javax.swing.JSpinner.NumberEditor;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ 
/*    */ public class DoubleOptParameterSpinner extends AbstractOptParameterSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private DoubleRangeDescription doubleRangeDescription;
/*    */ 
/*    */   public DoubleOptParameterSpinner(Double paramValue, DoubleRangeDescription doubleRangeDescription, PropertyChangeListener propertyChangeListener)
/*    */   {
/* 21 */     super(new SpinnerNumberModel(paramValue.doubleValue(), doubleRangeDescription.getMin(), doubleRangeDescription.getMax(), doubleRangeDescription.getSuggestedIncrement()), propertyChangeListener);
/*    */ 
/* 31 */     this.doubleRangeDescription = doubleRangeDescription;
/*    */ 
/* 33 */     setEditor(new JSpinner.NumberEditor(this, getPattern()));
/*    */   }
/*    */ 
/*    */   protected String getPattern() {
/* 37 */     String pattern = "###";
/* 38 */     if (this.doubleRangeDescription.getPrecision() > 0) {
/* 39 */       pattern = pattern + ".";
/* 40 */       for (int i = 0; i < this.doubleRangeDescription.getPrecision(); i++) {
/* 41 */         pattern = pattern + "#";
/*    */       }
/*    */     }
/* 44 */     return pattern;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.component.spinner.DoubleOptParameterSpinner
 * JD-Core Version:    0.6.0
 */