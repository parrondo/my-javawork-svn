/*    */ package com.dukascopy.dds2.greed.agent.strategy;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public final class StrategyParameters
/*    */ {
/* 17 */   private static final HashMap<String, HashMap<String, Variable>> parameters = new HashMap();
/*    */ 
/*    */   public static void putParameters(String strategyName, HashMap<String, Variable> params)
/*    */   {
/* 27 */     if (params != null)
/* 28 */       parameters.put(strategyName, params);
/*    */     else
/* 30 */       parameters.remove(strategyName);
/*    */   }
/*    */ 
/*    */   public static HashMap<String, Variable> getParameters(String strategyName)
/*    */   {
/* 35 */     return (HashMap)parameters.get(strategyName);
/*    */   }
/*    */ 
/*    */   public static boolean areParametersSet(String strategyName) {
/* 39 */     return parameters.containsKey(strategyName);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.StrategyParameters
 * JD-Core Version:    0.6.0
 */