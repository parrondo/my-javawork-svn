/*    */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*    */ 
/*    */ public abstract interface IEventHandler
/*    */ {
/*    */   public abstract void handle(Event paramEvent, Object paramObject);
/*    */ 
/*    */   public static enum Event
/*    */   {
/*  9 */     STRATEGY_STATE_CHANGED, 
/* 10 */     PRESENTED_INSTRUMENTS_CHANGED;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler
 * JD-Core Version:    0.6.0
 */