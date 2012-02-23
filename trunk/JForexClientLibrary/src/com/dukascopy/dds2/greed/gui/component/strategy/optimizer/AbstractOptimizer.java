/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class AbstractOptimizer
/*    */   implements ParameterOptimizer
/*    */ {
/* 19 */   private List<ParameterOptimizerListener> listeners = new LinkedList();
/*    */   private final boolean mandatory;
/*    */   private final boolean readOnly;
/*    */ 
/*    */   public AbstractOptimizer(boolean mandatory, boolean readOnly)
/*    */   {
/* 25 */     this.mandatory = mandatory;
/* 26 */     this.readOnly = readOnly;
/*    */   }
/*    */ 
/*    */   public boolean isMandatory() {
/* 30 */     return this.mandatory;
/*    */   }
/*    */ 
/*    */   public boolean isReadOnly() {
/* 34 */     return this.readOnly;
/*    */   }
/*    */ 
/*    */   public void addOptimizerListener(ParameterOptimizerListener listener)
/*    */   {
/* 39 */     this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void removeOptimizerListener(ParameterOptimizerListener listener)
/*    */   {
/* 44 */     this.listeners.remove(listener);
/*    */   }
/*    */ 
/*    */   protected void fireParametersChanged() {
/* 48 */     ParameterOptimizerEvent event = new ParameterOptimizerEvent(this);
/* 49 */     for (ParameterOptimizerListener listener : this.listeners)
/* 50 */       listener.parametersChanged(event);
/*    */   }
/*    */ 
/*    */   public void layoutOptimizerComponents(JPanel container, Object value)
/*    */   {
/* 59 */     container.setVisible(!isReadOnly());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.AbstractOptimizer
 * JD-Core Version:    0.6.0
 */