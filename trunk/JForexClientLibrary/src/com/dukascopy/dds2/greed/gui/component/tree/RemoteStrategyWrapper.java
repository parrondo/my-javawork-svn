/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ 
/*     */ public class RemoteStrategyWrapper extends StrategyWrapper
/*     */ {
/*     */   private Long remoteStrategyId;
/*     */   private boolean remoteRunAllowed;
/*     */   private boolean haveAnnotations;
/*     */   private String name;
/*     */   private RemoteStrategyType strategyType;
/*     */ 
/*     */   public boolean isRemote()
/*     */   {
/*  20 */     return true;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  24 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  29 */     if (this.name != null) {
/*  30 */       return this.name;
/*     */     }
/*  32 */     return super.getName();
/*     */   }
/*     */ 
/*     */   public Long getRemoteStrategyId()
/*     */   {
/*  37 */     return this.remoteStrategyId;
/*     */   }
/*     */ 
/*     */   public void setRemoteStrategyId(Long remoteStrategyId) {
/*  41 */     this.remoteStrategyId = remoteStrategyId;
/*     */   }
/*     */ 
/*     */   public RemoteStrategyType getStrategyType() {
/*  45 */     return this.strategyType;
/*     */   }
/*     */ 
/*     */   public void setStrategyType(RemoteStrategyType strategyType) {
/*  49 */     this.strategyType = strategyType;
/*     */   }
/*     */ 
/*     */   public boolean isAnnotated()
/*     */   {
/*  54 */     return this.haveAnnotations;
/*     */   }
/*     */ 
/*     */   public void setHaveAnnotations(boolean haveAnnotations) {
/*  58 */     this.haveAnnotations = haveAnnotations;
/*     */   }
/*     */ 
/*     */   public boolean isEditable()
/*     */   {
/*  63 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$RemoteStrategyType[getStrategyType().ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/*  66 */       return false;
/*     */     case 3:
/*     */     case 4:
/*  69 */       return super.isEditable();
/*     */     }
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isTestable()
/*     */   {
/*  76 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$RemoteStrategyType[getStrategyType().ordinal()]) {
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*  80 */       return true;
/*     */     case 1:
/*     */     }
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRunnable()
/*     */   {
/*  89 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$RemoteStrategyType[getStrategyType().ordinal()]) {
/*     */     case 3:
/*     */     case 4:
/*  92 */       return super.isRunnable();
/*     */     case 2:
/*  94 */       return getName().endsWith(".jfx");
/*     */     case 1:
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRemotelyRunnable()
/*     */   {
/* 103 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$RemoteStrategyType[getStrategyType().ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/* 108 */       return (this.remoteRunAllowed) && (getRemoteStrategyId() != null);
/*     */     }
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRemoteRunAllowed()
/*     */   {
/* 115 */     return this.remoteRunAllowed;
/*     */   }
/*     */ 
/*     */   public void setRemoteRunAllowed(boolean remoteRunAllowed) {
/* 119 */     this.remoteRunAllowed = remoteRunAllowed;
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy(boolean hasToReload) throws Exception
/*     */   {
/* 124 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$RemoteStrategyType[getStrategyType().ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/* 127 */       throw new IllegalStateException("Strategy is not downloadable.");
/*     */     case 3:
/*     */     case 4:
/*     */     }
/* 131 */     return super.getStrategy(hasToReload);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.RemoteStrategyWrapper
 * JD-Core Version:    0.6.0
 */