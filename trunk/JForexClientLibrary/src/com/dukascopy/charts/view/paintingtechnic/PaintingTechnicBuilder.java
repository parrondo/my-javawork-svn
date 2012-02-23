/*    */ package com.dukascopy.charts.view.paintingtechnic;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.StaticDynamicDataManager;
/*    */ import com.dukascopy.charts.view.swing.PaintingTechnic;
/*    */ 
/*    */ public class PaintingTechnicBuilder
/*    */ {
/*    */   final StaticDynamicDataManager staticDynamicDataManager;
/*    */   final InvalidateContentListenerRegistry invalidateContentListenerRegistry;
/*    */ 
/*    */   public PaintingTechnicBuilder(StaticDynamicDataManager staticDynamicDataManager, InvalidateContentListenerRegistry invalidateContentListenerRegistry)
/*    */   {
/* 16 */     this.staticDynamicDataManager = staticDynamicDataManager;
/* 17 */     this.invalidateContentListenerRegistry = invalidateContentListenerRegistry;
/*    */   }
/*    */ 
/*    */   public PaintingTechnic createPaintingTechnic(InvalidationContent contentToBeInvalidated) {
/* 21 */     VolatilePaintingTechnic paintingTechnic = new VolatilePaintingTechnic(contentToBeInvalidated, this.staticDynamicDataManager.createStaticDynamicDataFor(contentToBeInvalidated));
/* 22 */     this.invalidateContentListenerRegistry.registerMainWindowListener(paintingTechnic);
/* 23 */     return paintingTechnic;
/*    */   }
/*    */ 
/*    */   public PaintingTechnic createPaintingTechnicForSubPanel(InvalidationContent contentToBeInvalidated, SubIndicatorGroup subIndicatorGroup) {
/* 27 */     VolatilePaintingTechnic paintingTechnic = new VolatilePaintingTechnic(contentToBeInvalidated, this.staticDynamicDataManager.createStaticDynamicDataForSubPanel(contentToBeInvalidated, subIndicatorGroup));
/* 28 */     this.invalidateContentListenerRegistry.registerSubWindowListener(paintingTechnic, subIndicatorGroup.getSubWindowId());
/* 29 */     return paintingTechnic;
/*    */   }
/*    */ 
/*    */   public void deletePaintingTechnicForSubPanel(int subWindowId) {
/* 33 */     this.invalidateContentListenerRegistry.unregisterSubListener(subWindowId);
/*    */   }
/*    */ 
/*    */   public void invalidateMainWindowsContent()
/*    */   {
/* 41 */     this.invalidateContentListenerRegistry.invalidateMainWindowsContent();
/*    */   }
/*    */ 
/*    */   public void invalidateSubWindowsContent(int id) {
/* 45 */     this.invalidateContentListenerRegistry.invalidateSubWindowsContent(id);
/*    */   }
/*    */ 
/*    */   public void invalidateAllContent() {
/* 49 */     this.invalidateContentListenerRegistry.invalidateAllContent();
/*    */   }
/*    */ 
/*    */   public static InvalidateContentListenerRegistry createInvalidator()
/*    */   {
/* 54 */     return new InvalidateContentListenerRegistry();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.paintingtechnic.PaintingTechnicBuilder
 * JD-Core Version:    0.6.0
 */