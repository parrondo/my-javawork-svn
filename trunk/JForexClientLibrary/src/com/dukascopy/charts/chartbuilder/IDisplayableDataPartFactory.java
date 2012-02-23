/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart;
/*    */ 
/*    */ public abstract interface IDisplayableDataPartFactory
/*    */ {
/*    */   public abstract IDisplayableDataPart create(PART paramPART);
/*    */ 
/*    */   public abstract IDisplayableDataPart create(PART paramPART, SubIndicatorGroup paramSubIndicatorGroup);
/*    */ 
/*    */   public static enum PART
/*    */   {
/*  8 */     BACK_GROUND, 
/*    */ 
/* 10 */     COMMON_AXISX_PANEL_GRID, 
/* 11 */     COMMON_AXISX_PANEL_MOVABLE_LABEL, 
/*    */ 
/* 13 */     MAIN_CHART_PANEL_GRID, 
/* 14 */     MAIN_CHART_PANEL_PERIOD_SEPARATORS, 
/* 15 */     MAIN_CHART_PANEL_MOUSE_CURSOR, 
/* 16 */     MAIN_CHART_PANEL_INFO, 
/* 17 */     MAIN_CHART_PANEL_RAW_DATA, 
/* 18 */     MAIN_CHART_PANEL_DRAWINGS, 
/* 19 */     MAIN_CHART_PANEL_NEW_DRAWINGS, 
/* 20 */     MAIN_CHART_PANEL_EDITED_DRAWINGS, 
/* 21 */     MAIN_CHART_PANEL_DYNAMIC_DRAWINGS, 
/* 22 */     MAIN_CHART_PANEL_DRAWINGS_HANDLERS, 
/* 23 */     MAIN_CHART_PANEL_ORDERS, 
/* 24 */     MAIN_CHART_PANEL_SELECTED_ORDERS, 
/* 25 */     MAIN_CHART_PANEL_INDICATORS, 
/* 26 */     MAIN_CHART_PANEL_META_INFO, 
/*    */ 
/* 28 */     MAIN_AXISY_PANEL_GRID, 
/* 29 */     MAIN_AXISY_PANEL_MOVABLE_LABEL, 
/* 30 */     MAIN_AXISY_INDICATOR_VALUE_LABEL, 
/*    */ 
/* 32 */     SUB_CHART_PANEL_GRID, 
/* 33 */     SUB_CHART_PANEL_PERIOD_SEPARATORS, 
/* 34 */     SUB_CHART_PANEL_MOUSE_CURSOR, 
/* 35 */     SUB_CHART_PANEL_INDICATORS, 
/* 36 */     SUB_CHART_PANEL_DRAWINGS, 
/* 37 */     SUB_CHART_PANEL_NEW_DRAWINGS, 
/* 38 */     SUB_CHART_PANEL_EDITED_DRAWINGS, 
/* 39 */     SUB_CHART_PANEL_DRAWINGS_HANDLERS, 
/* 40 */     SUB_CHART_PANEL_META_INFO, 
/* 41 */     SUB_CHART_PANEL_INDICATOR_INFO, 
/* 42 */     SUB_AXISY_PANEL_GRID, 
/* 43 */     SUB_AXISY_PANEL_MOVABLE_LABEL, 
/* 44 */     SUB_AXISY_INDICATOR_VALUE_LABEL;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.IDisplayableDataPartFactory
 * JD-Core Version:    0.6.0
 */