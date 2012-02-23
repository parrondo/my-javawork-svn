/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.IStrategyListener;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.JFException;
/*    */ import java.io.File;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class SystemListenerAdapter
/*    */   implements ISystemListenerExtended
/*    */ {
/*    */   public void onStart(long processId)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onStop(long processId)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onConnect()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onDisconnect()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void subscribeToInstruments(Set<Instrument> instruments)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Set<Instrument> getSubscribedInstruments()
/*    */   {
/* 48 */     return new HashSet(0);
/*    */   }
/*    */ 
/*    */   public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*    */   {
/* 53 */     return 0L;
/*    */   }
/*    */ 
/*    */   public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException
/*    */   {
/* 58 */     return 0L;
/*    */   }
/*    */ 
/*    */   public void stopStrategy(long strategyId)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.SystemListenerAdapter
 * JD-Core Version:    0.6.0
 */