/*    */ package com.dukascopy.charts.listeners.verticalmover;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class VerticalMoverListenerRegistry
/*    */ {
/*  8 */   List<VerticalMoverEnableDisableListener> listeners = new ArrayList();
/*    */ 
/*    */   public void registerListener(VerticalMoverEnableDisableListener listener) {
/* 11 */     this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void switchVerticalMovement(boolean isVerticalChartMovementEnabled) {
/* 15 */     VerticalMoverEnableDisableListener[] copy = (VerticalMoverEnableDisableListener[])this.listeners.toArray(new VerticalMoverEnableDisableListener[this.listeners.size()]);
/* 16 */     for (VerticalMoverEnableDisableListener verticalMoverEnableDisableListener : copy)
/* 17 */       verticalMoverEnableDisableListener.setVerticalMovementEnabled(isVerticalChartMovementEnabled);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.verticalmover.VerticalMoverListenerRegistry
 * JD-Core Version:    0.6.0
 */