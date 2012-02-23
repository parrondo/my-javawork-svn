/*    */ package com.dukascopy.dds2.greed.gui.settings.autosaving;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.dds2.greed.gui.settings.autosaving.timer.Timer;
/*    */ import com.dukascopy.dds2.greed.gui.settings.autosaving.timer.TimerListener;
/*    */ 
/*    */ public class ClientSettingsStorageAutoSaving
/*    */   implements IClientSettingsStorageAutoSaving, TimerListener
/*    */ {
/*    */   private Timer timer;
/*    */   private Long autoSavePeriodInMinutes;
/*    */   private final ClientSettingsStorage clientSettingsStorage;
/*    */ 
/*    */   public ClientSettingsStorageAutoSaving(ClientSettingsStorage clientSettingsStorage)
/*    */   {
/* 20 */     this.clientSettingsStorage = clientSettingsStorage;
/*    */   }
/*    */ 
/*    */   public void startAutoSaving() {
/* 24 */     startAutoSaving(0L);
/*    */   }
/*    */ 
/*    */   private Long converToMillis(Long minutes) {
/* 28 */     return Long.valueOf(minutes.longValue() * 60L * 1000L);
/*    */   }
/*    */ 
/*    */   public void stopAutoSaving() {
/* 32 */     if (getTimer() != null)
/* 33 */       getTimer().setCanContinue(false);
/*    */   }
/*    */ 
/*    */   private Timer getTimer()
/*    */   {
/* 38 */     return this.timer;
/*    */   }
/*    */ 
/*    */   private void setTimer(Timer timer) {
/* 42 */     this.timer = timer;
/*    */   }
/*    */ 
/*    */   private void setAutoSavePeriodInMinutes(Long workspaceAutoSavePeriod) {
/* 46 */     this.autoSavePeriodInMinutes = workspaceAutoSavePeriod;
/*    */ 
/* 48 */     if (getTimer() != null)
/* 49 */       getTimer().setSavePeriod(converToMillis(getAutoSavePeriodInMinutes()).longValue());
/*    */   }
/*    */ 
/*    */   public Long getAutoSavePeriodInMinutes()
/*    */   {
/* 54 */     return this.autoSavePeriodInMinutes;
/*    */   }
/*    */ 
/*    */   public void onTimerTick()
/*    */   {
/* 59 */     this.clientSettingsStorage.saveWorkspaceSettings();
/*    */   }
/*    */ 
/*    */   private void reloadSetings() {
/* 63 */     setAutoSavePeriodInMinutes(this.clientSettingsStorage.restoreWorkspaceAutoSavePeriodInMinutes());
/*    */   }
/*    */ 
/*    */   public void startAutoSaving(long launchingDelay)
/*    */   {
/* 68 */     stopAutoSaving();
/* 69 */     reloadSetings();
/*    */ 
/* 71 */     if ((getAutoSavePeriodInMinutes() != null) && (getAutoSavePeriodInMinutes().longValue() > 0L)) {
/* 72 */       Timer timer = new Timer(converToMillis(getAutoSavePeriodInMinutes()).longValue(), launchingDelay);
/* 73 */       timer.addTimerListener(this);
/*    */ 
/* 75 */       setTimer(timer);
/* 76 */       getTimer().start();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.autosaving.ClientSettingsStorageAutoSaving
 * JD-Core Version:    0.6.0
 */