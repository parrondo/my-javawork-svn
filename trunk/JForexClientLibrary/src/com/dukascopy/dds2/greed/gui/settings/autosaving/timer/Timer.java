/*    */ package com.dukascopy.dds2.greed.gui.settings.autosaving.timer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.settings.autosaving.ClientSettingsStorageAutoSaving;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class Timer extends Thread
/*    */ {
/* 16 */   private static Logger LOGGER = LoggerFactory.getLogger(ClientSettingsStorageAutoSaving.class);
/*    */   private boolean canContinue;
/*    */   private long savePeriod;
/*    */   private static final long ONE_MINUTE = 60000L;
/*    */   private static final long FIRST_TIME_SLEEP_PERIOD = 60000L;
/*    */   private final long initialDelay;
/* 26 */   private final List<TimerListener> timerListeners = new ArrayList();
/*    */ 
/*    */   public Timer(long savePeriod)
/*    */   {
/* 30 */     this(savePeriod, 60000L);
/*    */   }
/*    */ 
/*    */   public Timer(long savePeriod, long initialDelay) {
/* 34 */     super("ClientSettingsStorageAutoSavingThread");
/*    */ 
/* 36 */     this.canContinue = true;
/* 37 */     this.savePeriod = savePeriod;
/*    */ 
/* 39 */     this.initialDelay = initialDelay;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 44 */     doSleep(this.initialDelay);
/* 45 */     while (getCanContinue())
/*    */       try {
/* 47 */         fireTick();
/*    */       } catch (Throwable t) {
/* 49 */         LOGGER.error("Error while autosaving client setting storage - ", t);
/*    */       }
/*    */       finally {
/* 52 */         doSleep(getSavePeriod());
/*    */       }
/*    */   }
/*    */ 
/*    */   private void fireTick()
/*    */   {
/* 58 */     for (TimerListener timerListener : getTimerListeners())
/* 59 */       timerListener.onTimerTick();
/*    */   }
/*    */ 
/*    */   private void doSleep(long period)
/*    */   {
/*    */     try {
/* 65 */       sleep(period);
/*    */     } catch (Throwable t) {
/* 67 */       LOGGER.error("Error while sleep - ", t);
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean getCanContinue() {
/* 72 */     return this.canContinue;
/*    */   }
/*    */ 
/*    */   public void setCanContinue(boolean canContinue) {
/* 76 */     this.canContinue = canContinue;
/*    */   }
/*    */ 
/*    */   public long getSavePeriod() {
/* 80 */     return this.savePeriod;
/*    */   }
/*    */ 
/*    */   public void setSavePeriod(long savePeriod) {
/* 84 */     this.savePeriod = savePeriod;
/*    */   }
/*    */ 
/*    */   private List<TimerListener> getTimerListeners() {
/* 88 */     return this.timerListeners;
/*    */   }
/*    */ 
/*    */   public void addTimerListener(TimerListener timerListener) {
/* 92 */     getTimerListeners().add(timerListener);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.autosaving.timer.Timer
 * JD-Core Version:    0.6.0
 */