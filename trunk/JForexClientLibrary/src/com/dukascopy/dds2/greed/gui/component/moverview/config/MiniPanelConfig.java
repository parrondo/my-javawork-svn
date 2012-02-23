/*    */ package com.dukascopy.dds2.greed.gui.component.moverview.config;
/*    */ 
/*    */ public class MiniPanelConfig
/*    */ {
/*    */   private String instrument;
/*    */ 
/*    */   private MiniPanelConfig()
/*    */   {
/*    */   }
/*    */ 
/*    */   private MiniPanelConfig(String instrument)
/*    */   {
/* 10 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public static MiniPanelConfig getConfig(String instrument) {
/* 14 */     return new MiniPanelConfig(instrument);
/*    */   }
/*    */ 
/*    */   public static MiniPanelConfig getConfig() {
/* 18 */     return new MiniPanelConfig();
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 22 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 26 */     this.instrument = instrument;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.config.MiniPanelConfig
 * JD-Core Version:    0.6.0
 */