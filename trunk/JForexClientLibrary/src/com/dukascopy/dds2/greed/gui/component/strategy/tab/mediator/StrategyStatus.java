/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator;
/*    */ 
/*    */ public enum StrategyStatus
/*    */ {
/*  5 */   STARTING("Starting"), 
/*  6 */   INITIALIZING("Initializing"), 
/*  7 */   RUNNING("Running"), 
/*  8 */   STOPPED("Stopped");
/*    */ 
/*    */   private String name;
/*    */ 
/* 13 */   private StrategyStatus(String name) { this.name = name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 18 */     return this.name;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus
 * JD-Core Version:    0.6.0
 */