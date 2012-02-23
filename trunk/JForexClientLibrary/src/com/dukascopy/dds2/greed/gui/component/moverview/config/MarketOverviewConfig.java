/*    */ package com.dukascopy.dds2.greed.gui.component.moverview.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MarketOverviewConfig
/*    */ {
/* 11 */   private List<TabConfig> tabs = new ArrayList();
/*    */ 
/*    */   public static MarketOverviewConfig getConfig()
/*    */   {
/* 16 */     return new MarketOverviewConfig();
/*    */   }
/*    */ 
/*    */   public void addTab(TabConfig tabConf)
/*    */   {
/* 21 */     if (this.tabs == null) throw new NullPointerException("tabs == null");
/* 22 */     this.tabs.add(tabConf);
/*    */ 
/* 25 */     Collections.sort(this.tabs, new TabsComparator(null));
/*    */   }
/*    */ 
/*    */   public int getSelectedTabIndex()
/*    */   {
/* 30 */     int i = 0;
/* 31 */     for (TabConfig tabConfig : this.tabs) {
/* 32 */       if (tabConfig.isLastActive()) return i;
/* 33 */       i++;
/*    */     }
/* 35 */     return -1;
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 39 */     this.tabs = new ArrayList();
/*    */   }
/*    */ 
/*    */   public List<TabConfig> getTabs() {
/* 43 */     return this.tabs;
/*    */   }
/*    */ 
/*    */   public void setTabs(List<TabConfig> tabs) {
/* 47 */     this.tabs = tabs;
/*    */   }
/*    */   private class TabsComparator implements Comparator {
/*    */     private TabsComparator() {
/*    */     }
/*    */ 
/*    */     public int compare(Object tab1, Object tab2) {
/* 54 */       Integer tab1index = Integer.valueOf(((TabConfig)tab1).getIndex());
/* 55 */       Integer tab2index = Integer.valueOf(((TabConfig)tab2).getIndex());
/* 56 */       return tab1index.compareTo(tab2index);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig
 * JD-Core Version:    0.6.0
 */