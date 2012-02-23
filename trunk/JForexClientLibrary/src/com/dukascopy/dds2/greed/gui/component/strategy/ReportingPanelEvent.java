/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class ReportingPanelEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ReportingPanelEvent(ReportingPanel source)
/*    */   {
/* 18 */     super(source);
/*    */   }
/*    */ 
/*    */   public ReportingPanel getSource()
/*    */   {
/* 23 */     return (ReportingPanel)super.getSource();
/*    */   }
/*    */ 
/*    */   public boolean isOptimizationOn()
/*    */   {
/* 31 */     return getSource().isOptimizationOn();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.ReportingPanelEvent
 * JD-Core Version:    0.6.0
 */