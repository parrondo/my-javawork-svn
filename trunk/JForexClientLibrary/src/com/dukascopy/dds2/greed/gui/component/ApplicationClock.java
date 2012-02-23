/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import javax.swing.Timer;
/*    */ 
/*    */ public class ApplicationClock
/*    */ {
/*    */   public static final int EACH_SECOND = 1000;
/* 15 */   private List<ClockListener> listeners = new LinkedList();
/*    */   private final Calendar calendar;
/*    */   private final Timer klok;
/*    */ 
/*    */   public ApplicationClock()
/*    */   {
/* 21 */     this.calendar = Calendar.getInstance();
/* 22 */     this.klok = new Timer(1000, new ActionListener() {
/*    */       public void actionPerformed(ActionEvent actionEvent) {
/* 24 */         ApplicationClock.this.calendar.add(13, 1);
/* 25 */         for (ClockListener listener : ApplicationClock.this.listeners)
/* 26 */           listener.updateTime(ApplicationClock.this.getTime());
/*    */       }
/*    */     });
/* 30 */     this.klok.start();
/*    */   }
/*    */ 
/*    */   public void addListener(ClockListener clockListener) {
/* 34 */     this.listeners.add(clockListener);
/*    */   }
/*    */ 
/*    */   public long getTime() {
/* 38 */     return this.calendar.getTime().getTime();
/*    */   }
/*    */ 
/*    */   public void syncTime(Long timeSyncMs) {
/* 42 */     if (null == timeSyncMs) {
/* 43 */       return;
/*    */     }
/* 45 */     if (!this.klok.isRunning())
/*    */     {
/* 49 */       setTimeAndStart(timeSyncMs.longValue());
/*    */     }
/*    */     else
/*    */     {
/* 54 */       this.calendar.setTimeInMillis(timeSyncMs.longValue());
/*    */     }
/*    */   }
/*    */ 
/*    */   private void setTimeAndStart(long millis) {
/* 59 */     this.calendar.setTimeInMillis(millis);
/* 60 */     this.klok.start();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ApplicationClock
 * JD-Core Version:    0.6.0
 */