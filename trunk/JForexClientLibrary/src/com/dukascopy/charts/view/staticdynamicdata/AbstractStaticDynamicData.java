/*    */ package com.dukascopy.charts.view.staticdynamicdata;
/*    */ 
/*    */ import com.dukascopy.charts.view.paintingtechnic.StaticDynamicData;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ abstract class AbstractStaticDynamicData
/*    */   implements StaticDynamicData, ViewModeChangeListener
/*    */ {
/* 10 */   protected final Map<IDisplayableDataPart.TYPE, IDisplayableDataPart> displayableDataParts = new EnumMap(IDisplayableDataPart.TYPE.class);
/*    */ 
/*    */   public void addDisplayableDataPart(IDisplayableDataPart.TYPE key, IDisplayableDataPart displayableDataPart) {
/* 13 */     this.displayableDataParts.put(key, displayableDataPart);
/*    */   }
/*    */ 
/*    */   public void viewModeChanged(ViewMode newViewMode)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.AbstractStaticDynamicData
 * JD-Core Version:    0.6.0
 */