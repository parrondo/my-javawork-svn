/*    */ package com.dukascopy.charts.dialogs.indicators.listener;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.event.AWTEventListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class IndicatorAWTEventListener
/*    */   implements AWTEventListener
/*    */ {
/*  9 */   private List<IndicatorAWTEventDelegateListener> delegates = new ArrayList();
/*    */ 
/*    */   public List<IndicatorAWTEventDelegateListener> getDelegates() {
/* 12 */     return this.delegates;
/*    */   }
/*    */ 
/*    */   public void setDelegates(List<IndicatorAWTEventDelegateListener> delegates) {
/* 16 */     this.delegates = delegates;
/*    */   }
/*    */ 
/*    */   public void addDelegateListener(IndicatorAWTEventDelegateListener listener) {
/* 20 */     getDelegates().add(listener);
/*    */   }
/*    */ 
/*    */   public void removeDelegateListener(IndicatorAWTEventDelegateListener listener) {
/* 24 */     getDelegates().remove(listener);
/*    */   }
/*    */ 
/*    */   public void removeAllDelegateListeners() {
/* 28 */     getDelegates().clear();
/*    */   }
/*    */ 
/*    */   private void fireEvent(AWTEvent event) {
/* 32 */     for (IndicatorAWTEventDelegateListener delegate : getDelegates())
/* 33 */       delegate.eventDispatched(event);
/*    */   }
/*    */ 
/*    */   public void eventDispatched(AWTEvent event)
/*    */   {
/* 39 */     fireEvent(event);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.listener.IndicatorAWTEventListener
 * JD-Core Version:    0.6.0
 */