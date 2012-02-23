/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ public enum FramesState
/*    */ {
/* 10 */   CUSTOM("item.custom.mode"), 
/* 11 */   ORDERED("item.tile.ordered"), 
/* 12 */   HORIZONTAL("item.tile.horizontal"), 
/* 13 */   VERTICAL("item.tile.vertical");
/*    */ 
/*    */   private String label;
/*    */ 
/* 18 */   private FramesState(String label) { this.label = label; }
/*    */ 
/*    */   public String getLabel()
/*    */   {
/* 22 */     return this.label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.FramesState
 * JD-Core Version:    0.6.0
 */