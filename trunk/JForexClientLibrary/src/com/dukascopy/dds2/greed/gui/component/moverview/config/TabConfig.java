/*    */ package com.dukascopy.dds2.greed.gui.component.moverview.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TabConfig
/*    */ {
/*    */   private String tabName;
/* 10 */   private boolean lastActive = false;
/*    */   private int index;
/* 13 */   private List<MiniPanelConfig> instrumentList = new ArrayList();
/*    */ 
/*    */   private TabConfig()
/*    */   {
/*    */   }
/*    */ 
/*    */   private TabConfig(String tabName, boolean lastActive) {
/* 20 */     this.tabName = tabName;
/* 21 */     this.lastActive = lastActive;
/*    */   }
/*    */ 
/*    */   private TabConfig(String tabName, boolean lastActive, int tabIndex)
/*    */   {
/* 28 */     this(tabName, lastActive);
/* 29 */     this.index = tabIndex;
/*    */   }
/*    */ 
/*    */   public static TabConfig getConfig(String tabName, boolean lastActive)
/*    */   {
/* 36 */     return new TabConfig(tabName, lastActive);
/*    */   }
/*    */ 
/*    */   public static TabConfig getConfig()
/*    */   {
/* 41 */     return new TabConfig();
/*    */   }
/*    */ 
/*    */   public void addMiniPanelConfig(MiniPanelConfig mpc) {
/* 45 */     if (this.instrumentList == null) throw new NullPointerException("instrumentList == null");
/* 46 */     this.instrumentList.add(mpc);
/*    */   }
/*    */ 
/*    */   public String getTabName() {
/* 50 */     return this.tabName;
/*    */   }
/*    */   public void setTabName(String tabName) {
/* 53 */     this.tabName = tabName;
/*    */   }
/*    */   public boolean isLastActive() {
/* 56 */     return this.lastActive;
/*    */   }
/*    */   public void setLastActive(boolean lastActive) {
/* 59 */     this.lastActive = lastActive;
/*    */   }
/*    */   public List<MiniPanelConfig> getInstrumentList() {
/* 62 */     return this.instrumentList;
/*    */   }
/*    */   public void setInstrumentList(List<MiniPanelConfig> instrumentList) {
/* 65 */     this.instrumentList = instrumentList;
/*    */   }
/*    */   public int getIndex() {
/* 68 */     return this.index;
/*    */   }
/*    */   public void setIndex(int index) {
/* 71 */     this.index = index;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.config.TabConfig
 * JD-Core Version:    0.6.0
 */