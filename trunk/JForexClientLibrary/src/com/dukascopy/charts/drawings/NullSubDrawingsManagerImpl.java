/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import java.awt.Point;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class NullSubDrawingsManagerImpl
/*     */   implements ISubDrawingsManager
/*     */ {
/*     */   public void drawingNew(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public IChartObject getNewChartObject()
/*     */   {
/*  18 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isDrawingNew()
/*     */   {
/*  23 */     return false;
/*     */   }
/*     */ 
/*     */   public ChartObject getEditedChartObject() {
/*  27 */     return null;
/*     */   }
/*     */ 
/*     */   public ChartObject getHighlightedChartObject() {
/*  31 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isEditingDrawing() {
/*  35 */     return false;
/*     */   }
/*     */ 
/*     */   public void finishDrawingNewDrawing(ChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean addNewPointToNewDrawing(ChartObject newChartObject, Point point) {
/*  43 */     return true;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(ChartObject newChartObject, Point point, boolean repaintWindow)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void modifyEditingDrawing(Point point)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void updatePrevPointAndSelectedHandler(Point point)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isSomeDrawingHighlighted() {
/*  59 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean triggerHighlighting(Point point) {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public void selectHighlitedDrawing()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean selectDrawingToBeEditedAndStartEditingDrawing(Point point) {
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   public void selectDrawingToBeEdited(Point point)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void selectDrawing(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEditedAndExitEditingMode()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean intersectsDrawingToBeEdited(Point point) {
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean intersectsDrawing(Point point) {
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */   public void deleteSelectedDrawing()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean updateTextChartObject(TextChartObject textChartObject) {
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */   public void addChartObjects(List<IChartObject> chartObjects)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addChartObject(IChartObject iChartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void remove(List<IChartObject> chartObjects)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingLeft()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingRight()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingDown()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingUp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseWheelUp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseWheelDown()
/*     */   {
/*     */   }
/*     */ 
/*     */   public IChartObject getDrawingByKey(String key)
/*     */   {
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public ChartsActionListenerRegistry getChartActionListenerRegistry()
/*     */   {
/* 153 */     return null;
/*     */   }
/*     */ 
/*     */   public void updateChartObjectPricesManualy(ChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void updatePipsPerBarOption(ChartObject chartObject)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.NullSubDrawingsManagerImpl
 * JD-Core Version:    0.6.0
 */