/*    */ package com.dukascopy.charts.dialogs.indicators.component.spinner;
/*    */ 
/*    */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import javax.swing.JFormattedTextField;
/*    */ import javax.swing.JSpinner.DefaultEditor;
/*    */ import javax.swing.SpinnerNumberModel;
/*    */ import javax.swing.text.DefaultFormatter;
/*    */ 
/*    */ public class IntegerOptParameterSpinner extends AbstractOptParameterSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public IntegerOptParameterSpinner(Integer paramValue, IntegerRangeDescription integerRangeDescription, PropertyChangeListener propertyChangeListener)
/*    */   {
/* 20 */     super(new SpinnerNumberModel(paramValue.intValue(), integerRangeDescription.getMin(), integerRangeDescription.getMax(), integerRangeDescription.getSuggestedIncrement()), propertyChangeListener);
/*    */ 
/* 29 */     ((DefaultFormatter)((JSpinner.DefaultEditor)getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.component.spinner.IntegerOptParameterSpinner
 * JD-Core Version:    0.6.0
 */