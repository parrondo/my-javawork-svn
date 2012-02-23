/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SubIndicatorGroup
/*    */ {
/*    */   final int groupViewId;
/*    */   final List<IndicatorWrapper> subIndicators;
/* 11 */   final Map<Integer, Integer> indicatorIdToGroupId = new HashMap();
/*    */ 
/*    */   public SubIndicatorGroup(int subWindowId) {
/* 14 */     this.groupViewId = subWindowId;
/* 15 */     this.subIndicators = new ArrayList();
/*    */   }
/*    */ 
/*    */   SubIndicatorGroup(int groupViewId, List<IndicatorWrapper> subIndicators)
/*    */   {
/* 20 */     this.groupViewId = groupViewId;
/* 21 */     this.subIndicators = subIndicators;
/* 22 */     for (IndicatorWrapper subIndicator : subIndicators)
/* 23 */       this.indicatorIdToGroupId.put(Integer.valueOf(subIndicator.getId()), Integer.valueOf(groupViewId));
/*    */   }
/*    */ 
/*    */   SubIndicatorGroup(int groupViewId, IndicatorWrapper indicatorWrapper)
/*    */   {
/* 28 */     this.groupViewId = groupViewId;
/* 29 */     this.subIndicators = new ArrayList();
/* 30 */     this.subIndicators.add(indicatorWrapper);
/* 31 */     this.indicatorIdToGroupId.put(Integer.valueOf(indicatorWrapper.getId()), Integer.valueOf(groupViewId));
/*    */   }
/*    */ 
/*    */   public int getSubWindowId() {
/* 35 */     return this.groupViewId;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 39 */     StringBuilder name = new StringBuilder();
/* 40 */     Iterator iterator = this.subIndicators.iterator();
/* 41 */     while (iterator.hasNext()) {
/* 42 */       IndicatorWrapper indicatorWrapper = (IndicatorWrapper)iterator.next();
/* 43 */       name.append(indicatorWrapper.getNameWithParams());
/* 44 */       if (iterator.hasNext()) {
/* 45 */         name.append(", ");
/*    */       }
/*    */     }
/* 48 */     return name.toString();
/*    */   }
/*    */ 
/*    */   public List<IndicatorWrapper> getSubIndicators() {
/* 52 */     return this.subIndicators;
/*    */   }
/*    */ 
/*    */   public IndicatorWrapper getBasicSubIndicator()
/*    */   {
/* 60 */     if ((getSubIndicators() == null) || (getSubIndicators().isEmpty())) {
/* 61 */       return null;
/*    */     }
/* 63 */     return (IndicatorWrapper)getSubIndicators().get(0);
/*    */   }
/*    */ 
/*    */   public void addIndicator(IndicatorWrapper indicator) {
/* 67 */     this.subIndicators.add(indicator);
/*    */   }
/*    */ 
/*    */   public void subIndicatorsDeleted(List<IndicatorWrapper> indicatorsToBeDeleted) {
/* 71 */     this.subIndicators.removeAll(indicatorsToBeDeleted);
/*    */   }
/*    */ 
/*    */   public void subIndicatorDeleted(IndicatorWrapper indicatorWrapper) {
/* 75 */     this.subIndicators.remove(indicatorWrapper);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.SubIndicatorGroup
 * JD-Core Version:    0.6.0
 */