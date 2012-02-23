/*    */ package com.dukascopy.charts.persistence;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class IdManager
/*    */ {
/*  9 */   private Set<Integer> reservedIds = new HashSet();
/* 10 */   private int nextFreeId = 5;
/*    */ 
/* 12 */   private static Set<Integer> reservedIndicatorIds = new HashSet();
/*    */   private static int nextFreeIndicatorId;
/* 15 */   private static Set<Integer> reservedDrawingIds = new HashSet();
/*    */   private static int nextFreeDrawingId;
/* 18 */   private static IdManager instance = null;
/*    */ 
/*    */   public static IdManager getInstance()
/*    */   {
/* 22 */     if (instance == null) {
/* 23 */       instance = new IdManager();
/*    */     }
/* 25 */     return instance;
/*    */   }
/*    */ 
/*    */   private IdManager() {
/* 29 */     this.nextFreeId = 10;
/*    */   }
/*    */   public static void cleanManager() {
/* 32 */     instance = null;
/*    */   }
/*    */ 
/*    */   public IdManager(int initialFreeChartId) {
/* 36 */     this.nextFreeId = initialFreeChartId;
/*    */   }
/*    */ 
/*    */   public synchronized int getNextChartId() {
/* 40 */     while (this.reservedIds.contains(Integer.valueOf(this.nextFreeId))) {
/* 41 */       this.nextFreeId += 1;
/*    */     }
/*    */ 
/* 44 */     return this.nextFreeId++;
/*    */   }
/*    */ 
/*    */   public synchronized void reserveChartId(int chartIdToReserve) {
/* 48 */     this.reservedIds.add(Integer.valueOf(chartIdToReserve));
/*    */   }
/*    */ 
/*    */   public synchronized int getNextServiceId() {
/* 52 */     while (this.reservedIds.contains(Integer.valueOf(this.nextFreeId))) {
/* 53 */       this.nextFreeId += 1;
/*    */     }
/* 55 */     return this.nextFreeId++;
/*    */   }
/*    */ 
/*    */   public synchronized void reserveServiceId(int serviceIdToBeReserved) {
/* 59 */     this.reservedIds.add(Integer.valueOf(serviceIdToBeReserved));
/*    */   }
/*    */ 
/*    */   public static synchronized int getNextIndicatorId() {
/* 63 */     while (reservedIndicatorIds.contains(Integer.valueOf(nextFreeIndicatorId))) {
/* 64 */       nextFreeIndicatorId += 1;
/*    */     }
/* 66 */     return nextFreeIndicatorId++;
/*    */   }
/*    */ 
/*    */   public static synchronized void reserveIndicatorId(int indicatorIdToBeReserved) {
/* 70 */     reservedIndicatorIds.add(Integer.valueOf(indicatorIdToBeReserved));
/* 71 */     if (nextFreeIndicatorId <= indicatorIdToBeReserved)
/* 72 */       nextFreeIndicatorId = indicatorIdToBeReserved + 1;
/*    */   }
/*    */ 
/*    */   public static synchronized int getNextDrawingId()
/*    */   {
/* 77 */     while (reservedDrawingIds.contains(Integer.valueOf(nextFreeDrawingId))) {
/* 78 */       nextFreeDrawingId += 1;
/*    */     }
/* 80 */     return nextFreeDrawingId++;
/*    */   }
/*    */ 
/*    */   public static synchronized void reserveDrawingId(int drawingIdToBeReserved) {
/* 84 */     reservedDrawingIds.add(Integer.valueOf(drawingIdToBeReserved));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.IdManager
 * JD-Core Version:    0.6.0
 */