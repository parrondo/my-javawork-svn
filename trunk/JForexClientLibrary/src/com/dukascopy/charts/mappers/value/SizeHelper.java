/*    */ package com.dukascopy.charts.mappers.value;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public final class SizeHelper
/*    */ {
/*    */   int width;
/*    */   int height;
/* 13 */   final Map<Integer, Integer> subHeights = new HashMap();
/*    */ 
/*    */   public void componentSizeChanged(int width, int height) {
/* 16 */     this.width = width;
/* 17 */     this.height = height;
/*    */   }
/*    */ 
/*    */   public void subComponentHeightChanged(IndicatorWrapper indicatorWrapper, int height) {
/* 21 */     this.subHeights.put(Integer.valueOf(indicatorWrapper.getId()), Integer.valueOf(height));
/*    */   }
/*    */ 
/*    */   public void subComponentAdded(IndicatorWrapper indicatorWrapper) {
/* 25 */     this.subHeights.put(Integer.valueOf(indicatorWrapper.getId()), Integer.valueOf(1));
/*    */   }
/*    */ 
/*    */   public void subComponentDeleted(IndicatorWrapper indicatorWrapper) {
/* 29 */     this.subHeights.remove(Integer.valueOf(indicatorWrapper.getId()));
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 33 */     return this.height;
/*    */   }
/*    */ 
/*    */   public int getWidth() {
/* 37 */     return this.width;
/*    */   }
/*    */ 
/*    */   public Integer getSubHeight(Integer id) {
/* 41 */     return (Integer)this.subHeights.get(id);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.value.SizeHelper
 * JD-Core Version:    0.6.0
 */