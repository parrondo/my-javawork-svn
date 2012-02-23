/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IStrategies;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.JFException;
/*     */ import java.io.File;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategiesControl
/*     */   implements IStrategies
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategiesControl.class);
/*     */   private ISystemListenerExtended systemListener;
/*  29 */   private Set<Long> startedStrategies = new HashSet();
/*     */   private StrategyProcessor strategyProcessor;
/*     */ 
/*     */   public StrategiesControl(ISystemListenerExtended systemListener, StrategyProcessor strategyProcessor)
/*     */   {
/*  33 */     this.systemListener = systemListener;
/*  34 */     this.strategyProcessor = strategyProcessor;
/*     */   }
/*     */ 
/*     */   public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*     */   {
/*  39 */     if (!this.strategyProcessor.isFullAccessGranted())
/*  40 */       throw new JFException("Starting strategy from the separate file requires full access. See @RequiresFullAccess annotation");
/*     */     Long strategyId;
/*     */     try {
/*  44 */       strategyId = (Long)AccessController.doPrivileged(new PrivilegedExceptionAction(jfxFile, listener, configurables, fullAccess) {
/*     */         public Long run() throws Exception {
/*  46 */           return Long.valueOf(StrategiesControl.this.systemListener.startStrategy(this.val$jfxFile, new StrategiesControl.ListenerWrapper(StrategiesControl.this, this.val$listener, null), this.val$configurables, this.val$fullAccess));
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/*  50 */       LOGGER.error(e.getMessage(), e);
/*  51 */       throw ((JFException)e.getException());
/*     */     }
/*  53 */     return strategyId.longValue();
/*     */   }
/*     */ 
/*     */   public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException
/*     */   {
/*  58 */     if ((fullAccess) && (!this.strategyProcessor.isFullAccessGranted()))
/*  59 */       throw new JFException("Strategy doesn't have full access permission to start another strategy with full access permission granted");
/*     */     Long strategyId;
/*     */     try {
/*  63 */       strategyId = (Long)AccessController.doPrivileged(new PrivilegedExceptionAction(strategy, listener, fullAccess) {
/*     */         public Long run() throws Exception {
/*  65 */           return Long.valueOf(StrategiesControl.this.systemListener.startStrategy(this.val$strategy, new StrategiesControl.ListenerWrapper(StrategiesControl.this, this.val$listener, null), this.val$fullAccess));
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/*  69 */       LOGGER.error(e.getMessage(), e);
/*  70 */       throw ((JFException)e.getException());
/*     */     }
/*  72 */     return strategyId.longValue();
/*     */   }
/*     */ 
/*     */   public void stopStrategy(long strategyId) throws JFException
/*     */   {
/*  77 */     if ((this.startedStrategies.contains(Long.valueOf(strategyId))) || (this.strategyProcessor.isFullAccessGranted()))
/*     */       try {
/*  79 */         AccessController.doPrivileged(new PrivilegedExceptionAction(strategyId) {
/*     */           public Object run() throws Exception {
/*  81 */             StrategiesControl.this.systemListener.stopStrategy(this.val$strategyId);
/*  82 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException e) {
/*  86 */         LOGGER.error(e.getMessage(), e);
/*  87 */         throw ((JFException)e.getException());
/*     */       }
/*     */   }
/*     */ 
/*     */   public void stopAll()
/*     */     throws JFException
/*     */   {
/*  97 */     if (!this.strategyProcessor.isFullAccessGranted())
/*  98 */       throw new JFException("Strategy doesn't have full access permission to stop all running strategies.");
/*     */     try
/*     */     {
/* 101 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Object run() throws Exception {
/* 103 */           for (Long strategyId : new HashSet(StrategiesControl.this.startedStrategies)) {
/* 104 */             StrategiesControl.this.systemListener.stopStrategy(strategyId.longValue());
/*     */           }
/* 106 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 110 */       LOGGER.error(e.getMessage(), e);
/* 111 */       throw ((JFException)e.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ListenerWrapper implements IStrategyListener
/*     */   {
/*     */     private IStrategyListener listener;
/*     */ 
/*     */     private ListenerWrapper(IStrategyListener listener)
/*     */     {
/* 122 */       this.listener = listener;
/*     */     }
/*     */ 
/*     */     public void onStart(long strategyId)
/*     */     {
/* 127 */       StrategiesControl.this.startedStrategies.add(Long.valueOf(strategyId));
/* 128 */       this.listener.onStart(strategyId);
/*     */     }
/*     */ 
/*     */     public void onStop(long strategyId)
/*     */     {
/* 133 */       StrategiesControl.this.startedStrategies.remove(Long.valueOf(strategyId));
/* 134 */       this.listener.onStop(strategyId);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.StrategiesControl
 * JD-Core Version:    0.6.0
 */