/*    */ package com.dukascopy.charts.dialogs.indicators.component;
/*    */ 
/*    */ import com.dukascopy.api.indicators.BooleanOptInputDescription;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.beans.PropertyChangeListener;
/*    */ import javax.swing.JCheckBox;
/*    */ 
/*    */ public class BooleanOptParameterEditor extends JCheckBox
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Boolean paramValue;
/*    */   private final BooleanOptInputDescription booleanOptInputDescription;
/*    */   private final PropertyChangeListener propertyChangeListener;
/*    */ 
/*    */   public BooleanOptParameterEditor(Boolean paramValue, BooleanOptInputDescription booleanOptInputDescription, PropertyChangeListener propertyChangeListener)
/*    */   {
/* 31 */     this.paramValue = paramValue;
/* 32 */     this.booleanOptInputDescription = booleanOptInputDescription;
/* 33 */     this.propertyChangeListener = propertyChangeListener;
/*    */ 
/* 35 */     setHorizontalAlignment(4);
/*    */ 
/* 37 */     addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 40 */         BooleanOptParameterEditor.this.getPropertyChangeListener().propertyChange(new PropertyChangeEvent(this, "value", new Boolean(!BooleanOptParameterEditor.this.isSelected()), new Boolean(BooleanOptParameterEditor.this.isSelected())));
/*    */       }
/*    */     });
/* 44 */     setSelected(paramValue.booleanValue());
/*    */   }
/*    */ 
/*    */   public Boolean getParamValue() {
/* 48 */     return this.paramValue;
/*    */   }
/*    */ 
/*    */   public BooleanOptInputDescription getBooleanOptInputDescription() {
/* 52 */     return this.booleanOptInputDescription;
/*    */   }
/*    */ 
/*    */   public PropertyChangeListener getPropertyChangeListener() {
/* 56 */     return this.propertyChangeListener;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.component.BooleanOptParameterEditor
 * JD-Core Version:    0.6.0
 */