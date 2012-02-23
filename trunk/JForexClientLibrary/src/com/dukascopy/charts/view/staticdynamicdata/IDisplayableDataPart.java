/*    */ package com.dukascopy.charts.view.staticdynamicdata;
/*    */ 
/*    */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*    */ 
/*    */ public abstract interface IDisplayableDataPart extends IDrawingStrategy
/*    */ {
/*    */   public static enum TYPE
/*    */   {
/*  8 */     BACKGROUND, 
/*  9 */     GRID, 
/* 10 */     PERIOD_SEPARATORS, 
/* 11 */     RAW_DATA, 
/*    */ 
/* 13 */     DRAWINGS, 
/* 14 */     ORDERS, 
/* 15 */     INDICATORS, 
/* 16 */     NEWDRAWINGS, 
/* 17 */     EDITEDDRAWINGS, 
/* 18 */     DYNAMICDRAWINGS, 
/* 19 */     DRAWINGSHANDLERS, 
/* 20 */     SELECTEDORDERS, 
/*    */ 
/* 22 */     META_DRAWINGS, 
/* 23 */     INFO_DRAWINGS, 
/* 24 */     MOUSE_CURSOR, 
/*    */ 
/* 26 */     MOVEABLELABEL, 
/* 27 */     INDICATOR_VALUE;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart
 * JD-Core Version:    0.6.0
 */