/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator;
/*    */ 
/*    */ public enum StrategyType
/*    */ {
/*  9 */   LOCAL("Local Run"), REMOTE("Remote Run");
/*    */ 
/*    */   private String title;
/*    */ 
/* 14 */   private StrategyType(String title) { this.title = title;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 19 */     return this.title;
/*    */   }
/*    */ 
/*    */   public static StrategyType getByName(String name) {
/* 23 */     for (StrategyType strategyType : values()) {
/* 24 */       if (strategyType.name().equals(name)) {
/* 25 */         return strategyType;
/*    */       }
/*    */     }
/*    */ 
/* 29 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType
 * JD-Core Version:    0.6.0
 */