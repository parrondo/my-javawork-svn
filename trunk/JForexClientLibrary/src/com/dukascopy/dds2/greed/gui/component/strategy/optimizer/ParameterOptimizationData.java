/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class ParameterOptimizationData
/*    */ {
/*    */   private HashMap<String, Variable[]> parameters;
/*    */   private double dropDown;
/*    */ 
/*    */   public ParameterOptimizationData(double dropDown)
/*    */   {
/* 17 */     this.parameters = null;
/* 18 */     this.dropDown = dropDown;
/*    */   }
/*    */ 
/*    */   public ParameterOptimizationData(HashMap<String, Variable[]> parameters, double dropDown) {
/* 22 */     this.parameters = parameters;
/* 23 */     this.dropDown = dropDown;
/*    */   }
/*    */ 
/*    */   public HashMap<String, Variable[]> getParameters()
/*    */   {
/* 28 */     return this.parameters;
/*    */   }
/*    */ 
/*    */   public double getDropDown()
/*    */   {
/* 33 */     return this.dropDown;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizationData
 * JD-Core Version:    0.6.0
 */