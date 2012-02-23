/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.IContext;
/*    */ import com.dukascopy.api.IDownloadableStrategies;
/*    */ import com.dukascopy.api.IDownloadableStrategy;
/*    */ import com.dukascopy.api.IDownloadableStrategy.ComponentType;
/*    */ import com.dukascopy.api.IEngine.StrategyMode;
/*    */ import com.dukascopy.api.JFException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class DownloadableStrategies
/*    */   implements IDownloadableStrategies
/*    */ {
/*    */   public IDownloadableStrategy init(String id, String name, IContext context, IDownloadableStrategy.ComponentType type, IEngine.StrategyMode mode, Map<String, Object> configurables)
/*    */     throws JFException
/*    */   {
/* 17 */     DownloadableStrategy strategy = new DownloadableStrategy(id, name, context, type, mode, configurables);
/* 18 */     strategy.start();
/* 19 */     return strategy;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.DownloadableStrategies
 * JD-Core Version:    0.6.0
 */