/*    */ package com.dukascopy.dds2.greed.util.event;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.actions.AppActionListener;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class SimpleApplicationEventMulticaster
/*    */   implements ApplicationEventMulticaster
/*    */ {
/* 18 */   private final Set<ApplicationListener> applicationListeners = new HashSet();
/*    */ 
/* 20 */   private static SimpleApplicationEventMulticaster singleton = null;
/*    */ 
/*    */   public static ApplicationEventMulticaster getInstance()
/*    */   {
/* 24 */     if (singleton == null) {
/* 25 */       singleton = new SimpleApplicationEventMulticaster();
/* 26 */       ApplicationListener appActionListener = new AppActionListener();
/* 27 */       singleton.addApplicationListener(appActionListener);
/*    */     }
/* 29 */     return singleton;
/*    */   }
/*    */ 
/*    */   public void addApplicationListener(ApplicationListener listener)
/*    */   {
/* 36 */     this.applicationListeners.add(listener);
/*    */   }
/*    */ 
/*    */   public void removeApplicationListener(ApplicationListener listener) {
/* 40 */     this.applicationListeners.remove(listener);
/*    */   }
/*    */ 
/*    */   public void removeAllListeners() {
/* 44 */     this.applicationListeners.clear();
/*    */   }
/*    */ 
/*    */   public static void removeInstance() {
/* 48 */     singleton = null;
/*    */   }
/*    */ 
/*    */   public void multicastEvent(ApplicationEvent event)
/*    */   {
/* 53 */     Iterator it = this.applicationListeners.iterator();
/* 54 */     while (it.hasNext())
/*    */     {
/* 57 */       ApplicationListener listener = (ApplicationListener)it.next();
/*    */ 
/* 55 */       listener.onApplicationEvent(event);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.event.SimpleApplicationEventMulticaster
 * JD-Core Version:    0.6.0
 */