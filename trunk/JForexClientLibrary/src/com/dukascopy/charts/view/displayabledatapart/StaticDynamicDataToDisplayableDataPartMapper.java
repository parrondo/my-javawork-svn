/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory;
/*    */ import com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory.PART;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.view.paintingtechnic.InvalidationContent;
/*    */ import com.dukascopy.charts.view.paintingtechnic.StaticDynamicData;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart.TYPE;
/*    */ 
/*    */ public class StaticDynamicDataToDisplayableDataPartMapper
/*    */ {
/*    */   final IDisplayableDataPartFactory displayableDataPartFactory;
/*    */ 
/*    */   public StaticDynamicDataToDisplayableDataPartMapper(IDisplayableDataPartFactory displayableDataPartFactory)
/*    */   {
/* 15 */     this.displayableDataPartFactory = displayableDataPartFactory;
/*    */   }
/*    */ 
/*    */   public void addDisplayableDataPartTo(StaticDynamicData staticDynamicData, InvalidationContent contentToBeInvalidated) {
/* 19 */     if (contentToBeInvalidated == InvalidationContent.COMMONAXISXPANEL) {
/* 20 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.BACKGROUND, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.BACK_GROUND));
/* 21 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.GRID, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.COMMON_AXISX_PANEL_GRID));
/* 22 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.MOVEABLELABEL, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.COMMON_AXISX_PANEL_MOVABLE_LABEL));
/* 23 */     } else if (contentToBeInvalidated == InvalidationContent.MAINCHARTPANEL) {
/* 24 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.BACKGROUND, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.BACK_GROUND));
/* 25 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.GRID, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_GRID));
/* 26 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.PERIOD_SEPARATORS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_PERIOD_SEPARATORS));
/* 27 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.MOUSE_CURSOR, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_MOUSE_CURSOR));
/* 28 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.RAW_DATA, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_RAW_DATA));
/* 29 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.DRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_DRAWINGS));
/* 30 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.NEWDRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_NEW_DRAWINGS));
/* 31 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.EDITEDDRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_EDITED_DRAWINGS));
/* 32 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.DYNAMICDRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_DYNAMIC_DRAWINGS));
/* 33 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.DRAWINGSHANDLERS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_DRAWINGS_HANDLERS));
/* 34 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.ORDERS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_ORDERS));
/* 35 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.SELECTEDORDERS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_SELECTED_ORDERS));
/* 36 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.INDICATORS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_INDICATORS));
/* 37 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.META_DRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_CHART_PANEL_META_INFO));
/* 38 */     } else if (contentToBeInvalidated == InvalidationContent.MAINAXISYPANEL) {
/* 39 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.BACKGROUND, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.BACK_GROUND));
/* 40 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.GRID, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_AXISY_PANEL_GRID));
/* 41 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.INDICATOR_VALUE, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_AXISY_INDICATOR_VALUE_LABEL));
/* 42 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.MOVEABLELABEL, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.MAIN_AXISY_PANEL_MOVABLE_LABEL));
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addIndicatorDisplayableDataPartTo(SubIndicatorGroup subIndicatorGroup, StaticDynamicData staticDynamicData, InvalidationContent contentToBeInvalidated) {
/* 47 */     if (contentToBeInvalidated == InvalidationContent.SUBCHARTPANEL) {
/* 48 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.BACKGROUND, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.BACK_GROUND));
/* 49 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.GRID, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_GRID, subIndicatorGroup));
/* 50 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.PERIOD_SEPARATORS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_PERIOD_SEPARATORS, subIndicatorGroup));
/* 51 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.MOUSE_CURSOR, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_MOUSE_CURSOR, subIndicatorGroup));
/* 52 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.INDICATORS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_INDICATORS, subIndicatorGroup));
/* 53 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.DRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_DRAWINGS, subIndicatorGroup));
/* 54 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.NEWDRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_NEW_DRAWINGS, subIndicatorGroup));
/* 55 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.EDITEDDRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_EDITED_DRAWINGS, subIndicatorGroup));
/* 56 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.DRAWINGSHANDLERS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_DRAWINGS_HANDLERS, subIndicatorGroup));
/* 57 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.META_DRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_META_INFO, subIndicatorGroup));
/* 58 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.INFO_DRAWINGS, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_CHART_PANEL_INDICATOR_INFO, subIndicatorGroup));
/* 59 */     } else if (contentToBeInvalidated == InvalidationContent.SUBAXISYPANEL) {
/* 60 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.BACKGROUND, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.BACK_GROUND));
/* 61 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.GRID, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_AXISY_PANEL_GRID, subIndicatorGroup));
/* 62 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.INDICATOR_VALUE, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_AXISY_INDICATOR_VALUE_LABEL, subIndicatorGroup));
/* 63 */       staticDynamicData.addDisplayableDataPart(IDisplayableDataPart.TYPE.MOVEABLELABEL, this.displayableDataPartFactory.create(IDisplayableDataPartFactory.PART.SUB_AXISY_PANEL_MOVABLE_LABEL, subIndicatorGroup));
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.StaticDynamicDataToDisplayableDataPartMapper
 * JD-Core Version:    0.6.0
 */