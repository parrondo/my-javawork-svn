/*    */ package com.dukascopy.charts.view.staticdynamicdata;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.chartbuilder.ViewModeChangeListenerRegistry;
/*    */ import com.dukascopy.charts.view.displayabledatapart.StaticDynamicDataToDisplayableDataPartMapper;
/*    */ import com.dukascopy.charts.view.paintingtechnic.InvalidationContent;
/*    */ import com.dukascopy.charts.view.paintingtechnic.StaticDynamicData;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class StaticDynamicDataManager
/*    */ {
/* 24 */   final Map<InvalidationContent, StaticDynamicData> staticDynamicDatas = new EnumMap(InvalidationContent.class);
/*    */   final ViewModeChangeListenerRegistry viewModeChangeListenerRegistry;
/*    */   final StaticDynamicDataToDisplayableDataPartMapper staticDynamicDataToDisplayableDataPartMapper;
/* 86 */   StaticDynamicData nullStaticDynamicData = new StaticDynamicData() {
/*    */     private static final String MESSAGE = "This component couldn't be initialized... See stacktrace for more info.";
/*    */ 
/* 90 */     public void drawDynamicData(Graphics g, JComponent jComponent) { int widthMiddle = jComponent.getWidth() / 2;
/* 91 */       int heightMiddle = jComponent.getHeight() / 2;
/* 92 */       int strWidth = g.getFontMetrics().stringWidth("This component couldn't be initialized... See stacktrace for more info.");
/* 93 */       g.drawString("This component couldn't be initialized... See stacktrace for more info.", widthMiddle - strWidth / 2, heightMiddle);
/*    */     }
/*    */ 
/*    */     public void drawStaticData(Graphics g, JComponent jComponent)
/*    */     {
/*    */     }
/*    */ 
/*    */     public void addDisplayableDataPart(IDisplayableDataPart.TYPE displayableDataPartType, IDisplayableDataPart displayableDataPart)
/*    */     {
/*    */     }
/* 86 */   };
/*    */ 
/*    */   public StaticDynamicDataManager(ViewModeChangeListenerRegistry viewModeChangeListenerRegistry, StaticDynamicDataToDisplayableDataPartMapper staticDynamicDataToDisplayableDataPartMapper)
/*    */   {
/* 30 */     this.viewModeChangeListenerRegistry = viewModeChangeListenerRegistry;
/* 31 */     this.staticDynamicDataToDisplayableDataPartMapper = staticDynamicDataToDisplayableDataPartMapper;
/*    */   }
/*    */ 
/*    */   public StaticDynamicData createStaticDynamicDataFor(InvalidationContent contentToBeInvalidated)
/*    */   {
/* 40 */     StaticDynamicData staticDynamicData = (StaticDynamicData)this.staticDynamicDatas.get(contentToBeInvalidated);
/* 41 */     if (staticDynamicData != null)
/* 42 */       return staticDynamicData;
/*    */     Object instance;
/* 47 */     if (InvalidationContent.COMMONAXISXPANEL == contentToBeInvalidated) {
/* 48 */       instance = new CommonAxisXPanelStaticDynamicData();
/*    */     }
/*    */     else
/*    */     {
/*    */       Object instance;
/* 49 */       if (InvalidationContent.MAINAXISYPANEL == contentToBeInvalidated) {
/* 50 */         instance = new MainAxisYPanelStaticDynamicData();
/*    */       }
/*    */       else
/*    */       {
/*    */         Object instance;
/* 51 */         if (InvalidationContent.MAINCHARTPANEL == contentToBeInvalidated)
/* 52 */           instance = new MainChartPanelStaticDynamicData();
/*    */         else
/* 54 */           return this.nullStaticDynamicData;
/*    */       }
/*    */     }
/*    */     Object instance;
/* 57 */     this.viewModeChangeListenerRegistry.registerViewModeChangeListener((ViewModeChangeListener)instance);
/* 58 */     staticDynamicData = (StaticDynamicData)instance;
/* 59 */     this.staticDynamicDataToDisplayableDataPartMapper.addDisplayableDataPartTo(staticDynamicData, contentToBeInvalidated);
/* 60 */     return staticDynamicData;
/*    */   }
/*    */ 
/*    */   public StaticDynamicData createStaticDynamicDataForSubPanel(InvalidationContent contentToBeInvalidated, SubIndicatorGroup subIndicatorGroup) {
/* 64 */     StaticDynamicData staticDynamicData = (StaticDynamicData)this.staticDynamicDatas.get(contentToBeInvalidated);
/*    */ 
/* 66 */     if (staticDynamicData != null)
/* 67 */       return staticDynamicData;
/*    */     AbstractStaticDynamicData instance;
/* 72 */     if (InvalidationContent.SUBCHARTPANEL == contentToBeInvalidated) {
/* 73 */       instance = new SubChartPanelStaticDynamicData();
/*    */     }
/*    */     else
/*    */     {
/*    */       AbstractStaticDynamicData instance;
/* 74 */       if (InvalidationContent.SUBAXISYPANEL == contentToBeInvalidated)
/* 75 */         instance = new SubAxisYPanelStaticDynamicData();
/*    */       else
/* 77 */         return this.nullStaticDynamicData;
/*    */     }
/*    */     AbstractStaticDynamicData instance;
/* 80 */     this.viewModeChangeListenerRegistry.registerViewModeChangeListener(instance);
/* 81 */     staticDynamicData = instance;
/* 82 */     this.staticDynamicDataToDisplayableDataPartMapper.addIndicatorDisplayableDataPartTo(subIndicatorGroup, staticDynamicData, contentToBeInvalidated);
/* 83 */     return staticDynamicData;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.StaticDynamicDataManager
 * JD-Core Version:    0.6.0
 */