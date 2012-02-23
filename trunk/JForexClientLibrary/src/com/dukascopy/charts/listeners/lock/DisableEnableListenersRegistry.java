/*    */ package com.dukascopy.charts.listeners.lock;
/*    */ 
/*    */ import com.dukascopy.charts.listener.DisableEnableListener;
/*    */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class DisableEnableListenersRegistry
/*    */   implements ProgressListener
/*    */ {
/* 11 */   List<DisableEnableListener> listeners = new ArrayList();
/*    */ 
/*    */   public void registerListener(DisableEnableListener listener) {
/* 14 */     this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void disabled() {
/* 18 */     DisableEnableListener[] copy = (DisableEnableListener[])this.listeners.toArray(new DisableEnableListener[this.listeners.size()]);
/* 19 */     for (DisableEnableListener disableEnableListener : copy)
/* 20 */       disableEnableListener.disabled();
/*    */   }
/*    */ 
/*    */   public void enabled()
/*    */   {
/* 25 */     DisableEnableListener[] copy = (DisableEnableListener[])this.listeners.toArray(new DisableEnableListener[this.listeners.size()]);
/* 26 */     for (DisableEnableListener disableEnableListener : copy)
/* 27 */       disableEnableListener.enabled();
/*    */   }
/*    */ 
/*    */   public void setProgress(boolean isProgress, boolean isLoadingOrders)
/*    */   {
/* 32 */     if (isLoadingOrders) {
/* 33 */       return;
/*    */     }
/* 35 */     if (isProgress)
/* 36 */       disabled();
/*    */     else
/* 38 */       enabled();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.lock.DisableEnableListenersRegistry
 * JD-Core Version:    0.6.0
 */