/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ExecutionControl
/*     */ {
/*     */   public static final int HALF_THE_REAL_SPEED = 0;
/*     */   public static final int REAL_SPEED = 1;
/*     */   public static final int REAL_SPEED_2X = 2;
/*     */   public static final int REAL_SPEED_5X = 3;
/*     */   public static final int REAL_SPEED_10X = 4;
/*     */   public static final int REAL_SPEED_100X = 5;
/*     */   public static final int REAL_SPEED_500X = 6;
/*     */   public static final int MAX_SPEED = 7;
/*  22 */   private boolean running = true;
/*  23 */   private boolean executing = false;
/*  24 */   private int speed = 7;
/*  25 */   private boolean processTick = true;
/*     */   private boolean visualEnabled;
/*  28 */   private List<ExecutionControlListener> listeners = new LinkedList();
/*  29 */   private boolean startEnabled = true;
/*     */   private boolean optimization;
/*     */ 
/*     */   public void addExecutionControlListener(ExecutionControlListener listener)
/*     */   {
/*  36 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void removeExecutionControlListener(ExecutionControlListener listener) {
/*  40 */     this.listeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void startExecuting(boolean visualEnabled) {
/*  44 */     this.executing = true;
/*  45 */     this.visualEnabled = visualEnabled;
/*  46 */     fireStateChanged();
/*  47 */     run();
/*     */   }
/*     */ 
/*     */   public boolean isExecuting() {
/*  51 */     return this.executing;
/*     */   }
/*     */ 
/*     */   public boolean isVisualEnabled() {
/*  55 */     return this.visualEnabled;
/*     */   }
/*     */ 
/*     */   public void stopExecuting(boolean visualEnabled) {
/*  59 */     this.executing = false;
/*  60 */     this.visualEnabled = visualEnabled;
/*  61 */     fireStateChanged();
/*  62 */     run();
/*     */   }
/*     */ 
/*     */   public void setSpeed(int value) {
/*  66 */     if (this.speed != value) {
/*  67 */       this.speed = value;
/*  68 */       fireSpeedChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSpeed() {
/*  73 */     return this.speed;
/*     */   }
/*     */ 
/*     */   public boolean isPaused() {
/*  77 */     return !this.running;
/*     */   }
/*     */ 
/*     */   public void pause() {
/*  81 */     this.running = false;
/*  82 */     fireStateChanged();
/*     */   }
/*     */ 
/*     */   public synchronized void run() {
/*  86 */     if (!this.running) {
/*  87 */       this.running = true;
/*  88 */       notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void nextTick() {
/*  93 */     this.processTick = true;
/*  94 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized void waitForResume(LoadingProgressListener progressListener) {
/*  98 */     if (!this.running) {
/*  99 */       fireStateChanged();
/*     */ 
/* 101 */       while ((!this.running) && (!this.processTick) && (!progressListener.stopJob()))
/*     */         try {
/* 103 */           wait(1000L);
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/* 108 */       if (this.running)
/* 109 */         fireStateChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void tickProcessed()
/*     */   {
/* 115 */     this.processTick = false;
/*     */   }
/*     */ 
/*     */   protected void fireStateChanged() {
/* 119 */     ExecutionControlEvent event = new ExecutionControlEvent(this);
/* 120 */     for (ExecutionControlListener listener : this.listeners)
/* 121 */       listener.stateChanged(event);
/*     */   }
/*     */ 
/*     */   protected void fireSpeedChanged()
/*     */   {
/* 126 */     ExecutionControlEvent event = new ExecutionControlEvent(this);
/* 127 */     for (ExecutionControlListener listener : this.listeners)
/* 128 */       listener.speedChanged(event);
/*     */   }
/*     */ 
/*     */   public void setStartEnabled(boolean startEnabled)
/*     */   {
/* 133 */     this.startEnabled = startEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isStartEnabled() {
/* 137 */     return this.startEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isOptimization() {
/* 141 */     return this.optimization;
/*     */   }
/*     */ 
/*     */   public void setOptimization(boolean optimization) {
/* 145 */     this.optimization = optimization;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl
 * JD-Core Version:    0.6.0
 */