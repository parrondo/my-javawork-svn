/*    */ package com.dukascopy.dds2.router.statistics;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class RouterEmexStatisticsDTO
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 0L;
/*    */   private String routerName;
/* 12 */   private Map<String, EmexStatisticsEntry> emexStatMap = new HashMap();
/*    */ 
/*    */   public String getRouterName() {
/* 15 */     return this.routerName;
/*    */   }
/*    */ 
/*    */   public void setRouterName(String routerName) {
/* 19 */     this.routerName = routerName;
/*    */   }
/*    */ 
/*    */   public Map<String, EmexStatisticsEntry> getEmexStatMap() {
/* 23 */     return this.emexStatMap;
/*    */   }
/*    */ 
/*    */   public synchronized void resetStatistics() {
/* 27 */     this.emexStatMap.clear();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.RouterEmexStatisticsDTO
 * JD-Core Version:    0.6.0
 */