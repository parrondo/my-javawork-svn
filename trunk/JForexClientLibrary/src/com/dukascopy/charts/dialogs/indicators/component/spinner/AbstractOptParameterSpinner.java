/*    */ package com.dukascopy.charts.dialogs.indicators.component.spinner;
/*    */ 
/*    */ import java.awt.event.FocusEvent;
/*    */ import java.awt.event.FocusListener;
/*    */ import java.awt.event.MouseWheelEvent;
/*    */ import java.awt.event.MouseWheelListener;
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import java.text.ParseException;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JSpinner;
/*    */ import javax.swing.SpinnerModel;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ 
/*    */ public abstract class AbstractOptParameterSpinner extends JSpinner
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final PropertyChangeListener propertyChangeListener;
/*    */ 
/*    */   public AbstractOptParameterSpinner(SpinnerModel model, PropertyChangeListener propertyChangeListener)
/*    */   {
/* 25 */     super(model);
/*    */ 
/* 27 */     this.propertyChangeListener = propertyChangeListener;
/*    */ 
/* 29 */     addListeners();
/*    */   }
/*    */ 
/*    */   protected void addListeners() {
/* 33 */     addChangeListener(new ChangeListener()
/*    */     {
/*    */       public void stateChanged(ChangeEvent e) {
/* 36 */         AbstractOptParameterSpinner.this.fireValueChanged();
/*    */       }
/*    */     });
/* 40 */     addMouseWheelListener(new Object()
/*    */     {
/*    */       public void mouseWheelMoved(MouseWheelEvent e) {
/* 43 */         int unitsToScroll = e.getUnitsToScroll();
/* 44 */         Object value = null;
/* 45 */         if (unitsToScroll == 0) {
/* 46 */           return;
/*    */         }
/* 48 */         if (unitsToScroll > 0) {
/* 49 */           value = AbstractOptParameterSpinner.this.getNextValue();
/*    */         }
/*    */         else {
/* 52 */           value = AbstractOptParameterSpinner.this.getPreviousValue();
/*    */         }
/* 54 */         if (value != null)
/* 55 */           AbstractOptParameterSpinner.this.setValue(value);
/*    */       }
/*    */     });
/* 60 */     FocusListener focusListener = new FocusListener()
/*    */     {
/*    */       public void focusLost(FocusEvent e) {
/* 63 */         AbstractOptParameterSpinner.this.fireValueChanged();
/*    */       }
/*    */ 
/*    */       public void focusGained(FocusEvent e)
/*    */       {
/*    */       }
/*    */     };
/* 71 */     addFocusListener(focusListener);
/* 72 */     getEditor().addFocusListener(focusListener);
/*    */   }
/*    */ 
/*    */   private void fireValueChanged() {
/*    */     try {
/* 77 */       commitEdit();
/*    */     } catch (ParseException e) {
/* 79 */       e.printStackTrace();
/*    */     }
/* 81 */     this.propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", getPreviousValue(), getValue()));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.component.spinner.AbstractOptParameterSpinner
 * JD-Core Version:    0.6.0
 */