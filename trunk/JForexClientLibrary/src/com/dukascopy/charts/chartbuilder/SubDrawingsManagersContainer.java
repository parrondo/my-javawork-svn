/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.charts.drawings.ISubDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.NullSubDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.drawings.SubDrawingsManagerImpl;
/*     */ import com.dukascopy.charts.drawings.TextChartObject;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import java.awt.Point;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SubDrawingsManagersContainer
/*     */   implements ISubDrawingsManager
/*     */ {
/*     */   final Collection<SubDrawingsManagerImpl> subDrawingsManagers;
/*  14 */   final NullSubDrawingsManagerImpl nullSubDrawingsManagerImpl = new NullSubDrawingsManagerImpl();
/*     */ 
/*     */   public SubDrawingsManagersContainer(Collection<SubDrawingsManagerImpl> subDrawingsManagers) {
/*  17 */     this.subDrawingsManagers = subDrawingsManagers;
/*     */   }
/*     */ 
/*     */   private ISubDrawingsManager getCurrentSubDrawingsManager()
/*     */   {
/*  23 */     Iterator drawingsManagerIterator = this.subDrawingsManagers.iterator();
/*  24 */     if (drawingsManagerIterator.hasNext()) {
/*  25 */       return (ISubDrawingsManager)drawingsManagerIterator.next();
/*     */     }
/*  27 */     return this.nullSubDrawingsManagerImpl;
/*     */   }
/*     */ 
/*     */   public IChartObject getNewChartObject()
/*     */   {
/*  34 */     return this.nullSubDrawingsManagerImpl.getNewChartObject();
/*     */   }
/*     */ 
/*     */   public ChartObject getEditedChartObject() {
/*  38 */     return getCurrentSubDrawingsManager().getEditedChartObject();
/*     */   }
/*     */ 
/*     */   public ChartObject getHighlightedChartObject() {
/*  42 */     return getCurrentSubDrawingsManager().getHighlightedChartObject();
/*     */   }
/*     */ 
/*     */   public boolean isEditingDrawing() {
/*  46 */     return getCurrentSubDrawingsManager().isEditingDrawing();
/*     */   }
/*     */ 
/*     */   public void drawingNew(IChartObject chartObject)
/*     */   {
/*  51 */     getCurrentSubDrawingsManager().drawingNew(chartObject);
/*     */   }
/*     */ 
/*     */   public boolean isDrawingNew()
/*     */   {
/*  57 */     return getCurrentSubDrawingsManager().isDrawingNew();
/*     */   }
/*     */ 
/*     */   public void finishDrawingNewDrawing(ChartObject newChartObject)
/*     */   {
/*  63 */     getCurrentSubDrawingsManager().finishDrawingNewDrawing(newChartObject);
/*     */   }
/*     */ 
/*     */   public boolean addNewPointToNewDrawing(ChartObject newChartObject, Point point)
/*     */   {
/*  68 */     return getCurrentSubDrawingsManager().addNewPointToNewDrawing(newChartObject, point);
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(ChartObject newChartObject, Point point, boolean repaintWindow)
/*     */   {
/*  73 */     getCurrentSubDrawingsManager().modifyNewDrawing(newChartObject, point, repaintWindow);
/*     */   }
/*     */ 
/*     */   public void modifyEditingDrawing(Point point) {
/*  77 */     getCurrentSubDrawingsManager().modifyEditingDrawing(point);
/*     */   }
/*     */ 
/*     */   public void updatePrevPointAndSelectedHandler(Point point) {
/*  81 */     getCurrentSubDrawingsManager().updatePrevPointAndSelectedHandler(point);
/*     */   }
/*     */ 
/*     */   public boolean isSomeDrawingHighlighted() {
/*  85 */     return getCurrentSubDrawingsManager().isSomeDrawingHighlighted();
/*     */   }
/*     */ 
/*     */   public boolean triggerHighlighting(Point point) {
/*  89 */     return getCurrentSubDrawingsManager().triggerHighlighting(point);
/*     */   }
/*     */ 
/*     */   public void selectHighlitedDrawing() {
/*  93 */     getCurrentSubDrawingsManager().selectHighlitedDrawing();
/*     */   }
/*     */ 
/*     */   public boolean selectDrawingToBeEditedAndStartEditingDrawing(Point point) {
/*  97 */     return getCurrentSubDrawingsManager().selectDrawingToBeEditedAndStartEditingDrawing(point);
/*     */   }
/*     */ 
/*     */   public void selectDrawingToBeEdited(Point point) {
/* 101 */     getCurrentSubDrawingsManager().selectDrawingToBeEdited(point);
/*     */   }
/*     */ 
/*     */   public void selectDrawing(IChartObject chartObject) {
/* 105 */     getCurrentSubDrawingsManager().selectDrawing(chartObject);
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEdited() {
/* 109 */     getCurrentSubDrawingsManager().unselectDrawingToBeEdited();
/*     */   }
/*     */ 
/*     */   public void unselectDrawingToBeEditedAndExitEditingMode() {
/* 113 */     getCurrentSubDrawingsManager().unselectDrawingToBeEditedAndExitEditingMode();
/*     */   }
/*     */ 
/*     */   public boolean intersectsDrawingToBeEdited(Point point) {
/* 117 */     return getCurrentSubDrawingsManager().intersectsDrawingToBeEdited(point);
/*     */   }
/*     */ 
/*     */   public boolean intersectsDrawing(Point point) {
/* 121 */     return getCurrentSubDrawingsManager().intersectsDrawing(point);
/*     */   }
/*     */ 
/*     */   public void deleteSelectedDrawing() {
/* 125 */     getCurrentSubDrawingsManager().deleteSelectedDrawing();
/*     */   }
/*     */ 
/*     */   public boolean updateTextChartObject(TextChartObject textChartObject) {
/* 129 */     return getCurrentSubDrawingsManager().updateTextChartObject(textChartObject);
/*     */   }
/*     */ 
/*     */   public void addChartObjects(List<IChartObject> chartObjects) {
/* 133 */     getCurrentSubDrawingsManager().addChartObjects(chartObjects);
/*     */   }
/*     */ 
/*     */   public void addChartObject(IChartObject iChartObject) {
/* 137 */     getCurrentSubDrawingsManager().addChartObject(iChartObject);
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject) {
/* 141 */     getCurrentSubDrawingsManager().remove(chartObject);
/*     */   }
/*     */ 
/*     */   public void remove(List<IChartObject> chartObjects) {
/* 145 */     getCurrentSubDrawingsManager().remove(chartObjects);
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingLeft() {
/* 149 */     getCurrentSubDrawingsManager().moveEditedDrawingLeft();
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingRight() {
/* 153 */     getCurrentSubDrawingsManager().moveEditedDrawingRight();
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingDown() {
/* 157 */     getCurrentSubDrawingsManager().moveEditedDrawingDown();
/*     */   }
/*     */ 
/*     */   public void moveEditedDrawingUp() {
/* 161 */     getCurrentSubDrawingsManager().moveEditedDrawingUp();
/*     */   }
/*     */ 
/*     */   public void mouseWheelUp() {
/* 165 */     getCurrentSubDrawingsManager().mouseWheelUp();
/*     */   }
/*     */ 
/*     */   public void mouseWheelDown() {
/* 169 */     getCurrentSubDrawingsManager().mouseWheelDown();
/*     */   }
/*     */ 
/*     */   public IChartObject getDrawingByKey(String key)
/*     */   {
/* 174 */     return getCurrentSubDrawingsManager().getDrawingByKey(key);
/*     */   }
/*     */ 
/*     */   public ChartsActionListenerRegistry getChartActionListenerRegistry()
/*     */   {
/* 181 */     return getCurrentSubDrawingsManager().getChartActionListenerRegistry();
/*     */   }
/*     */ 
/*     */   public void updateChartObjectPricesManualy(ChartObject chartObject)
/*     */   {
/* 188 */     getCurrentSubDrawingsManager().updateChartObjectPricesManualy(chartObject);
/*     */   }
/*     */ 
/*     */   public void updatePipsPerBarOption(ChartObject chartObject)
/*     */   {
/* 195 */     getCurrentSubDrawingsManager().updatePipsPerBarOption(chartObject);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.SubDrawingsManagersContainer
 * JD-Core Version:    0.6.0
 */